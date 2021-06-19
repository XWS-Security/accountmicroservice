package com.example.pki.exceptions;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(Long id) {
        super("Object with id: " + id + "couldn't be found.");
    }
}
