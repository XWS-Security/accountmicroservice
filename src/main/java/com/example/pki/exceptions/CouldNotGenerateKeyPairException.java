package com.example.pki.exceptions;

public class CouldNotGenerateKeyPairException extends RuntimeException {
    public CouldNotGenerateKeyPairException() {
        super("Could not generate key-pair.");
    }
}
