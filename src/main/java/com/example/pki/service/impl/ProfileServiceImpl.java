package com.example.pki.service.impl;

import com.example.pki.exceptions.EmailAlreadyExistsException;
import com.example.pki.exceptions.UsernameAlreadyExistsException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.UserDto;
import com.example.pki.model.dto.saga.CreateUserOrchestratorResponse;
import com.example.pki.repository.NistagramUserRepository;
import com.example.pki.repository.UserRepository;
import com.example.pki.saga.updateuser.UpdateUserOrchestrator;
import com.example.pki.service.CertificateService;
import com.example.pki.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public Mono<CreateUserOrchestratorResponse> updateUserInfo(UserDto userDto, String token) throws SSLException {
        NistagramUser newUser = getCurrentlyLoggedUser();
        NistagramUser oldUser = (NistagramUser) userRepository.findByNistagramUsername(newUser.getNistagramUsername());

        newUser.setAbout(userDto.getAbout());
        newUser.setName(userDto.getName());
        newUser.setSurname(userDto.getSurname());
        newUser.setPhoneNumber(userDto.getPhoneNumber());
        newUser.setProfilePrivate(userDto.isProfilePrivate());
        newUser.setMessagesEnabled(userDto.isMessagesEnabled());
        newUser.setTagsEnabled(userDto.isTagsEnabled());

        if (!userDto.getUsername().equals(newUser.getUsername())) {
            setUsername(userDto.getUsername());
        }

        if (!userDto.getEmail().equals(newUser.getEmail())) {
            setUserEmail(userDto.getEmail());
        }

        UpdateUserOrchestrator orchestrator = new UpdateUserOrchestrator(getFollowerMicroserviceWebClient(),
                updateUserInfoInContentMicroservice(), userRepository, token);
        return orchestrator.createUser(oldUser, newUser);
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

    private WebClient getFollowerMicroserviceWebClient() throws SSLException {
        return WebClient.builder()
                .baseUrl(followerMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(certificateService.buildHttpClient()))
                .build();
    }

    private WebClient updateUserInfoInContentMicroservice() throws SSLException {
        return WebClient.builder()
                .baseUrl(contentMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(certificateService.buildHttpClient()))
                .build();
    }

    private NistagramUser getCurrentlyLoggedUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            return null;
        } else {
            return (NistagramUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
    }

    @Override
    public String getUsername() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            return null;
        } else {
            var user = (NistagramUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getUsername();
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
