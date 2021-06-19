package com.example.pki.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User with username: " + username + " couldn't be found.");
    }

    public UserNotFoundException() {
        super("User couldn't be found.");
    }
}
