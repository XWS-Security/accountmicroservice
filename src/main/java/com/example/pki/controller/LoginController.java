package com.example.pki.controller;

import com.example.pki.model.User;
import com.example.pki.model.dto.LogInDto;
import com.example.pki.model.dto.UserTokenState;
import com.example.pki.security.TokenUtils;
import com.example.pki.service.LogInService;
import com.example.pki.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {

    private final LogInService logInService;
    private final TokenUtils tokenUtils;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public LoginController(LogInService logInService, TokenUtils tokenUtils, CustomUserDetailsService userDetailsService) {
        this.logInService = logInService;
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/")
    public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody LogInDto authenticationRequest) {
        try {
            UserTokenState state = logInService.logIn(authenticationRequest);
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            return new ResponseEntity<UserTokenState>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<UserTokenState> refreshAuthenticationToken(HttpServletRequest request) {
        String token = tokenUtils.getToken(request);
        String username = this.tokenUtils.getUsernameFromToken(token);
        User user = (User) this.userDetailsService.loadUserByUsername(username);
        String userType = user.getClass().getSimpleName();
        if (this.tokenUtils.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = tokenUtils.refreshToken(token);
            int expiresIn = tokenUtils.getExpiredIn();
            return ResponseEntity.ok(new UserTokenState(userType, refreshedToken, expiresIn));
        } else {
            UserTokenState userTokenState = new UserTokenState();
            return ResponseEntity.badRequest().body(userTokenState);
        }
    }
}
