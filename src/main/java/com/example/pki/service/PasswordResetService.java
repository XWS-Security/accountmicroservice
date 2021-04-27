package com.example.pki.service;

import Exceptions.BadPasswordResetCodeException;
import Exceptions.EmailDoesNotExistException;
import com.example.pki.model.dto.ChangePasswordDto;

import javax.mail.MessagingException;

public interface PasswordResetService {
    void resetPassword(String email) throws EmailDoesNotExistException, MessagingException;

    void changePassword(ChangePasswordDto passwordDto) throws BadPasswordResetCodeException;
}
