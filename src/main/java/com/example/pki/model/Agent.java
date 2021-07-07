package com.example.pki.model;

import com.example.pki.model.dto.RegisterAgentDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@DiscriminatorValue("AGENT_USER")
public class Agent extends User{
    private transient final String administrationRole = "ROLE_AGENT_USER";

    @Column(name = "website")
    protected String website;

    @Column(name = "aboutAgent")
    protected String aboutAgent;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAboutAgent() {
        return aboutAgent;
    }

    public void setAboutAgent(String aboutAgent) {
        this.aboutAgent = aboutAgent;
    }
}
