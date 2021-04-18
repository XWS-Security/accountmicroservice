package com.example.pki.service;

import com.example.pki.model.InstagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.LogInDto;

public interface RegisterService {

    User activate(String email, String activationCode);

    User register(LogInDto dto, String siteURL);

    boolean userExists(String email);

    InstagramUser findByEmail(String email);
}
