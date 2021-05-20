package com.example.pki.service.impl;

import com.example.pki.exceptions.BadUserInformationException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.UserDto;
import com.example.pki.repository.UserRepository;
import com.example.pki.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProfileServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto extractUserInfo() {
        return UserDto.convertUserToDto(getCurrentlyLoggedUser());
    }

    @Override
    public void updateUserInfo(UserDto userDto) {
        NistagramUser currentlyLoggedUser = getCurrentlyLoggedUser();

        currentlyLoggedUser.setAbout(userDto.getAbout());
        currentlyLoggedUser.setName(userDto.getName());
        currentlyLoggedUser.setSurname(userDto.getSurname());
        currentlyLoggedUser.setPhoneNumber(userDto.getPhoneNumber());

        if (!userDto.getUsername().equals(currentlyLoggedUser.getUsername())) {
            setUsername(userDto.getUsername());
        }

        if (!userDto.getEmail().equals(currentlyLoggedUser.getEmail())) {
            setUserEmail(userDto.getEmail());
        }

        userRepository.save(currentlyLoggedUser);
    }

    private NistagramUser getCurrentlyLoggedUser() {
        return (NistagramUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void setUserEmail(String email) {
        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();
        users.forEach(user -> {
            if (user.getEmail().equals(email)) {
                throw new BadUserInformationException();
            }
        });
        getCurrentlyLoggedUser().setEmail(email);
    }

    private void setUsername(String username) {
        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();
        users.forEach(user -> {
            if (user.getUsername().equals(username)) {
                throw new BadUserInformationException();
            }
        });
        getCurrentlyLoggedUser().setNistagramUsername(username);
    }

    private boolean checkPassword(String password) {
        return getCurrentlyLoggedUser().getPassword().equals(passwordEncoder.encode(password));
    }
}