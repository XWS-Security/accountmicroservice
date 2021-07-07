package com.example.pki.model.dto;

import com.example.pki.model.Agent;
import com.example.pki.model.enums.Gender;
import com.example.pki.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class RegisterAgentDTO {
    @NotNull
    private String username;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String repeatedPassword;

    private String about;
    @NotNull
    private String website;

    public RegisterAgentDTO() {

    }

    public RegisterAgentDTO(String username, String name, String surname, String email, String password, String repeatedPassword, String about, String website) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
        this.about = about;
        this.website = website;
    }

    public RegisterAgentDTO(String username, String name, String surname, String email, String about, String website) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.about = about;
        this.website = website;
    }

    public RegisterAgentDTO(Agent agent) {
        this.username = agent.getUsername();
        this.name = agent.getName();
        this.surname = agent.getSurname();
        this.email = agent.getEmail();
        this.about = agent.getAboutAgent();
        this.website = agent.getWebsite();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
