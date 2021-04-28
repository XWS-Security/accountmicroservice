package com.example.pki.exceptions;

public class CertificateAlreadyExists extends RuntimeException {

    public CertificateAlreadyExists() {
        super("Certificate already exists!");
    }
}
