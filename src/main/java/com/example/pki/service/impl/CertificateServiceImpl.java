package com.example.pki.service.impl;

import com.example.pki.certificate.CertificateGenerator;
import com.example.pki.keystore.KeyStoreReader;
import com.example.pki.keystore.KeyStoreWriter;
import com.example.pki.model.IssuerData;
import com.example.pki.model.SubjectData;
import com.example.pki.model.dto.CertificateDto;
import com.example.pki.service.CertificateService;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class CertificateServiceImpl implements CertificateService {

    private final String KEY_STORE_PASS = "123";
    private final String PASS = "123";

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
        //TODO READ PRIVATE KEY AND SAVE CERTIFICATE
        switch (dto.getCa()) {
            case Root:
                issuerData = generateIssuerData(pair.privateKey, dto);
                X509Certificate certificate = certificateGenerator.generateCertificate(subjectData, issuerData, true);
                writer.loadKeyStore(null, KEY_STORE_PASS.toCharArray());
                writer.write("alias", issuerData.getPrivateKey(), PASS.toCharArray(), certificate);
                writer.saveKeyStore("data/alias", KEY_STORE_PASS.toCharArray());

                X509Certificate certificateLoaded = (X509Certificate) reader.readCertificate("data/alias", KEY_STORE_PASS, "alias");
                System.out.println(certificateLoaded.getIssuerX500Principal().getName());

                PrivateKey privateKey = reader.readPrivateKey("data/alias", KEY_STORE_PASS, "alias", PASS);
                System.out.println(privateKey);

            case Intermediate:
                issuerData = generateIssuerData(keyPairSubject.getPrivate(), dto);
                certificateGenerator.generateCertificate(subjectData, issuerData, true);
            case EndEntity:
                issuerData = generateIssuerData(keyPairSubject.getPrivate(), dto);
                certificateGenerator.generateCertificate(subjectData, issuerData, false);
        }
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
        builder.addRDN(BCStyle.UID, dto.getIssuerUid());
        return new IssuerData(issuerKey, builder.build());
    }

    private DataKeyPair generateSubjectDataAndKey(CertificateDto dto) {

        KeyPair keyPairSubject = this.generateKeyPair();

        Date startDate = dto.getStartDate();
        Date endDate = dto.getEndDate();
        //TODO MAKE SERIAL NUMBER
        String sn = "1";
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.UID, dto.getSubjectUid());
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
