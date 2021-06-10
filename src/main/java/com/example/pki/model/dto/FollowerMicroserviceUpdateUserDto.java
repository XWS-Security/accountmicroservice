package com.example.pki.model.dto;

import com.example.pki.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class FollowerMicroserviceUpdateUserDto implements Serializable {
    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
    private String username;

    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
    private String oldUsername;

    @NotNull
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.PLAIN_TEXT_PATTERN)
    private String about;

    private boolean profilePrivate = false;

    public FollowerMicroserviceUpdateUserDto() {
    }

    public FollowerMicroserviceUpdateUserDto(String oldUsername, String username, boolean profilePrivate, String about) {
        this.oldUsername = oldUsername;
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

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
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
