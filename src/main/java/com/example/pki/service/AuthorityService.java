package com.example.pki.service;

import com.example.pki.model.Authority;

import java.util.List;

public interface AuthorityService {
    List<Authority> findById(Long id);
    List<Authority> findByname(String name);
}
