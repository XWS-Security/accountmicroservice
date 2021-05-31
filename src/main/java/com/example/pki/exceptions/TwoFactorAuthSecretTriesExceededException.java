package com.example.pki.exceptions;

public class TwoFactorAuthSecretTriesExceededException extends RuntimeException {
    public TwoFactorAuthSecretTriesExceededException() {
        super("You have exceeded the auth secret limit. The code is no longer valid!");
    }
}
