package com.example.pki.exceptions;

public class BadUserInformationException extends RuntimeException {
    public BadUserInformationException() { super("User with that mail address or username already exists!");
    }
}
