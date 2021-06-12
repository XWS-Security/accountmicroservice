package com.example.pki.keystore;

import com.example.pki.exceptions.KeystoreErrorException;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class Keystore {
    private final String KEY_STORE_PASS = "123";
    private final String PASS = "123";
    private final String keystorePath;

    public Keystore(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public PrivateKey readPrivateKeyFromPfx(String name) {
        KeyStoreReader reader = new KeyStoreReader();
        String fileName = this.keystorePath + name + ".pfx";
        return reader.readPrivateKey(fileName, KEY_STORE_PASS, name, PASS);
    }

    public X509Certificate readCertificateFromPfx(String name) {
        System.out.println(this.keystorePath);
        KeyStoreReader reader = new KeyStoreReader();
        String fileName = this.keystorePath + name + ".pfx";
        return (X509Certificate) reader.readCertificate(fileName, KEY_STORE_PASS, name);
    }

    public void saveCertAsPfx(X509Certificate certificate, String name, PrivateKey issuerPrivateKey) {
        KeyStoreWriter writer = new KeyStoreWriter();
        String fileName = this.keystorePath + name + ".pfx";
        writer.loadKeyStore(null, KEY_STORE_PASS.toCharArray());
        writer.write(name, issuerPrivateKey, PASS.toCharArray(), certificate);
        writer.saveKeyStore(fileName, KEY_STORE_PASS.toCharArray());
    }

    public void saveCertAndPrivateKeyPems(String name, X509Certificate certificate, PrivateKey privateKey) {
        String certFileName = this.keystorePath + name + "-cert.pem";
        saveAsPem(certFileName, certificate);
        String keyFileName = this.keystorePath + name + "-key.pem";
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
