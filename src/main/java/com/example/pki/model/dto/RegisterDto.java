package com.example.pki.model.dto;

import java.io.Serializable;

public class RegisterDto implements Serializable {

    private String name;
    private String surname;
    private String email;
    private String password;
    private String repeatedPassword;

    public RegisterDto() {

    }

    public RegisterDto(String name, String surname, String email, String password, String repeatedPassword) {
        this.name = name;
        this.surname = surname;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
