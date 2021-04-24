package com.example.pki.service;

import com.example.pki.model.Role;

import java.util.List;

public interface AuthorityService {
    List<Role> findById(Long id);
    List<Role> findByname(String name);
}
