package com.example.pki.exceptions;

public class CertificateIsNotCA extends RuntimeException {

    public CertificateIsNotCA() {
        super("Certificate is not CA!");
    }
}
