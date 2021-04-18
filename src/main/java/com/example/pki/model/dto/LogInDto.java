package com.example.pki.model.dto;

import java.util.Objects;

public class LogInDto {

    private String email;
    private String password;
    private String oldPassword;

    public LogInDto(String email, String password, String oldPassword) {
        this.email = email;
        this.password = password;
        this.oldPassword = oldPassword;
    }

    public LogInDto() {
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @Override
    public String toString() {
        return "LogInDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", oldPassword='" + oldPassword + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogInDto logInDto = (LogInDto) o;
        return Objects.equals(email, logInDto.email) &&
                Objects.equals(password, logInDto.password) &&
                Objects.equals(oldPassword, logInDto.oldPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, oldPassword);
    }
}
