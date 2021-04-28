package com.example.pki.service;

import Exceptions.*;
import com.example.pki.model.dto.ChangePasswordDto;
import com.example.pki.model.dto.ResetPasswordDto;
import org.springframework.security.authentication.BadCredentialsException;

import javax.mail.MessagingException;

public interface PasswordResetService {
    void sendPasswordResetCode(String email) throws EmailDoesNotExistException, MessagingException;

    void resetPassword(ResetPasswordDto passwordDto) throws BadPasswordResetCodeException, PasswordResetTriesExceededException;

    void changePassword(ChangePasswordDto passwordDto, String email) throws PasswordsDoNotMatch, PasswordIsNotValid, BadCredentialsException;
}
