package com.example.pki.service;

import com.example.pki.model.InstagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.LogInDto;
import com.example.pki.model.dto.RegisterDto;

import javax.mail.MessagingException;

public interface RegisterService {

    User activate(String email, String activationCode);

    User register(RegisterDto registerDto, String siteURL) throws MessagingException;

    boolean userExists(String email);

    InstagramUser findByEmail(String email);
}
