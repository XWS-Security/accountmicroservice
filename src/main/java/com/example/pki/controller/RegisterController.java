package com.example.pki.controller;

import Exceptions.BadActivationCodeException;
import Exceptions.BadUserInformationException;
import Exceptions.PasswordIsNotValid;
import Exceptions.PasswordsDoNotMatch;
import com.example.pki.model.dto.ActivateDto;
import com.example.pki.model.dto.RegisterDto;
import com.example.pki.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
public class RegisterController {

    @Qualifier("registerServiceImpl")
    private final RegisterService registerService;

    private final static String userExistsAlert = "User with that mail address already exists!";
    private final static String passwordIsNotValid = "Password must contain one uppercase letter, one special character, and digit! Minimum length is 10 characters";
    private final static String passwordDoNotMatch = "Passwords must match!";
    private final static String registrationFailedAlert = "Registration failed!";
    private final static String missingBasicUserInfoAlert = "Registration failed! Missing email or password";

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
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
        } catch (PasswordIsNotValid e) {
            return new ResponseEntity<>(passwordIsNotValid, HttpStatus.BAD_REQUEST);
        } catch (PasswordsDoNotMatch e) {
            return new ResponseEntity<>(passwordDoNotMatch, HttpStatus.BAD_REQUEST);
        }
        catch(Exception e){
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
        } catch (BadActivationCodeException e) {
            return new ResponseEntity<>("/activation/failed", HttpStatus.BAD_REQUEST);
        }
    }

    private String getSiteURL(HttpServletRequest request) {
        return request.getHeader("origin");
    }

    private boolean validUserInfo(String email, String password) {
        return email != null && !email.isEmpty() && password != null && !password.isEmpty();
    }
}
