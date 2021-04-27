package com.example.pki.model.dto;

import java.io.Serializable;

public class PasswordResetDto implements Serializable {
    private String email;

    public PasswordResetDto() {
    }

    public PasswordResetDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
