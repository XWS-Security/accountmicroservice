package com.example.pki.service;

import com.example.pki.model.dto.UserDto;

public interface ProfileService {

    UserDto extractUserInfo();

    void updateUserInfo(UserDto userDto);
}
