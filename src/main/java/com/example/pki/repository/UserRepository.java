package com.example.pki.repository;

import com.example.pki.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    User findByNistagramUsername(String username);
}
