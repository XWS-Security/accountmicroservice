package com.example.pki.repository;

import com.example.pki.model.NistagramUser;
import org.springframework.data.repository.CrudRepository;

public interface NistagramUserRepository extends CrudRepository<NistagramUser, Long> {

    NistagramUser findNistagramUserByNistagramUsername(String nistagramUsername);
}
