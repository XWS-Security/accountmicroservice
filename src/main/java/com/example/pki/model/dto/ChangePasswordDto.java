package com.example.pki.model.dto;

import java.io.Serializable;

public class ChangePasswordDto implements Serializable {
    private String newPassword;
    private String newPasswordRepeated;
    private String oldPassword;

    public ChangePasswordDto() {
    }

    public ChangePasswordDto(String newPassword, String newPasswordRepeated, String oldPassword) {
        this.newPassword = newPassword;
        this.newPasswordRepeated = newPasswordRepeated;
        this.oldPassword = oldPassword;
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
