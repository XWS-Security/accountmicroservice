package com.example.pki.certificate;

import com.example.pki.exceptions.CouldNotGenerateCertificateException;
import com.example.pki.model.IssuerData;
import com.example.pki.model.SubjectData;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateGenerator {

    public CertificateGenerator() {
    }

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, boolean isCertificateCA) throws CouldNotGenerateCertificateException {
        try {
            // Create content signer
            JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            contentSignerBuilder = contentSignerBuilder.setProvider(BouncyCastleProvider.PROVIDER_NAME);
            ContentSigner contentSigner = contentSignerBuilder.build(issuerData.getPrivateKey());

            // Create certificate builder
            X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());

            // Add extensions for CA
            if (isCertificateCA) {
                JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
                certificateBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
                certificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(subjectData.getPublicKey()));
            }

            // Extensions for localhost
            DERSequence subjectAlternativeNames = new DERSequence(new ASN1Encodable[]{
                    new GeneralName(GeneralName.dNSName, "localhost"),
                    new GeneralName(GeneralName.dNSName, "127.0.0.1")
            });
            certificateBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAlternativeNames);

            // Create certificate holder
            X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

            // Convert holder to certificate
            JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
            certificateConverter = certificateConverter.setProvider("BC");

            return certificateConverter.getCertificate(certificateHolder);
        } catch (IllegalArgumentException | IllegalStateException | OperatorCreationException | CertificateException | NoSuchAlgorithmException | CertIOException e) {
            // TODO: log error
            throw new CouldNotGenerateCertificateException();
        }
    }
}

