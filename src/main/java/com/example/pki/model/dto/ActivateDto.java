package com.example.pki.model.dto;

import com.example.pki.util.Constants;

import javax.validation.constraints.Pattern;
import java.util.Objects;

public class ActivateDto {
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String code;

    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String email;

    public ActivateDto(String code, String email) {
        this.code = code;
        this.email = email;
    }

    public ActivateDto() {
    }

    public String getCode() {
        return code;
    }

    public String getEmail() {
        return email;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivateDto that = (ActivateDto) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, email);
    }
}
