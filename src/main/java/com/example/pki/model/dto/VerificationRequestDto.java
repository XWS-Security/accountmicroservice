package com.example.pki.model.dto;

import com.example.pki.model.enums.VerificationCategory;

import java.io.Serializable;

public class VerificationRequestDto implements Serializable {

    private VerificationCategory category;
    private String officialDocumentImageName;
    private String name;
    private String surname;

    public VerificationRequestDto() {
    }

    public VerificationRequestDto(VerificationCategory category, String officialDocumentImageName, String name, String surname) {
        this.category = category;
        this.officialDocumentImageName = officialDocumentImageName;
        this.name = name;
        this.surname = surname;
    }

    public VerificationCategory getCategory() {
        return category;
    }

    public void setCategory(VerificationCategory category) {
        this.category = category;
    }

    public String getOfficialDocumentImageName() {
        return officialDocumentImageName;
    }

    public void setOfficialDocumentImageName(String officialDocumentImageName) {
        this.officialDocumentImageName = officialDocumentImageName;
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


    @Override
    public String toString() {
        return "VerificationRequestDto{" +
                "category=" + category +
                ", officialDocumentImageName='" + officialDocumentImageName + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
