package com.example.pki.repository;

import com.example.pki.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
