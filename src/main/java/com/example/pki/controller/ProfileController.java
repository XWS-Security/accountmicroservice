package com.example.pki.controller;

import com.example.pki.exceptions.EmailAlreadyExistsException;
import com.example.pki.exceptions.UsernameAlreadyExistsException;
import com.example.pki.logging.LoggerService;
import com.example.pki.logging.LoggerServiceImpl;
import com.example.pki.model.User;
import com.example.pki.model.dto.UserDto;
import com.example.pki.security.TokenUtils;
import com.example.pki.service.ProfileService;
import com.example.pki.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class ProfileController {

    @Qualifier("profileServiceImpl")
    private final ProfileService profileService;
    private final TokenUtils tokenUtils;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    @Autowired
    public ProfileController(ProfileService profileService, TokenUtils tokenUtils) {
        this.profileService = profileService;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<UserDto> getUserInfo() {
        return new ResponseEntity<>(profileService.extractUserInfo(), HttpStatus.OK);
    }

    @PutMapping("/updateProfileInfo")
    public ResponseEntity<String> updateProfileInfo(@RequestBody @Valid UserDto userDto, HttpServletRequest request) {
        try {
            String token = tokenUtils.getToken(request);
            profileService.updateUserInfo(userDto, token);
            loggerService.logUpdateUserSuccess(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
            return new ResponseEntity<>("Profile info successfully updated!", HttpStatus.OK);

        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            loggerService.logUpdateUserFailed(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            loggerService.logUpdateUserFailed(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername(), e.getMessage());
            return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllNistagramUsers() {
        return new ResponseEntity<>(profileService.findAllNistagramUsers(), HttpStatus.OK);
    }

    @GetMapping("/searchUser/{nistagramUsername}")
    public ResponseEntity<List<UserDto>> getNistagramUserByUsername(@PathVariable("nistagramUsername") @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE) String nistagramUsername) {
        return new ResponseEntity<>(profileService.findNistagramUser(nistagramUsername), HttpStatus.OK);
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
