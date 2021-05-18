package com.example.pki.service;

import com.example.pki.exceptions.BadActivationCodeException;
import com.example.pki.model.InstagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.RegisterDto;

import javax.mail.MessagingException;

public interface RegisterService {

    User activate(String email, String activationCode) throws BadActivationCodeException;

    User register(RegisterDto registerDto, String siteURL) throws MessagingException;

    InstagramUser findByEmail(String email);

    InstagramUser findByUsername(String username);

    boolean userExists(String email, String username);
}
