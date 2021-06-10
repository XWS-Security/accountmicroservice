package com.example.pki.service.impl;

import com.example.pki.exceptions.InvalidTwoFactorAuthSecretException;
import com.example.pki.exceptions.TwoFactorAuthSecretTriesExceededException;
import com.example.pki.mail.MailService;
import com.example.pki.mail.PasswordResetMailFormatter;
import com.example.pki.model.User;
import com.example.pki.model.dto.LogInDto;
import com.example.pki.model.dto.UserTokenState;
import com.example.pki.repository.UserRepository;
import com.example.pki.security.TokenUtils;
import com.example.pki.service.LogInService;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class LogInServiceImpl implements LogInService {

    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;
    private final MailService<String> mailService;
    private final UserRepository userRepository;

    @Autowired
    public LogInServiceImpl(TokenUtils tokenUtils, AuthenticationManager authenticationManager, MailService<String> mailService, UserRepository userRepository) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.mailService = mailService;
        this.userRepository = userRepository;
    }

    @Override
    public UserTokenState logIn(LogInDto authenticationRequest) {
        User user = getUser(authenticationRequest);

        if (!authenticationRequest.getTwoFactorAuthenticationSecret().equals(user.getTwoFactorAuthSecret())) {
            user.incrementTwoAuthFactorCount();
            if (user.getTwoFactorAuthCount() >= 3) {
                user.resetTwoAuthFactorCount();
                userRepository.save(user);
                throw new TwoFactorAuthSecretTriesExceededException();
            }
            userRepository.save(user);
            throw new InvalidTwoFactorAuthSecretException();

        } else {
            String username = user.getUsername();
            String userType = user.getClass().getSimpleName();
            String accessToken = tokenUtils.generateToken(username);
            int accessExpiresIn = tokenUtils.getExpiredIn();
            user.resetTwoAuthFactorCount();
            userRepository.save(user);
            return new UserTokenState(userType, accessToken, accessExpiresIn);
        }
    }

    @Override
    public void sendTwoFactorAuthSecret(LogInDto authenticationRequest) throws MessagingException {
        User user = getUser(authenticationRequest);
        String secret = Base32.random();
        user.setTwoFactorAuthSecret(secret);
        mailService.sendMail(user.getEmail(), secret, new PasswordResetMailFormatter());
        userRepository.save(user);
    }

    private User getUser(LogInDto authenticationRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return (User) authentication.getPrincipal();
    }
}
