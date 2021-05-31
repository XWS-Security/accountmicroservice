package com.example.pki.keystore;

import com.example.pki.exceptions.KeystoreErrorException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class KeyStoreWriter {

    private KeyStore keyStore;

    public KeyStoreWriter() {
        try {
            keyStore = KeyStore.getInstance("PKCS12", "SUN");
        } catch (KeyStoreException | NoSuchProviderException e) {
            // TODO: log error
            throw new KeystoreErrorException();
        }
    }

    public void loadKeyStore(String fileName, char[] password) {
        try {
            if (fileName != null) {
                keyStore.load(new FileInputStream(fileName), password);
            } else {
                keyStore.load(null, password);
            }
        } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
            // TODO: log error
            throw new KeystoreErrorException();
        }
    }

    public void saveKeyStore(String fileName, char[] password) { //Keystore
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            // TODO: log error
            throw new KeystoreErrorException();
        }
    }

    public void write(String alias, PrivateKey privateKey, char[] password, Certificate certificate) { //za private key
        try {
            keyStore.setKeyEntry(alias, privateKey, password, new Certificate[]{certificate});
        } catch (KeyStoreException e) {
            // TODO: log error
            throw new KeystoreErrorException();
        }
    }
}

