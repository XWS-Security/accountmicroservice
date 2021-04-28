package com.example.pki.controller;

import com.example.pki.exceptions.*;
import com.example.pki.model.User;
import com.example.pki.model.dto.*;
import com.example.pki.service.PasswordResetService;
import com.example.pki.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
public class RegisterController {

    @Qualifier("registerServiceImpl")
    private final RegisterService registerService;
    @Qualifier("passwordResetServiceImpl")
    private final PasswordResetService passwordResetService;

    private final static String userExistsAlert = "User with that mail address already exists!";
    private final static String registrationFailedAlert = "Registration failed!";
    private final static String missingBasicUserInfoAlert = "Registration failed! Missing email or password";
    private final static String mailCannotBeSent = "There's been an issue with our mailing service, please try again.";

    @Autowired
    public RegisterController(RegisterService registerService, PasswordResetService passwordResetService) {
        this.registerService = registerService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/")
    public ResponseEntity<String> registerUser(HttpServletRequest request, @RequestBody RegisterDto dto) {
        if (!validUserInfo(dto.getEmail(), dto.getPassword())) {
            return new ResponseEntity<>(missingBasicUserInfoAlert, HttpStatus.BAD_REQUEST);
        }
        if (this.registerService.userExists(dto.getEmail())) {
            return new ResponseEntity<>(userExistsAlert, HttpStatus.BAD_REQUEST);
        }

        try {
            this.registerService.register(dto, getSiteURL(request));
            return new ResponseEntity<>("/emailSent", HttpStatus.OK);
        } catch (BadUserInformationException e) {
            return new ResponseEntity<>(userExistsAlert, HttpStatus.BAD_REQUEST);
        } catch (PasswordIsNotValid | PasswordsDoNotMatch e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InvalidCharacterException e) {
            return new ResponseEntity<>("Fields can not contain less/greater than signs.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(registrationFailedAlert, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestBody ActivateDto dto) {
        String email = dto.getEmail();
        String code = dto.getCode();
        try {
            this.registerService.activate(email, code);
            return new ResponseEntity<>("/activation/success", HttpStatus.OK);
        } catch (BadActivationCodeException | RegistrationTimeExpiredException e) {
            return new ResponseEntity<>("/activation/failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/triggerReset")
    public ResponseEntity<String> triggerResetPassword(@RequestBody TriggerResetPasswordDto triggerResetPasswordDto) {
        try {
            passwordResetService.sendPasswordResetCode(triggerResetPasswordDto.getEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EmailDoesNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (MessagingException e) {
            return new ResponseEntity<>(mailCannotBeSent, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        try {
            passwordResetService.resetPassword(resetPasswordDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (BadPasswordResetCodeException | PasswordsDoNotMatch | PasswordIsNotValid | PasswordResetTriesExceededException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/change")
    public ResponseEntity<UserTokenState> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        try {
            UserTokenState state = passwordResetService.changePassword(changePasswordDto, getSignedInUser().getEmail());
            return new ResponseEntity<>(state, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String getSiteURL(HttpServletRequest request) {
        return request.getHeader("origin");
    }

    private boolean validUserInfo(String email, String password) {
        return email != null && !email.isEmpty() && password != null && !password.isEmpty();
    }

    private User getSignedInUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
