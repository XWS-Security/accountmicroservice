package com.example.pki.exceptions;

public class BadActivationCodeException extends Exception {
    public BadActivationCodeException() {
        super("Activation code is false, or profile is already activated.");
    }
}
