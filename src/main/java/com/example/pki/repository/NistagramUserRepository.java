package com.example.pki.repository;

import com.example.pki.model.NistagramUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NistagramUserRepository extends CrudRepository<NistagramUser, Long> {

    NistagramUser findNistagramUserByNistagramUsername(String nistagramUsername);

    @Query(value = "SELECT distinct u.nistagram_username FROM gram_user as u , verification_request as vr WHERE u.id=vr.user_id AND vr.approved=true AND vr.category=0", nativeQuery = true)
    List<String> findInfluencers();

    @Query(value = "SELECT distinct u.nistagram_username FROM gram_user as u , verification_request as vr WHERE u.nistagram_username=:username AND u.id=vr.user_id AND vr.approved=true AND vr.category=0", nativeQuery = true)
    List<String> findInfluencers(String username);
}
