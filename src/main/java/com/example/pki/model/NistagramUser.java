package com.example.pki.model;

import com.example.pki.model.enums.Gender;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@DiscriminatorValue("INSTAGRAM_USER")
public class NistagramUser extends User {
    private transient final String administrationRole = "ROLE_INSTAGRAM_USER";

    @Column(name = "mail_activation_code", length = 64)
    private String activationCode;

    @Column(name = "registration_sent_date")
    private Timestamp registrationSentDate;

    @Column(name = "geneder")
    private Gender gender;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "dateOfBirth")
    private Date dateOfBirth;

    @Column(name = "about")
    private String about;

    @Column(name = "profilePrivate")
    private Boolean profilePrivate;

    @Override
    public String getAdministrationRole() {
        return administrationRole;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public void setRegistrationSentDate(Timestamp registrationSentDate) {
        this.registrationSentDate = registrationSentDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Timestamp getRegistrationSentDate() {
        return registrationSentDate;
    }

    public boolean isProfilePrivate() {
        return profilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        this.profilePrivate = profilePrivate;
    }
}

