package com.example.pki.model.dto;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class ChangePasswordDto implements Serializable {

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=]).{10,20}$", message = "Invalid character!")
    private String newPassword;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=]).{10,20}$", message = "Invalid character!")
    private String newPasswordRepeated;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=]).{10,20}$", message = "Invalid character!")
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
