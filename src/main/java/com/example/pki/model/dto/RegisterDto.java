package com.example.pki.model.dto;

import java.io.Serializable;

public class RegisterDto implements Serializable {

    private String email;
    private String password;
    private String repeatedPassword;

    public RegisterDto() {

    }

    public RegisterDto(String email, String password, String repeatedPassword) {
        this.email = email;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
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

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
}
