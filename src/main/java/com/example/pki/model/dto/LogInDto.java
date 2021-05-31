package com.example.pki.model.dto;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class LogInDto implements Serializable {

    @Pattern(regexp = "^[^<>]+", message = "Invalid character!")
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=]).{10,20}$", message = "Invalid character!")
    private String password;

    @Pattern(regexp = "^[^<>]*", message = "Invalid character!")
    private String twoFactorAuthenticationSecret;

    public LogInDto(String email, String password, String twoFactorAuthenticationSecret) {
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
