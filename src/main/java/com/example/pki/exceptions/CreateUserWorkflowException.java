package com.example.pki.exceptions;

public class CreateUserWorkflowException extends RuntimeException {
    public CreateUserWorkflowException() {
        super("User could not be created because of errors in other microservices.");
    }
}
