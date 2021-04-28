package com.example.pki.exceptions;

public class CertificateIsNotValid extends RuntimeException {

    public CertificateIsNotValid() {
        super("Certificate is not valid!");
    }
}
