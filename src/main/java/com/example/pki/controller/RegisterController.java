package com.example.pki.controller;

import com.example.pki.exceptions.*;
import com.example.pki.logging.LoggerService;
import com.example.pki.logging.LoggerServiceImpl;
import com.example.pki.model.User;
import com.example.pki.model.dto.*;
import com.example.pki.model.dto.saga.CreateUserOrchestratorResponse;
import com.example.pki.service.PasswordResetService;
import com.example.pki.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class RegisterController {

    @Qualifier("registerServiceImpl")
    private final RegisterService registerService;
    @Qualifier("passwordResetServiceImpl")
    private final PasswordResetService passwordResetService;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    private final static String registrationFailedAlert = "Registration failed!";
    private final static String missingBasicUserInfoAlert = "Registration failed! Missing email or password";
    private final static String mailCannotBeSent = "There's been an issue with our mailing service, please try again.";

    @Autowired
    public RegisterController(RegisterService registerService, PasswordResetService passwordResetService) {
        this.registerService = registerService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/")
    public Mono<CreateUserOrchestratorResponse> registerUser(HttpServletRequest request, @RequestBody @Valid RegisterDto dto) {
        try {
            loggerService.logCreateUser(dto.getUsername());
            return this.registerService.register(dto, getSiteURL(request));
        } catch (PasswordIsNotValid | PasswordsDoNotMatch | UserAlreadyExistsException e) {
            loggerService.logCreateUserFail(dto.getUsername(), e.getMessage());
            return Mono.just(new CreateUserOrchestratorResponse(dto.getUsername(), false, e.getMessage()));
        } catch (Exception e) {
            loggerService.logCreateUserFail(dto.getUsername(), e.getMessage());
            return Mono.just(new CreateUserOrchestratorResponse(dto.getUsername(), false, "Something went wrong"));
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestBody @Valid ActivateDto dto) {
        String email = dto.getEmail();
        String code = dto.getCode();
        try {
            this.registerService.activate(email, code);
            loggerService.activationSuccess(email);
            return new ResponseEntity<>("/activation/success", HttpStatus.OK);

        } catch (BadActivationCodeException | RegistrationTimeExpiredException e) {
            loggerService.activationFailed(email, e.getMessage());
            return new ResponseEntity<>("/activation/failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/triggerReset")
    public ResponseEntity<String> triggerResetPassword(@RequestBody @Valid TriggerResetPasswordDto triggerResetPasswordDto) {
        try {
            passwordResetService.sendPasswordResetCode(triggerResetPasswordDto.getEmail());
            loggerService.triggerResetPasswordSuccess(triggerResetPasswordDto.getEmail());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (EmailDoesNotExistException e) {
            loggerService.triggerResetPasswordFailed(triggerResetPasswordDto.getEmail(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (MessagingException e) {
            loggerService.triggerResetPasswordFailed(triggerResetPasswordDto.getEmail(), e.getMessage());
            return new ResponseEntity<>(mailCannotBeSent, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/registerAgent")
    public ResponseEntity<String> registerAgent(@RequestBody @Valid RegisterAgentDTO registerAgentDTO) {
        try {
            registerService.registerAgent(registerAgentDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        try {
            passwordResetService.resetPassword(resetPasswordDto);
            loggerService.passwordResetSuccess(resetPasswordDto.getEmail());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (BadPasswordResetCodeException | PasswordsDoNotMatch | PasswordIsNotValid | PasswordResetTriesExceededException e) {
            loggerService.passwordResetFailed(resetPasswordDto.getEmail(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/change")
    public ResponseEntity<UserTokenState> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto) {
        try {
            UserTokenState state = passwordResetService.changePassword(changePasswordDto, getSignedInUser().getEmail());
            loggerService.passwordChangeSuccess(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
            return new ResponseEntity<>(state, HttpStatus.OK);

        } catch (Exception e) {
            loggerService.passwordChangeFailed(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
