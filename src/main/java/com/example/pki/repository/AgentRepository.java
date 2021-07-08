package com.example.pki.repository;

import com.example.pki.model.TokenOwner;
import org.springframework.data.repository.CrudRepository;

public interface AgentRepository extends CrudRepository<TokenOwner, Long>{
    TokenOwner findByNistagramUsername(String username);
}
