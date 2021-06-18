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
import com.example.pki.service.CertificateService;
import com.example.pki.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final NistagramUserRepository nistagramUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final CertificateService certificateService;

    @Value("${CONTENT}")
    private String contentMicroserviceURI;

    @Value("${FOLLOWER}")
    private String followerMicroserviceURI;

    @Autowired
    public ProfileServiceImpl(UserRepository userRepository, NistagramUserRepository nistagramUserRepository,
                              PasswordEncoder passwordEncoder, CertificateService certificateService) {
        this.userRepository = userRepository;
        this.nistagramUserRepository = nistagramUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.certificateService = certificateService;
    }

    @Override
    public UserDto extractUserInfo() {
        return UserDto.convertUserToDto(getCurrentlyLoggedUser());
    }

    @Override
    public void updateUserInfo(UserDto userDto, String token) throws SSLException {
        NistagramUser currentlyLoggedUser = getCurrentlyLoggedUser();

        FollowerMicroserviceUpdateUserDto followerMicroserviceUpdateUserDto =
                new FollowerMicroserviceUpdateUserDto(currentlyLoggedUser.getNistagramUsername(), userDto.getUsername(), userDto.isProfilePrivate(),
                        userDto.getAbout());

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

        updateUserInfoInContentMicroservice(followerMicroserviceUpdateUserDto, token);
        updateUserInfoInFollowerMicroservice(followerMicroserviceUpdateUserDto, token);

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

    @Override
    public void removeUser(Long userId, String token) throws SSLException {
        userRepository.deleteById(userId);

        WebClient client = WebClient.builder()
                .baseUrl(contentMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(certificateService.buildHttpClient()))
                .build();

        client.put()
                .uri("/profile/remove/" + userId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToFlux(long.class)
                .subscribe();
    }

    private void updateUserInfoInFollowerMicroservice(FollowerMicroserviceUpdateUserDto followerMicroserviceUserDto, String token) throws SSLException {
        WebClient client = WebClient.builder()
                .baseUrl(followerMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(certificateService.buildHttpClient()))
                .build();

        client.put()
                .uri("/users")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(h -> h.setBearerAuth(token))
                .body(Mono.just(followerMicroserviceUserDto), FollowerMicroserviceUserDto.class)
                .retrieve()
                .bodyToFlux(String.class)
                .subscribe();
    }

    private void updateUserInfoInContentMicroservice(FollowerMicroserviceUpdateUserDto followerMicroserviceUserDto, String token) throws SSLException {
        WebClient client = WebClient.builder()
                .baseUrl(contentMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(certificateService.buildHttpClient()))
                .build();

        client.put()
                .uri("/profile/updateUser")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(h -> h.setBearerAuth(token))
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
}
