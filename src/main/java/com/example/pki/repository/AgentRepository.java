package com.example.pki.repository;

import com.example.pki.model.Agent;
import org.springframework.data.repository.CrudRepository;

public interface AgentRepository extends CrudRepository<Agent, Long>{
    Agent findByNistagramUsername(String username);
}
