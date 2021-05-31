package com.example.pki.service.impl;

import com.example.pki.exceptions.BadUserInformationException;
import com.example.pki.exceptions.InvalidCharacterException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.UserDto;
import com.example.pki.repository.NistagramUserRepository;
import com.example.pki.repository.UserRepository;
import com.example.pki.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final NistagramUserRepository nistagramUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProfileServiceImpl(UserRepository userRepository, NistagramUserRepository nistagramUserRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.nistagramUserRepository = nistagramUserRepository;
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

    @Override
    public List<UserDto> findAllNistagramUsers() {
        ArrayList<UserDto> usersDto = new ArrayList<>();
        ArrayList<NistagramUser> users = (ArrayList<NistagramUser>) nistagramUserRepository.findAll();
        users.forEach(user -> {
            usersDto.add(UserDto.convertUserToDto(user));
        });
        return usersDto;
    }

    @Override
    public List<UserDto> findNistagramUser(String nistagramUsername) {
        checkForInvalidSigns(nistagramUsername);

        ArrayList<NistagramUser> users = (ArrayList<NistagramUser>) nistagramUserRepository.findAll();
        ArrayList<UserDto> userDtos = new ArrayList<>();

        users.forEach(user -> {
            if (user.getNistagramUsername().contains(nistagramUsername)) {
                userDtos.add(UserDto.convertUserToDto(user));
            }
        });
        return userDtos;
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

    private void checkForInvalidSigns(String dtoParameter) {
        if (dtoParameter.contains(">") || dtoParameter.contains("<")) {
            throw new InvalidCharacterException();
        }
    }

    private boolean checkPassword(String password) {
        return getCurrentlyLoggedUser().getPassword().equals(passwordEncoder.encode(password));
    }
}
