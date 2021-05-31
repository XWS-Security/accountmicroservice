package com.example.pki.controller;

import com.example.pki.exceptions.EmailAlreadyExistsException;
import com.example.pki.exceptions.UsernameAlreadyExistsException;
import com.example.pki.model.dto.UserDto;
import com.example.pki.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileController {

    @Qualifier("profileServiceImpl")
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<UserDto> getUserInfo() {
        return new ResponseEntity<>(profileService.extractUserInfo(), HttpStatus.OK);
    }

    @PutMapping("/updateProfileInfo")
    public ResponseEntity<String> updateProfileInfo(@RequestBody @Valid UserDto userDto) {
        try {
            profileService.updateUserInfo(userDto);
            return new ResponseEntity<>("Profile info successfully updated!", HttpStatus.OK);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllNistagramUsers() {
        return new ResponseEntity<>(profileService.findAllNistagramUsers(), HttpStatus.OK);
    }

    @GetMapping("/searchUser/{nistagramUsername}")
    public ResponseEntity<List<UserDto>> getNistagramUserByUsername(@PathVariable("nistagramUsername") String nistagramUsername) {
        return new ResponseEntity<>(profileService.findNistagramUser(nistagramUsername), HttpStatus.OK);
    }
}
