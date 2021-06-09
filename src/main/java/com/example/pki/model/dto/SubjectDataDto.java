package com.example.pki.model.dto;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class SubjectDataDto implements Serializable {
    @Pattern(regexp = "^[^<>]+")
    private String cn;
    @Pattern(regexp = "^[^<>]+")
    private String name;
    @Pattern(regexp = "^[^<>]+")
    private String surname;
    @Pattern(regexp = "^[^<>]+")
    private String organisation;
    @Pattern(regexp = "^[^<>]+")
    private String organisationUnit;
    @Pattern(regexp = "^[^<>]+")
    private String countryCode;
    @Pattern(regexp = "^[^<>]+")
    private String email;

    public SubjectDataDto() {
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
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

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getOrganisationUnit() {
        return organisationUnit;
    }

    public void setOrganisationUnit(String organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
