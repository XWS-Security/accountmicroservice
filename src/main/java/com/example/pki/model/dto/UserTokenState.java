package com.example.pki.model.dto;

import java.util.Objects;

public class UserTokenState {

    private String userType;
    private String accessToken;
    private int expiresIn;

    public UserTokenState() {
    }

    public UserTokenState(String userType, String accessToken, int expiresIn) {
        this.userType = userType;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTokenState state = (UserTokenState) o;
        return Objects.equals(userType, state.userType) &&
                Objects.equals(accessToken, state.accessToken) &&
                Objects.equals(expiresIn, state.expiresIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userType, accessToken, expiresIn);
    }

    @Override
    public String toString() {
        return "UserTokenState{" +
                "userType='" + userType + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
