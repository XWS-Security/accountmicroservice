package com.example.pki.model.dto;

import com.example.pki.model.NistagramUser;
import com.example.pki.model.enums.Gender;
import com.example.pki.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

public class UserDto implements Serializable {
    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
    private String username;

    @NotNull
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String name;

    @NotNull
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String surname;

    @NotNull
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String email;

    @Pattern(regexp = "^[0-9]*", message = "Invalid character!")
    private String phoneNumber;

    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String about;

    private Date dateOfBirth;

    private boolean profilePrivate;

    public UserDto() {

    }

    public UserDto(String name, String surname, String email, String username,
                   Gender gender, Date dateOfBirth, String phoneNumber, String about, boolean profilePrivate) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.about = about;
        this.dateOfBirth = dateOfBirth;
        this.profilePrivate = profilePrivate;
    }

    public static UserDto convertUserToDto(NistagramUser user) {
        return new UserDto(user.getName(), user.getSurname(), user.getEmail(), user.getNistagramUsername(),
                user.getGender(), user.getDateOfBirth(), user.getPhoneNumber(), user.getAbout(), user.isProfilePrivate());
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isProfilePrivate() {
        return profilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        this.profilePrivate = profilePrivate;
    }
}
