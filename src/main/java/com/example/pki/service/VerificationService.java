package com.example.pki.service;

import com.example.pki.model.dto.RegisterAgentDTO;
import com.example.pki.model.dto.VerificationRequestDto;
import com.example.pki.model.enums.VerificationStatus;

import java.util.List;

public interface VerificationService {
    VerificationStatus getVerificationStatus(String username);

    void requestVerification(VerificationRequestDto dto);

    List<Long> getUnresolvedVerificationRequests();

    VerificationRequestDto getVerification(Long id);

    void approve(Long id);

    void reject(Long id);

    List<String> getInfluencers(String username);

    List<RegisterAgentDTO> getAgents();

    void approveAgent(String username);
}
