package com.example.pki.service.impl;

import com.example.pki.certificate.CertificateGenerator;
import com.example.pki.exceptions.*;
import com.example.pki.keystore.KeyStoreReader;
import com.example.pki.keystore.Keystore;
import com.example.pki.logging.LoggerService;
import com.example.pki.logging.LoggerServiceImpl;
import com.example.pki.model.IssuerData;
import com.example.pki.model.OCSPCertificate;
import com.example.pki.model.SubjectData;
import com.example.pki.model.dto.CertificateDto;
import com.example.pki.model.dto.DownloadCertificateDto;
import com.example.pki.model.dto.SubjectDataDto;
import com.example.pki.model.enums.CA;
import com.example.pki.repository.CertificateRepository;
import com.example.pki.service.CertificateService;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Value("${KEYSTORE_CERT}")
    public String KEYSTORE_PATH;

    private final Keystore keystore = new Keystore("/data/certificates/");
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());
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

        X509Certificate parentX509 = null;
        if (dto.getCa() != CA.Root) {
            parentX509 = keystore.readCertificateFromPfx(parentName);
            if (!isCertificateCA(parentX509)) {
                throw new CertificateIsNotCA();
            }
            if (!isCertificateValid(parentName)) {
                throw new CertificateIsNotValid();
            }
            Date now = new Date();
            dto.setStartDate(now);
            dto.setEndDate(parentX509.getNotAfter());
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
                issuerData = generateIssuerData(parentKey, parentX509);
                issuerOCSP = certificateRepository.findByFileName(parentName);
                isCA = true;
                break;

            default: // End-entity
                PrivateKey parentKeyEnd = keystore.readPrivateKeyFromPfx(parentName);
                issuerData = generateIssuerData(parentKeyEnd, parentX509);
                issuerOCSP = certificateRepository.findByFileName(parentName);
                isCA = false;
        }

        // Generate certificate
        CertificateGenerator certificateGenerator = new CertificateGenerator();
        X509Certificate certificate = certificateGenerator.generateCertificate(subjectData, issuerData, parentX509, isCA);
        // Save to keystore
        keystore.saveCertAsPfx(certificate, certificateName, pair.privateKey);
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
    public boolean isCertificateValid(String fileName) throws KeystoreErrorException {
        OCSPCertificate ocspCertificate = certificateRepository.findByFileName(fileName);
        X509Certificate x509Certificate = keystore.readCertificateFromPfx(fileName);
        return !ocspCertificate.isRevoked() && !isCertificateExpired(x509Certificate);
    }

    private boolean isCertificateExpired(X509Certificate certificate) {
        Date today = new Date();
        return today.before(certificate.getNotBefore()) || today.after(certificate.getNotAfter());
    }

    private boolean isCertificateCA(X509Certificate certificate) {
        return certificate.getBasicConstraints() != -1;
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

    @Override
    public HttpClient buildHttpClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(keystore.readCertificateFromPfx("root"))
                .build();
        return HttpClient.create().secure(t -> t.sslContext(sslContext));
    }

    @Override
    public void downloadCertificate(DownloadCertificateDto certificateDto) throws CertificateEncodingException, IOException {
        KeyStoreReader reader = new KeyStoreReader();
        X509Certificate certificate = (X509Certificate) reader.readCertificate("/data/certificates/" + certificateDto.getCertificateName() + ".pfx",
                certificateDto.getKeystorePass(), certificateDto.getCertificateName());

        X500Name x500Name = new JcaX509CertificateHolder(certificate).getSubject();
        RDN cn = x500Name.getRDNs(BCStyle.CN)[0];
        String certificateName = IETFUtils.valueToString(cn.getFirst().getValue());

        FileOutputStream os = new FileOutputStream("/data/certificates/download/" + certificateName + ".pfx");
        os.write(certificate.getEncoded());
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
            loggerService.logException(e.getMessage());
            throw new CouldNotGenerateKeyPairException();
        }
    }

    private IssuerData generateIssuerData(PrivateKey issuerKey, X509Certificate issuer) {
        X500Name issuerName = X500Name.getInstance(issuer.getSubjectX500Principal().getEncoded());
        return new IssuerData(issuerKey, issuerName);
    }

    private DataKeyPair generateSubjectDataAndKey(CertificateDto dto) throws CouldNotGenerateKeyPairException {
        KeyPair keyPairSubject = generateKeyPair();

        Date startDate = dto.getStartDate();
        Date endDate = dto.getEndDate();

        Random rand = new Random();
        int n = rand.nextInt(1000000);

        String sn = Integer.toString(n);
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        SubjectDataDto data = dto.getSubjectData();
        builder.addRDN(BCStyle.CN, dto.getCertificateName());
        builder.addRDN(BCStyle.SURNAME, data.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, data.getName());
        builder.addRDN(BCStyle.O, data.getOrganisation());
        builder.addRDN(BCStyle.OU, data.getOrganisationUnit());
        builder.addRDN(BCStyle.C, data.getCountryCode());
        builder.addRDN(BCStyle.E, data.getEmail());
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
