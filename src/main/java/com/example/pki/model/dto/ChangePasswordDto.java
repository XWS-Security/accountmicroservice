package com.example.pki.model.dto;

import com.example.pki.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class ChangePasswordDto implements Serializable {
    @NotNull
    @Pattern(regexp = Constants.PASSWORD_PATTERN, message = Constants.PASSWORD_INVALID_MESSAGE)
    private String newPassword;

    @NotNull
    @Pattern(regexp = Constants.PASSWORD_PATTERN, message = Constants.PASSWORD_INVALID_MESSAGE)
    private String newPasswordRepeated;

    @NotNull
    @Pattern(regexp = Constants.PASSWORD_PATTERN, message = Constants.PASSWORD_INVALID_MESSAGE)
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
