package com.example.pki.exceptions;

public class PasswordIsNotValid extends RuntimeException {

    public PasswordIsNotValid() {
        super("Password must contain one uppercase letter, one special character, and digit! Minimum length is 10 characters");
    }
}
