package com.example.pki.model.dto;

import java.io.Serializable;

public class CreateTokenOwnerDto implements Serializable {
    private String username;
    private String agentUsername;

    public CreateTokenOwnerDto() {
    }

    public CreateTokenOwnerDto(String username, String agentUsername) {
        this.username = username;
        this.agentUsername = agentUsername;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAgentUsername() {
        return agentUsername;
    }

    public void setAgentUsername(String agentUsername) {
        this.agentUsername = agentUsername;
    }
}
