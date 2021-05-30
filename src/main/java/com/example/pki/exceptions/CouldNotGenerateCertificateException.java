package com.example.pki.exceptions;

public class CouldNotGenerateCertificateException extends RuntimeException {
    public CouldNotGenerateCertificateException() {
        super("Could not generate certificate.");
    }
}
