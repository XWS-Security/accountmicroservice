package com.example.pki.model.dto;

import java.io.Serializable;

public class FollowerMicroserviceUserDto implements Serializable {
    private String username;
    private boolean profilePrivate = false;

    public FollowerMicroserviceUserDto() {

    }

    public FollowerMicroserviceUserDto(String username, boolean profilePrivate) {
        this.username = username;
        this.profilePrivate = profilePrivate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isProfilePrivate() {
        return profilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        this.profilePrivate = profilePrivate;
    }
}
