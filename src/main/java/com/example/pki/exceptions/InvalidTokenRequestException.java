package com.example.pki.exceptions;

public class InvalidTokenRequestException extends RuntimeException {
    public InvalidTokenRequestException() {
        super("Only registered agents can generate campaign management tokens.");
    }
}
