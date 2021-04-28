package com.example.pki.exceptions;

public class EmailDoesNotExistException extends Exception {
    public EmailDoesNotExistException() {
        super("User with entered email does not exist. Please check if the email address is entered correctly.");
    }
}
