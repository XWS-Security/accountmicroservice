package com.example.pki.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("INSTAGRAM")
public class InstagramUser extends User{
    private transient final String administrationRole = "ROLE_INSTAGRAM_USER";

    @Column(name = "mail_activation_code", length = 64)
    private String activationCode;

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
}
