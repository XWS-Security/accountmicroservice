package com.example.pki.model.dto;

import com.example.pki.model.NistagramUser;
import com.example.pki.model.enums.Gender;
import com.example.pki.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class RegisterDto {

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

    @NotNull
    @Pattern(regexp = Constants.PASSWORD_PATTERN, message = Constants.PASSWORD_INVALID_MESSAGE)
    private String password;

    @NotNull
    @Pattern(regexp = Constants.PASSWORD_PATTERN, message = Constants.PASSWORD_INVALID_MESSAGE)
    private String repeatedPassword;

    @NotNull
    private Gender gender;

    @Pattern(regexp = "^[0-9]*", message = "Invalid character!")
    private String phoneNumber;

    @NotNull
    private Date dateOfBirth;

    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String about;

    private boolean profilePrivate;

    public RegisterDto() {

    }

    public RegisterDto(String name, String surname, String email, String password, String repeatedPassword, String username,
                       Gender gender, Date dateOfBirth, String phoneNumber, String about, boolean profilePrivate) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.about = about;
        this.profilePrivate = profilePrivate;
    }

    public RegisterDto(String name, String surname, String email, String username,
                       Gender gender, Date dateOfBirth, String phoneNumber, String about, boolean profilePrivate) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.about = about;
        this.profilePrivate = profilePrivate;
    }

    public static UserDto convertUserToDto(NistagramUser user) {
        return new UserDto(user.getName(), user.getSurname(), user.getEmail(), user.getNistagramUsername(),
                user.getGender(), user.getDateOfBirth(), user.getPhoneNumber(), user.getAbout(), user.isProfilePrivate());
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public boolean isProfilePrivate() {
        return profilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        this.profilePrivate = profilePrivate;
    }
}
