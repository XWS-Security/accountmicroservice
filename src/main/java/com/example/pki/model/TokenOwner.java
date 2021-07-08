package com.example.pki.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@DiscriminatorValue("TOKEN_OWNER_USER")
public class TokenOwner extends User {
    private transient final String administrationRole = "ROLE_TOKEN_OWNER";

    @OneToOne
    private NistagramUser nistagramUser;

    public NistagramUser getNistagramUser() {
        return nistagramUser;
    }

    public void setNistagramUser(NistagramUser nistagramUser) {
        this.nistagramUser = nistagramUser;
    }

    @Override
    public String getAdministrationRole() {
        return administrationRole;
    }
}
