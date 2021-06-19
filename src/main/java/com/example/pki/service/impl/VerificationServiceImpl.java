package com.example.pki.service.impl;

import com.example.pki.exceptions.ObjectNotFoundException;
import com.example.pki.exceptions.UserNotFoundException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.VerificationRequest;
import com.example.pki.model.dto.VerificationRequestDto;
import com.example.pki.model.enums.VerificationStatus;
import com.example.pki.repository.NistagramUserRepository;
import com.example.pki.repository.VerificationRequestRepository;
import com.example.pki.service.VerificationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRequestRepository verificationRequestRepository;
    private final NistagramUserRepository nistagramUserRepository;

    public VerificationServiceImpl(VerificationRequestRepository verificationRequestRepository, NistagramUserRepository nistagramUserRepository) {
        this.verificationRequestRepository = verificationRequestRepository;
        this.nistagramUserRepository = nistagramUserRepository;
    }


    @Override
    public VerificationStatus getVerificationStatus(String username) {
        var user = nistagramUserRepository.findNistagramUserByNistagramUsername(username);
        if (user != null) {
            var status = user.getVerificationStatus();
            if (status != null) return status;
            return VerificationStatus.NOT_VERIFIED;
        }
        throw new UserNotFoundException(username);
    }

    @Override
    public void requestVerification(VerificationRequestDto dto) {
        try {
            System.out.println(dto.getCategory());
            NistagramUser user = getCurrentlyLoggedUser();
            verificationRequestRepository.save(
                    new VerificationRequest(dto.getOfficialDocumentImageName(), dto.getCategory(), user));
            user.setVerificationStatus(VerificationStatus.REQUESTED_VERIFICATION);
            nistagramUserRepository.save(user);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Long> getUnresolvedVerificationRequests() {
        List<Long> ids = new ArrayList<>();
        verificationRequestRepository.findAll().forEach(verificationRequest -> {
            if (!verificationRequest.isResolved())
                ids.add(verificationRequest.getId());
        });
        return ids;
    }

    @Override
    public VerificationRequestDto getVerification(Long id) {
        var optional = verificationRequestRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ObjectNotFoundException(id);
        }
        var verification = optional.get();

        var dto = new VerificationRequestDto(
                verification.getStatus(),
                verification.getOfficialDocumentImageName(),
                verification.getUser().getName(),
                verification.getUser().getSurname());

        return dto;
    }

    private VerificationRequest resolve(Long id) {
        var optional = verificationRequestRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ObjectNotFoundException(id);
        }
        var request = optional.get();
        request.setResolved(true);
        return request;
    }

    @Override
    public void approve(Long id) {
        try {
            var request = resolve(id);
            request.setApproved(true);
            var user = request.getUser();
            user.setVerificationStatus(VerificationStatus.VERIFIED);
            nistagramUserRepository.save(user);
            verificationRequestRepository.save(request);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void reject(Long id) {
        try {
            var request = resolve(id);
            request.setApproved(false);
            var user = request.getUser();
            user.setVerificationStatus(VerificationStatus.NOT_VERIFIED);
            nistagramUserRepository.save(user);
            verificationRequestRepository.save(request);
        } catch (Exception e) {
            throw e;
        }
    }

    private NistagramUser getCurrentlyLoggedUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            throw new UserNotFoundException();
        } else {
            return (NistagramUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
    }
}
