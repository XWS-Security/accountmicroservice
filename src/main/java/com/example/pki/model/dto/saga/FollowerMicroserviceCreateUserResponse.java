package com.example.pki.model.dto.saga;

import java.io.Serializable;

public class FollowerMicroserviceCreateUserResponse implements Serializable {
    private boolean success;
    private String message;

    public FollowerMicroserviceCreateUserResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
