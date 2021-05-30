package com.example.pki.certificate;

import com.example.pki.model.IssuerData;
import com.example.pki.model.SubjectData;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateGenerator {

    public CertificateGenerator() {
    }

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, boolean CA) {
        try {
            // Create content signer
            JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            contentSignerBuilder = contentSignerBuilder.setProvider(BouncyCastleProvider.PROVIDER_NAME);
            ContentSigner contentSigner = contentSignerBuilder.build(issuerData.getPrivateKey());

            // Create certificate builder
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());

            // TODO: Add extensions for CA
//            .addExtension(Extension.certificateIssuer, true, new BasicConstraints(CA))
//            .addExtension(Extension.basicConstraints, true, new BasicConstraints(CA));

            // TODO: Extensions for localhost (check if necessary)
//            DERSequence subjectAlternativeNames = new DERSequence(new ASN1Encodable[]{
//                    new GeneralName(GeneralName.dNSName, "localhost"),
//                    new GeneralName(GeneralName.dNSName, "127.0.0.1")
//            });
//            certGen.addExtension(Extension.subjectAlternativeName, false, subjectAlternativeNames);

            // Create certificate holder
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            // Convert holder to certificate
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            return certConverter.getCertificate(certHolder);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }
}

