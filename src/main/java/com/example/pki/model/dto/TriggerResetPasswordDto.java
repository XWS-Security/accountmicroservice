package com.example.pki.model.dto;

import com.example.pki.util.Constants;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class TriggerResetPasswordDto implements Serializable {

    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String email;

    public TriggerResetPasswordDto() {
    }

    public TriggerResetPasswordDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
