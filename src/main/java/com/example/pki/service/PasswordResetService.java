package com.example.pki.service;

import com.example.pki.exceptions.*;
import com.example.pki.model.dto.ChangePasswordDto;
import com.example.pki.model.dto.ResetPasswordDto;
import com.example.pki.model.dto.UserTokenState;
import org.springframework.security.authentication.BadCredentialsException;

import javax.mail.MessagingException;

public interface PasswordResetService {
    void sendPasswordResetCode(String email) throws EmailDoesNotExistException, MessagingException;

    void resetPassword(ResetPasswordDto passwordDto) throws BadPasswordResetCodeException, PasswordResetTriesExceededException;

    UserTokenState changePassword(ChangePasswordDto passwordDto, String email) throws PasswordsDoNotMatch, PasswordIsNotValid, BadCredentialsException;
}
