package com.example.pki.service;

import com.example.pki.model.dto.LogInDto;
import com.example.pki.model.dto.UserTokenState;

import javax.mail.MessagingException;

public interface LogInService {

    UserTokenState logIn(LogInDto authenticationRequest);

    void sendTwoFactorAuthSecret(LogInDto authenticationRequest) throws MessagingException;
}
