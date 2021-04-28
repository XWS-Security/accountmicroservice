package com.example.pki.model.dto;

import java.io.Serializable;

public class ResetPasswordDto implements Serializable {
    private String email;
    private String newPassword;
    private String newPasswordRepeated;
    private String code;

    public ResetPasswordDto() {
    }

    public ResetPasswordDto(String email, String newPassword, String newPasswordRepeated, String code) {
        this.email = email;
        this.newPassword = newPassword;
        this.newPasswordRepeated = newPasswordRepeated;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordRepeated() {
        return newPasswordRepeated;
    }

    public void setNewPasswordRepeated(String newPasswordRepeated) {
        this.newPasswordRepeated = newPasswordRepeated;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
