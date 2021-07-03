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
    private boolean tagsEnabled = false;
    private boolean messagesEnabled = false;

    public FollowerMicroserviceUpdateUserDto() {
    }

    public FollowerMicroserviceUpdateUserDto(String username, String oldUsername, String about, boolean profilePrivate,
                                             boolean tagsEnabled, boolean messagesEnabled) {
        this.username = username;
        this.oldUsername = oldUsername;
        this.about = about;
        this.profilePrivate = profilePrivate;
        this.tagsEnabled = tagsEnabled;
        this.messagesEnabled = messagesEnabled;
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

    public boolean isTagsEnabled() {
        return tagsEnabled;
    }

    public void setTagsEnabled(boolean tagsEnabled) {
        this.tagsEnabled = tagsEnabled;
    }

    public boolean isMessagesEnabled() {
        return messagesEnabled;
    }

    public void setMessagesEnabled(boolean messagesEnabled) {
        this.messagesEnabled = messagesEnabled;
    }
}
