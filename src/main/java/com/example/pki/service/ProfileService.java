package com.example.pki.service;

import com.example.pki.model.dto.UserDto;

import java.util.List;

public interface ProfileService {

    UserDto extractUserInfo();

    void updateUserInfo(UserDto userDto);

    List<UserDto> findAllNistagramUsers();

    List<UserDto> findNistagramUser(String nistagramUsername);
}
