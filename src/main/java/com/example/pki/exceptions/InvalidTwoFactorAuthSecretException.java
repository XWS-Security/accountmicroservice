package com.example.pki.exceptions;

public class InvalidTwoFactorAuthSecretException extends RuntimeException {
    public InvalidTwoFactorAuthSecretException() {
        super("Authentication secret is invalid!");
    }
}
