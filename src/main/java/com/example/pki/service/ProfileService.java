package com.example.pki.service;

import com.example.pki.model.dto.UserDto;
import com.example.pki.model.dto.saga.CreateUserOrchestratorResponse;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLException;
import java.util.List;

public interface ProfileService {

    UserDto extractUserInfo();

    Mono<CreateUserOrchestratorResponse> updateUserInfo(UserDto userDto, String token) throws SSLException;

    List<UserDto> findAllNistagramUsers();

    List<UserDto> findNistagramUser(String nistagramUsername);

    String getUsername();
}
