package com.example.pki.model.dto;

import com.example.pki.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class LogInDto implements Serializable {
    @NotNull
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String username;

    @NotNull
    @Pattern(regexp = Constants.PASSWORD_PATTERN, message = Constants.PASSWORD_INVALID_MESSAGE)
    private String password;

    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String twoFactorAuthenticationSecret;

    public LogInDto(String username, String password, String twoFactorAuthenticationSecret) {
        this.username = username;
        this.password = password;
        this.twoFactorAuthenticationSecret = twoFactorAuthenticationSecret;
    }

    public LogInDto() {
    }

    public String getTwoFactorAuthenticationSecret() {
        return twoFactorAuthenticationSecret;
    }

    public void setTwoFactorAuthenticationSecret(String twoFactorAuthenticationSecret) {
        this.twoFactorAuthenticationSecret = twoFactorAuthenticationSecret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
