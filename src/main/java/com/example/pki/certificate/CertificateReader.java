package com.example.pki.certificate;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Iterator;

public class CertificateReader {

    public static final String BASE64_ENC_CERT_FILE = "./data/";
    public static final String BIN_ENC_CERT_FILE = "./data/";

    private void readFromBase64EncFile() {
        try {
            FileInputStream fis = new FileInputStream(BASE64_ENC_CERT_FILE);
            BufferedInputStream bis = new BufferedInputStream(fis);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                Certificate cert = cf.generateCertificate(bis);
                System.out.println(cert.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("rawtypes")
    private void readFromBinEncFile() {
        try {
            FileInputStream fis = new FileInputStream(BIN_ENC_CERT_FILE);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            //Ovde se vade svi sertifkati
            Collection c = cf.generateCertificates(fis);
            Iterator i = c.iterator();
            while (i.hasNext()) {
                Certificate cert = (Certificate)i.next();
                System.out.println(cert);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        CertificateReader test = new CertificateReader();
    }
}

