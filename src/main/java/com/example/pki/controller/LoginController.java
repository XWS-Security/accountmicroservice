package com.example.pki.controller;

import com.example.pki.logging.LoggerService;
import com.example.pki.logging.LoggerServiceImpl;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {

    private final LogInService logInService;
    private final TokenUtils tokenUtils;
    private final CustomUserDetailsService userDetailsService;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    @Autowired
    public LoginController(LogInService logInService, TokenUtils tokenUtils, CustomUserDetailsService userDetailsService) {
        this.logInService = logInService;
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/twoFactorAuth")
    public ResponseEntity<String> sendTwoFactorAuthSecret(@RequestBody @Valid LogInDto authenticationRequest) {
        try {
            logInService.sendTwoFactorAuthSecret(authenticationRequest);
            loggerService.sendTwoFactorAuthenticationSecretSuccess(authenticationRequest.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            loggerService.sendTwoFactorAuthenticationSecretFailed(authenticationRequest.getUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/")
    public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody @Valid LogInDto authenticationRequest) {
        try {
            UserTokenState state = logInService.logIn(authenticationRequest);
            loggerService.loginSuccess(authenticationRequest.getUsername());
            return ResponseEntity.ok(state);

        } catch (Exception e) {
            loggerService.loginFailed(authenticationRequest.getUsername(), e.getMessage());
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

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        loggerService.logValidationFailed(e.getMessage());
        return new ResponseEntity<>("Invalid characters in request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        loggerService.logValidationFailed(e.getMessage());
        return new ResponseEntity<>("Invalid characters in request", HttpStatus.BAD_REQUEST);
    }
}
