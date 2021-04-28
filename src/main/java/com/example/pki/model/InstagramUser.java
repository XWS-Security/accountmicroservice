package com.example.pki.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity
@DiscriminatorValue("INSTAGRAM_USER")
public class InstagramUser extends User {
    private transient final String administrationRole = "ROLE_INSTAGRAM_USER";

    @Column(name = "mail_activation_code", length = 64)
    private String activationCode;

    @Column(name = "registration_sent_date")
    private Timestamp registrationSentDate;

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

    public Timestamp getRegistrationSentDate() {
        return registrationSentDate;
    }
}

