package com.example.pki.keystore;

import com.example.pki.exceptions.KeystoreErrorException;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class Keystore {
    private final String KEY_STORE_PASS = "123";
    private final String PASS = "123";

    @Value("${KEYSTORE_CERT}")
    public String KEYSTORE_CERT;

    public PrivateKey readPrivateKeyFromPfx(String name) {
        KeyStoreReader reader = new KeyStoreReader();
        String fileName = KEYSTORE_CERT + name + ".pfx";
        return reader.readPrivateKey(fileName, KEY_STORE_PASS, name, PASS);
    }

    public X509Certificate readCertificateFromPfx(String name) {
        KeyStoreReader reader = new KeyStoreReader();
        String fileName = KEYSTORE_CERT + name + ".pfx";
        return (X509Certificate) reader.readCertificate(fileName, KEY_STORE_PASS, name);
    }

    public void saveCertAsPfx(X509Certificate certificate, String name, PrivateKey issuerPrivateKey) {
        KeyStoreWriter writer = new KeyStoreWriter();
        String fileName = KEYSTORE_CERT + name + ".pfx";
        writer.loadKeyStore(null, KEY_STORE_PASS.toCharArray());
        writer.write(name, issuerPrivateKey, PASS.toCharArray(), certificate);
        writer.saveKeyStore(fileName, KEY_STORE_PASS.toCharArray());
    }

    public void saveCertAndPrivateKeyPems(String name, X509Certificate certificate, PrivateKey privateKey) {
        String certFileName = KEYSTORE_CERT + name + "-cert.pem";
        saveAsPem(certFileName, certificate);
        String keyFileName = KEYSTORE_CERT + name + "-key.pem";
        saveAsPem(keyFileName, privateKey);
    }

    private void saveAsPem(String fileName, Object object) {
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(fileName))) {
            pemWriter.writeObject(object);
        } catch (IOException e) {
            // TODO: log error
            throw new KeystoreErrorException();
        }
    }
}
