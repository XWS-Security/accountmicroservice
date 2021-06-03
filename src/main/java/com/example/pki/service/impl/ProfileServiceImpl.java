package com.example.pki.service.impl;

import com.example.pki.exceptions.EmailAlreadyExistsException;
import com.example.pki.exceptions.InvalidCharacterException;
import com.example.pki.exceptions.UsernameAlreadyExistsException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.FollowerMicroserviceUpdateUserDto;
import com.example.pki.model.dto.FollowerMicroserviceUserDto;
import com.example.pki.model.dto.UserDto;
import com.example.pki.repository.NistagramUserRepository;
import com.example.pki.repository.UserRepository;
import com.example.pki.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final NistagramUserRepository nistagramUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${CONTENT}")
    private String contentMicroserviceURI;

    @Value("${FOLLOWER}")
    private String followerMicroserviceURI;

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

        FollowerMicroserviceUpdateUserDto followerMicroserviceUpdateUserDto =
                new FollowerMicroserviceUpdateUserDto(currentlyLoggedUser.getNistagramUsername(), userDto.getUsername(), userDto.isProfilePrivate());

        currentlyLoggedUser.setAbout(userDto.getAbout());
        currentlyLoggedUser.setName(userDto.getName());
        currentlyLoggedUser.setSurname(userDto.getSurname());
        currentlyLoggedUser.setPhoneNumber(userDto.getPhoneNumber());
        currentlyLoggedUser.setProfilePrivate(userDto.isProfilePrivate());

        if (!userDto.getUsername().equals(currentlyLoggedUser.getUsername())) {
            setUsername(userDto.getUsername());
        }

        if (!userDto.getEmail().equals(currentlyLoggedUser.getEmail())) {
            setUserEmail(userDto.getEmail());
        }

        updateUserInfoInContentMicroservice(followerMicroserviceUpdateUserDto);
        updateUserInfoInFollowerMicroservice(followerMicroserviceUpdateUserDto);

        userRepository.save(currentlyLoggedUser);
    }

    @Override
    public List<UserDto> findAllNistagramUsers() {

        if (getCurrentlyLoggedUser() != null) {
            ArrayList<UserDto> usersDto = new ArrayList<>();
            ArrayList<NistagramUser> users = (ArrayList<NistagramUser>) nistagramUserRepository.findAll();
            users.forEach(user -> {
                usersDto.add(UserDto.convertUserToDto(user));
            });
            return usersDto;
        } else {
            ArrayList<UserDto> usersDto = new ArrayList<>();
            ArrayList<NistagramUser> users = (ArrayList<NistagramUser>) nistagramUserRepository.findAll();
            users.forEach(user -> {
                if (!user.isProfilePrivate()) {
                    usersDto.add(UserDto.convertUserToDto(user));
                }
            });
            return usersDto;
        }
    }

    @Override
    public List<UserDto> findNistagramUser(String nistagramUsername) {
        checkForInvalidSigns(nistagramUsername);

        ArrayList<NistagramUser> users = (ArrayList<NistagramUser>) nistagramUserRepository.findAll();
        ArrayList<UserDto> userDtos = new ArrayList<>();

        if (getCurrentlyLoggedUser() != null) {
            users.forEach(user -> {
                if (user.getNistagramUsername().contains(nistagramUsername)) {
                    userDtos.add(UserDto.convertUserToDto(user));
                }
            });
        } else {
            users.forEach(user -> {
                if (user.getNistagramUsername().contains(nistagramUsername) && !user.isProfilePrivate()) {
                    userDtos.add(UserDto.convertUserToDto(user));
                }
            });
        }
        return userDtos;
    }

    private void updateUserInfoInFollowerMicroservice(FollowerMicroserviceUpdateUserDto followerMicroserviceUserDto) {
        WebClient client = WebClient.builder()
                .baseUrl(followerMicroserviceURI)
                .build();

        client.put()
                .uri("/users")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(followerMicroserviceUserDto), FollowerMicroserviceUserDto.class)
                .retrieve()
                .bodyToFlux(String.class)
                .subscribe();
    }

    private void updateUserInfoInContentMicroservice(FollowerMicroserviceUpdateUserDto followerMicroserviceUserDto) {
        WebClient client = WebClient.builder()
                .baseUrl(contentMicroserviceURI)
                .build();

        client.put()
                .uri("/profile/updateUser")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(followerMicroserviceUserDto), FollowerMicroserviceUserDto.class)
                .retrieve()
                .bodyToFlux(String.class)
                .subscribe();
    }

    private NistagramUser getCurrentlyLoggedUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            return null;
        } else {
            return (NistagramUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
    }

    private void setUserEmail(String email) {
        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();
        users.forEach(user -> {
            if (user.getEmail().equals(email)) {
                throw new EmailAlreadyExistsException();
            }
        });
        getCurrentlyLoggedUser().setEmail(email);
    }

    private void setUsername(String username) {
        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();
        users.forEach(user -> {
            if (user.getUsername().equals(username)) {
                throw new UsernameAlreadyExistsException();
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
