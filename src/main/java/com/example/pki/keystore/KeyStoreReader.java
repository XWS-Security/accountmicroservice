package com.example.pki.keystore;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.example.pki.model.IssuerData;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

public class KeyStoreReader {

    private KeyStore keyStore;

    public KeyStoreReader() {
        try {
            keyStore = KeyStore.getInstance("PKCS12", "SUN");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public IssuerData readIssuerFromStore(String keyStoreFile, String alias, char[] password, char[] keyPass) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, password);
            Certificate cert = keyStore.getCertificate(alias);

            PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPass);
            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            return new IssuerData(privKey, issuerName);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Certificate readCertificate(String keyStoreFile, String keyStorePass, String alias) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, keyStorePass.toCharArray());

            if (keyStore.isKeyEntry(alias)) {
                Certificate cert = keyStore.getCertificate(alias);
                return cert;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias, String pass) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, keyStorePass.toCharArray());

            if (keyStore.isKeyEntry(alias)) {
                PrivateKey pk = (PrivateKey) keyStore.getKey(alias, pass.toCharArray());
                return pk;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}

