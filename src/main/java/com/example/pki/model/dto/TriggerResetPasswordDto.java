package com.example.pki.model.dto;

import java.io.Serializable;

public class TriggerResetPasswordDto implements Serializable {
    private String email;

    public TriggerResetPasswordDto() {
    }

    public TriggerResetPasswordDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
