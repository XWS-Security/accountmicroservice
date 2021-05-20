package com.example.pki.service;

import com.example.pki.exceptions.BadActivationCodeException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.UserDto;

import javax.mail.MessagingException;

public interface RegisterService {

    User activate(String email, String activationCode) throws BadActivationCodeException;

    User register(UserDto userDto, String siteURL) throws MessagingException;

    NistagramUser findByEmail(String email);

    NistagramUser findByUsername(String username);

    boolean userExists(String email, String username);
}
