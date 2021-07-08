package com.example.pki.model.dto;

import com.example.pki.model.enums.Gender;

import java.io.Serializable;

public class BasicUserInfoDto implements Serializable {

    private String username;
    private Gender gender;
    private Integer age;

    public BasicUserInfoDto(String username, Gender gender, Integer age) {
        this.username = username;
        this.gender = gender;
        this.age = age;
    }

    public BasicUserInfoDto() {
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
