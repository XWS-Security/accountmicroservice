package com.example.pki.service.impl;

import com.example.pki.exceptions.CertificateAlreadyExists;
import com.example.pki.exceptions.CertificateIsNotValid;
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
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
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

    @Override
    public void generate(CertificateDto dto) {
        String certificateName = dto.getCertificateName();
        String parentName = dto.getParentName();

        if (certificateRepository.findByFileName(certificateName) != null) {
            throw new CertificateAlreadyExists();
        }

        if (dto.getCa() != CA.Root) {
            X509Certificate parent = readCertificateFromPfx(parentName);
            // TODO: Check if parent is CA
//            if (parent.getBasicConstraints() == -1) {
//                throw new CertificateIsNotCA();
//            }
            if (!isCertificateValid(parentName)) {
                throw new CertificateIsNotValid();
            }
        }

        DataKeyPair pair = generateSubjectDataAndKey(dto);
        SubjectData subjectData = pair.subjectData;

        IssuerData issuerData;
        boolean isCA;
        OCSPCertificate issuerOCSP;

        switch (dto.getCa()) {
            case Root:
                issuerData = generateIssuerData(pair.privateKey, "Self-signed");
                issuerOCSP = null;
                isCA = true;
                break;

            case Intermediate:
                PrivateKey parentKey = readPrivateKeyFromPfx(parentName);
                issuerData = generateIssuerData(parentKey, parentName);
                issuerOCSP = certificateRepository.findByFileName(parentName);
                isCA = true;
                break;

            default: // End-entity
                PrivateKey parentKeyEnd = readPrivateKeyFromPfx(parentName);
                issuerData = generateIssuerData(parentKeyEnd, parentName);
                issuerOCSP = certificateRepository.findByFileName(parentName);
                isCA = false;
        }

        // Generate certificate
        CertificateGenerator certificateGenerator = new CertificateGenerator();
        X509Certificate certificate = certificateGenerator.generateCertificate(subjectData, issuerData, isCA);
        // Save to keystore
        saveCertAsPfx(certificate, certificateName, issuerData.getPrivateKey());
        // Save OCSP data in database
        saveOCSPData(certificateName, issuerOCSP);
        // Save in pem format to use in front app
        saveCertAndPrivateKeyPems(certificateName, certificate, pair.privateKey);
    }

    @Override
    public void revoke(String certificateAlias) {
        // TODO: Refactor: revoke every in chain
        OCSPCertificate certificate = certificateRepository.findByFileName(certificateAlias);
        certificate.setRevoked(true);
        certificateRepository.save(certificate);
    }

    @Override
    public boolean isCertificateValid(String alias) {
        OCSPCertificate ocspCertificate = certificateRepository.findByFileName(alias);
        X509Certificate x509Certificate = readCertificateFromPfx(alias);
        return !ocspCertificate.isRevoked() && !isCertificateExpired(x509Certificate);
    }

    private boolean isCertificateExpired(X509Certificate certificate) {
        Date today = new Date();
        return today.before(certificate.getNotBefore()) || today.after(certificate.getNotAfter());
    }

    public List<CertificateDto> getCertificates(boolean onlyCA) {
        KeyStoreReader reader = new KeyStoreReader();
        List<OCSPCertificate> ocspCertificates = (List<OCSPCertificate>) certificateRepository.findAll();
        List<CertificateDto> certificateDtos = new ArrayList<>();

        ocspCertificates.forEach(ocspCertificate -> {
            String certificateName = ocspCertificate.getFileName();
            String certificateFileName = "data/" + certificateName + ".pfx";
            X509Certificate certificate = (X509Certificate)
                    reader.readCertificate(certificateFileName, KEY_STORE_PASS, certificateName);

            CertificateDto certificateDto = new CertificateDto();
            certificateDto.setStartDate(certificate.getNotBefore());
            certificateDto.setEndDate(certificate.getNotAfter());
            certificateDto.setCertificateName(ocspCertificate.getFileName());
            certificateDto.setRevoked(ocspCertificate.isRevoked());
            certificateDto.setValid(isCertificateValid(ocspCertificate.getFileName()));

//            if (certificate.getBasicConstraints() != -1) {
            if (ocspCertificate.getIssuer() == null) {
                certificateDto.setCa(CA.Root);
                certificateDto.setParentName("Self-signed");
            } else {
                certificateDto.setCa(CA.Intermediate);
                certificateDto.setParentName(ocspCertificate.getIssuer().getFileName());
            }
            certificateDtos.add(certificateDto);
//            } else if (!onlyCA) {
//                certificateDto.setCa(CA.EndEntity);
//                certificateDto.setParentName(ocspCertificate.getIssuer().getFileName());
//                certificateDtos.add(certificateDto);
//            }
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

    private IssuerData generateIssuerData(PrivateKey issuerKey, String parent) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, parent);
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

    private PrivateKey readPrivateKeyFromPfx(String name) {
        KeyStoreReader reader = new KeyStoreReader();
        String fileName = "data/" + name + ".pfx";
        return reader.readPrivateKey(fileName, KEY_STORE_PASS, name, PASS);
    }

    private X509Certificate readCertificateFromPfx(String name) {
        KeyStoreReader reader = new KeyStoreReader();
        String fileName = "data/" + name + ".pfx";
        return (X509Certificate) reader.readCertificate(fileName, KEY_STORE_PASS, name);
    }

    private void saveOCSPData(String certificateName, OCSPCertificate issuer) {
        OCSPCertificate ocspCertificate = new OCSPCertificate(certificateName, issuer);
        certificateRepository.save(ocspCertificate);
    }

    private void saveCertAsPfx(X509Certificate certificate, String name, PrivateKey issuerPrivateKey) {
        KeyStoreWriter writer = new KeyStoreWriter();
        String fileName = "data/" + name + ".pfx";
        writer.loadKeyStore(null, KEY_STORE_PASS.toCharArray());
        writer.write(name, issuerPrivateKey, PASS.toCharArray(), certificate);
        writer.saveKeyStore(fileName, KEY_STORE_PASS.toCharArray());
    }

    private void saveCertAndPrivateKeyPems(String name, X509Certificate certificate, PrivateKey privateKey) {
        String certFileName = "data/" + name + "-cert.pem";
        saveAsPem(certFileName, certificate);
        String keyFileName = "data/" + name + "-key.pem";
        saveAsPem(keyFileName, privateKey);
    }

    private void saveAsPem(String fileName, Object object) {
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(fileName))) {
            pemWriter.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
