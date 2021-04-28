package com.example.pki.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends User{
    private transient final String administrationRole = "ROLE_ADMINISTRATOR";

    @Override
    public String getAdministrationRole() {
        return administrationRole;
    }
}
