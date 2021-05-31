package com.example.pki.service.impl;

import com.example.pki.exceptions.*;
import com.example.pki.keystore.Keystore;
import com.example.pki.certificate.CertificateGenerator;
import com.example.pki.model.IssuerData;
import com.example.pki.model.OCSPCertificate;
import com.example.pki.model.SubjectData;
import com.example.pki.model.dto.CertificateDto;
import com.example.pki.model.enums.CA;
import com.example.pki.repository.CertificateRepository;
import com.example.pki.service.CertificateService;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class CertificateServiceImpl implements CertificateService {
    private final Keystore keystore = new Keystore();
    private final CertificateRepository certificateRepository;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    @Override
    public void generate(CertificateDto dto) throws CouldNotGenerateKeyPairException, CouldNotGenerateCertificateException, KeystoreErrorException {
        String certificateName = dto.getCertificateName();
        String parentName = dto.getParentName();

        if (certificateRepository.findByFileName(certificateName) != null) {
            throw new CertificateAlreadyExists();
        }
        if (dto.getCa() != CA.Root && !isCertificateCA(parentName)) {
            throw new CertificateIsNotCA();
        }
        if (dto.getCa() != CA.Root && !isCertificateValid(parentName)) {
            throw new CertificateIsNotValid();
        }

        DataKeyPair pair = generateSubjectDataAndKey(dto);
        SubjectData subjectData = pair.subjectData;

        IssuerData issuerData;
        boolean isCA;
        OCSPCertificate issuerOCSP;

        switch (dto.getCa()) {
            case Root:
                issuerData = new IssuerData(pair.privateKey, subjectData.getX500name());
                issuerOCSP = null;
                isCA = true;
                break;

            case Intermediate:
                PrivateKey parentKey = keystore.readPrivateKeyFromPfx(parentName);
                issuerData = generateIssuerData(parentKey, parentName);
                issuerOCSP = certificateRepository.findByFileName(parentName);
                isCA = true;
                break;

            default: // End-entity
                PrivateKey parentKeyEnd = keystore.readPrivateKeyFromPfx(parentName);
                issuerData = generateIssuerData(parentKeyEnd, parentName);
                issuerOCSP = certificateRepository.findByFileName(parentName);
                isCA = false;
        }

        // Generate certificate
        CertificateGenerator certificateGenerator = new CertificateGenerator();
        X509Certificate certificate = certificateGenerator.generateCertificate(subjectData, issuerData, isCA);
        // Save to keystore
        keystore.saveCertAsPfx(certificate, certificateName, issuerData.getPrivateKey());
        // Save OCSP data in database
        saveOCSPData(certificateName, issuerOCSP);
        // Save in pem format to use in front app
        keystore.saveCertAndPrivateKeyPems(certificateName, certificate, pair.privateKey);
    }

    @Override
    public void revoke(String certificateAlias) {
        OCSPCertificate certificate = certificateRepository.findByFileName(certificateAlias);
        certificate.revoke();
        certificateRepository.save(certificate);
    }

    @Override
    public boolean isCertificateValid(String alias) throws KeystoreErrorException {
        OCSPCertificate ocspCertificate = certificateRepository.findByFileName(alias);
        X509Certificate x509Certificate = keystore.readCertificateFromPfx(alias);
        return !ocspCertificate.isRevoked() && !isCertificateExpired(x509Certificate);
    }

    private boolean isCertificateExpired(X509Certificate certificate) {
        Date today = new Date();
        return today.before(certificate.getNotBefore()) || today.after(certificate.getNotAfter());
    }

    private boolean isCertificateCA(String alias) {
        X509Certificate parent = keystore.readCertificateFromPfx(alias);
        return parent.getBasicConstraints() != -1;
    }

    @Override
    public List<CertificateDto> getCertificates(boolean onlyCA) throws KeystoreErrorException {
        List<OCSPCertificate> ocspCertificates = (List<OCSPCertificate>) certificateRepository.findAll();
        List<CertificateDto> certificateDtos = new ArrayList<>();

        ocspCertificates.forEach(ocspCertificate -> {
            String certificateName = ocspCertificate.getFileName();
            X509Certificate certificate = keystore.readCertificateFromPfx(certificateName);

            if (certificate.getBasicConstraints() != -1) {
                CertificateDto certificateDto = createCertificateDto(ocspCertificate, certificate);
                if (ocspCertificate.getIssuer() == null) {
                    certificateDto.setCa(CA.Root);
                    certificateDto.setParentName("Self-signed");
                } else {
                    certificateDto.setCa(CA.Intermediate);
                    certificateDto.setParentName(ocspCertificate.getIssuer().getFileName());
                }
                certificateDtos.add(certificateDto);
            } else if (!onlyCA) {
                CertificateDto certificateDto = createCertificateDto(ocspCertificate, certificate);
                certificateDto.setCa(CA.EndEntity);
                certificateDto.setParentName(ocspCertificate.getIssuer().getFileName());
                certificateDtos.add(certificateDto);
            }
        });

        return certificateDtos;
    }

    private CertificateDto createCertificateDto(OCSPCertificate ocspCertificate, X509Certificate certificate) {
        CertificateDto certificateDto = new CertificateDto();
        certificateDto.setStartDate(certificate.getNotBefore());
        certificateDto.setEndDate(certificate.getNotAfter());
        certificateDto.setCertificateName(ocspCertificate.getFileName());
        certificateDto.setRevoked(ocspCertificate.isRevoked());
        certificateDto.setValid(isCertificateValid(ocspCertificate.getFileName()));
        return certificateDto;
    }

    private KeyPair generateKeyPair() throws CouldNotGenerateKeyPairException {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            // TODO: log error
            throw new CouldNotGenerateKeyPairException();
        }
    }

    private IssuerData generateIssuerData(PrivateKey issuerKey, String parent) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, parent);
        return new IssuerData(issuerKey, builder.build());
    }

    private DataKeyPair generateSubjectDataAndKey(CertificateDto dto) throws CouldNotGenerateKeyPairException {
        KeyPair keyPairSubject = generateKeyPair();

        Date startDate = dto.getStartDate();
        Date endDate = dto.getEndDate();

        Random rand = new Random();
        int n = rand.nextInt(1000000);

        String sn = Integer.toString(n);
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, dto.getCertificateName());
        SubjectData subjectData = new SubjectData(keyPairSubject.getPublic(), builder.build(), sn, startDate, endDate);
        return new DataKeyPair(subjectData, keyPairSubject.getPrivate());
    }

    private void saveOCSPData(String certificateName, OCSPCertificate issuer) {
        OCSPCertificate ocspCertificate = new OCSPCertificate(certificateName, issuer);
        certificateRepository.save(ocspCertificate);
    }

    private class DataKeyPair {
        public SubjectData subjectData;
        public PrivateKey privateKey;

        public DataKeyPair(SubjectData subjectData, PrivateKey privateKey) {
            this.subjectData = subjectData;
            this.privateKey = privateKey;
        }
    }
}
