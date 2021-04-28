package com.example.pki.service.impl;

import com.example.pki.exceptions.InvalidCharacterException;
import com.example.pki.model.User;
import com.example.pki.model.dto.LogInDto;
import com.example.pki.model.dto.UserTokenState;
import com.example.pki.security.TokenUtils;
import com.example.pki.service.LogInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LogInServiceImpl implements LogInService {

    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LogInServiceImpl(TokenUtils tokenUtils, AuthenticationManager authenticationManager) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserTokenState logIn(LogInDto authenticationRequest) {

        if (authenticationRequest.getEmail().contains("<") || authenticationRequest.getEmail().contains(">") ||
                authenticationRequest.getPassword().contains("<") || authenticationRequest.getPassword().contains(">")) {
            throw new InvalidCharacterException();
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        String userType = user.getClass().getSimpleName();
        String accessToken = tokenUtils.generateToken(username);
        int accessExpiresIn = tokenUtils.getExpiredIn();
        return new UserTokenState(userType, accessToken, accessExpiresIn);
    }
}
