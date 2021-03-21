package com.example.pki.service.impl;

import Exceptions.CertificateAlreadyExists;
import Exceptions.CertificateIsNotCA;
import Exceptions.CertificateIsNotValid;
import com.example.pki.certificate.CertificateGenerator;
import com.example.pki.keystore.KeyStoreReader;
import com.example.pki.keystore.KeyStoreWriter;
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

    private final String KEY_STORE_PASS = "123";
    private final String PASS = "123";

    private final CertificateRepository certificateRepository;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    // CertName - alias - slace se kroz front
    // keyStorePass - 123
    // pass - 123

    @Override
    public void generateCertificate(CertificateDto dto) {
        CertificateGenerator certificateGenerator = new CertificateGenerator();
        DataKeyPair pair = generateSubjectDataAndKey(dto);
        SubjectData subjectData = pair.subjectData;

        IssuerData issuerData;
        KeyPair keyPairSubject = this.generateKeyPair();
        KeyStoreWriter writer = new KeyStoreWriter();
        KeyStoreReader reader = new KeyStoreReader();

        String certificateName = dto.getCertificateName();
        String parentName = dto.getParentName();

        if (certificateRepository.findByFileName(certificateName) != null) {
            throw new CertificateAlreadyExists();
        }

        System.out.println(dto);

        switch (dto.getCa()) {

            case Root:
                issuerData = generateIssuerData(pair.privateKey, dto);
                X509Certificate certificate = certificateGenerator.generateCertificate(subjectData, issuerData, true);
                writer.loadKeyStore(null, KEY_STORE_PASS.toCharArray());
                writer.write(certificateName, issuerData.getPrivateKey(), PASS.toCharArray(), certificate);
                writer.saveKeyStore("data/" + certificateName, KEY_STORE_PASS.toCharArray());
                OCSPCertificate ocspCertificate = new OCSPCertificate(certificateName, null);
                certificateRepository.save(ocspCertificate);
                break;

            case Intermediate:
                X509Certificate parent = (X509Certificate) reader.readCertificate("data/" + parentName,
                        KEY_STORE_PASS, parentName);
                if (parent.getBasicConstraints() == -1) {
                    throw new CertificateIsNotCA();
                }

                if (!isCertificateValid(parentName)) {
                    throw new CertificateIsNotValid();
                }

                issuerData = generateIssuerData(reader.readPrivateKey("data/" + parentName, KEY_STORE_PASS, parentName, PASS), dto);
                X509Certificate certificateIntermediate = certificateGenerator.generateCertificate(subjectData, issuerData, true);

                writer.loadKeyStore(null, KEY_STORE_PASS.toCharArray());
                writer.write(certificateName, issuerData.getPrivateKey(), PASS.toCharArray(), certificateIntermediate);
                writer.saveKeyStore("data/" + certificateName, KEY_STORE_PASS.toCharArray());

                X509Certificate certificateLoadedIntermediate = (X509Certificate) reader.readCertificate("data/" + certificateName,
                        KEY_STORE_PASS, certificateName);
                System.out.println(certificateLoadedIntermediate.getIssuerX500Principal().getName());

                OCSPCertificate ocspCertificateIntermediate = new OCSPCertificate(certificateName, certificateRepository.findByFileName(parentName));
                certificateRepository.save(ocspCertificateIntermediate);
                break;

            case EndEntity:
                X509Certificate endEntityParent = (X509Certificate) reader.readCertificate("data/" + parentName,
                        KEY_STORE_PASS, parentName);
                if (endEntityParent.getBasicConstraints() == -1) {
                    throw new CertificateIsNotCA();
                }

                if (!isCertificateValid(parentName)) {
                    throw new CertificateIsNotValid();
                }

                issuerData = generateIssuerData(reader.readPrivateKey("data/" + parentName, KEY_STORE_PASS, parentName, PASS), dto);
                X509Certificate endEntity = certificateGenerator.generateCertificate(subjectData, issuerData, false);

                writer.loadKeyStore(null, KEY_STORE_PASS.toCharArray());
                writer.write(certificateName, issuerData.getPrivateKey(), PASS.toCharArray(), endEntity);
                writer.saveKeyStore("data/" + certificateName, KEY_STORE_PASS.toCharArray());

                X509Certificate certificateLoadedEndEntity = (X509Certificate) reader.readCertificate("data/" + certificateName,
                        KEY_STORE_PASS, certificateName);
                System.out.println(certificateLoadedEndEntity.getIssuerX500Principal().getName());

                OCSPCertificate ocspCertificateEndEntity = new OCSPCertificate(certificateName, certificateRepository.findByFileName(parentName));
                certificateRepository.save(ocspCertificateEndEntity);
        }
    }

    @Override
    public void changeCertificateStatus(String certificateAlias) {
        OCSPCertificate certificate = certificateRepository.findByFileName(certificateAlias);
        certificate.setRevoked(!certificate.isRevoked());
        certificateRepository.save(certificate);
    }

    @Override
    public boolean isAnyInChainRevoked(String certificateAlias) {
        OCSPCertificate certificate = certificateRepository.findByFileName(certificateAlias);
        if (certificate.isRevoked()) {
            return true;
        }
        if (certificate.getIssuer() != null) {
            return isAnyInChainRevoked(certificate.getIssuer().getFileName());
        }
        return false;
    }

    @Override
    public boolean isAnyInChainOutdated(String certificateName) {
        Date today = new Date();
        KeyStoreReader reader = new KeyStoreReader();

        X509Certificate certificate = (X509Certificate) reader.readCertificate("data/" + certificateName,
                KEY_STORE_PASS, certificateName);
        OCSPCertificate ocspCertificate = certificateRepository.findByFileName(certificateName);

        if (today.after(certificate.getNotBefore()) && today.before(certificate.getNotAfter())) {
            if (ocspCertificate.getIssuer() != null) {
                return isAnyInChainOutdated(ocspCertificate.getIssuer().getFileName());
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCertificateValid(String certificateAlias) {
        return !(isAnyInChainOutdated(certificateAlias) || isAnyInChainRevoked(certificateAlias));
    }

    @Override
    public Iterable<OCSPCertificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    @Override
    public List<CertificateDto> getCACertificates() {
        KeyStoreReader reader = new KeyStoreReader();
        List<OCSPCertificate> ocspCertificates = (List<OCSPCertificate>) certificateRepository.findAll();
        List<CertificateDto> certificateDtos = new ArrayList<>();

        ocspCertificates.forEach(ocspCertificate -> {
            String certificateName = ocspCertificate.getFileName();
            X509Certificate certificate = (X509Certificate) reader.readCertificate("data/" + certificateName,
                    KEY_STORE_PASS, certificateName);
            if (certificate.getBasicConstraints() != -1) {
                CertificateDto certificateDto = new CertificateDto();
                certificateDto.setStartDate(certificate.getNotBefore());
                certificateDto.setEndDate(certificate.getNotAfter());
                certificateDto.setCertificateName(ocspCertificate.getFileName());
                if (ocspCertificate.getIssuer() == null) {
                    certificateDto.setCa(CA.Root);
                    certificateDto.setParentName("Self-signed");
                } else {
                    certificateDto.setCa(CA.Intermediate);
                    certificateDto.setParentName(ocspCertificate.getIssuer().getFileName());
                }
                certificateDtos.add(certificateDto);
            }
        });

        return certificateDtos;
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        } catch (NoSuchProviderException var4) {
            var4.printStackTrace();
        }

        return null;
    }

    private IssuerData generateIssuerData(PrivateKey issuerKey, CertificateDto dto) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        if (dto.getParentName() == null) {
            builder.addRDN(BCStyle.CN, "Self-signed");
        } else {
            builder.addRDN(BCStyle.CN, dto.getParentName());
        }

        return new IssuerData(issuerKey, builder.build());
    }

    private DataKeyPair generateSubjectDataAndKey(CertificateDto dto) {
        KeyPair keyPairSubject = this.generateKeyPair();

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

    private class DataKeyPair {

        public SubjectData subjectData;
        public PrivateKey privateKey;

        public DataKeyPair(SubjectData subjectData, PrivateKey privateKey) {
            this.subjectData = subjectData;
            this.privateKey = privateKey;
        }
    }
}
