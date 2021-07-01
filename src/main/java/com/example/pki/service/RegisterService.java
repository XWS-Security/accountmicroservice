package com.example.pki.service;

import com.example.pki.exceptions.BadActivationCodeException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.RegisterDto;
import com.example.pki.model.dto.saga.CreateUserOrchestratorResponse;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import javax.net.ssl.SSLException;

public interface RegisterService {

    User activate(String email, String activationCode) throws BadActivationCodeException;

    Mono<CreateUserOrchestratorResponse> register(RegisterDto registerDto, String siteURL) throws MessagingException, SSLException;

    NistagramUser findByEmail(String email);

    NistagramUser findByUsername(String username);

    boolean userExists(String email, String username);
}
