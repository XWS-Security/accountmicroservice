package com.example.pki.model.dto;

import java.io.Serializable;

public class FollowerMicroserviceUserDto implements Serializable {
    private String username;
    private boolean profilePrivate = false;
    private String about;

    public FollowerMicroserviceUserDto() {

    }

    public FollowerMicroserviceUserDto(String username, boolean profilePrivate, String about) {
        this.username = username;
        this.profilePrivate = profilePrivate;
        this.about = about;
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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
