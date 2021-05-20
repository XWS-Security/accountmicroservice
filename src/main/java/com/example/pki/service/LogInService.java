package com.example.pki.service;

import com.example.pki.model.dto.LogInDto;
import com.example.pki.model.dto.UserTokenState;

public interface LogInService {

    UserTokenState logIn(LogInDto authenticationRequest);
}
