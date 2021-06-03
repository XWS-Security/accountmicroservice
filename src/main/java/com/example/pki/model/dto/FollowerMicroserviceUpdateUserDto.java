package com.example.pki.model.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class FollowerMicroserviceUpdateUserDto implements Serializable {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_]{2,12}", message = "Username must be 2 to 12 characters long and can contain only letters, numbers and an underscore.")
    private String username;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_]{2,12}", message = "Username must be 2 to 12 characters long and can contain only letters, numbers and an underscore.")
    private String oldUsername;
    private boolean profilePrivate = false;

    public FollowerMicroserviceUpdateUserDto() {
    }

    public FollowerMicroserviceUpdateUserDto(String oldUsername, String username, boolean profilePrivate) {
        this.oldUsername = oldUsername;
        this.username = username;
        this.profilePrivate = profilePrivate;
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
}
