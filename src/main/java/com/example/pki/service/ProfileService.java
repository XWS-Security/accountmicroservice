package com.example.pki.service;

import com.example.pki.model.dto.UserDto;

import javax.net.ssl.SSLException;
import java.util.List;

public interface ProfileService {

    UserDto extractUserInfo();

    void updateUserInfo(UserDto userDto, String token) throws SSLException;

    List<UserDto> findAllNistagramUsers();

    List<UserDto> findNistagramUser(String nistagramUsername);
}
