package com.example.pki.saga.updateuser;

import com.example.pki.model.NistagramUser;
import com.example.pki.repository.UserRepository;
import com.example.pki.saga.WorkflowStep;
import com.example.pki.saga.WorkflowStepStatus;
import reactor.core.publisher.Mono;

public class UpdateUserInAccountMicroserviceWorkflowStep implements WorkflowStep {
    private final NistagramUser oldUser;
    private final NistagramUser newUser;
    private final UserRepository userRepository;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public UpdateUserInAccountMicroserviceWorkflowStep(NistagramUser oldUser, NistagramUser newUser, UserRepository userRepository) {
        this.oldUser = oldUser;
        this.newUser = newUser;
        this.userRepository = userRepository;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public Mono<Boolean> process() {
        userRepository.save(newUser);
        this.status = WorkflowStepStatus.COMPLETE;
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> revert() {
        newUser.setNistagramUsername(oldUser.getNistagramUsername());
        newUser.setName(oldUser.getName());
        newUser.setSurname(oldUser.getSurname());
        newUser.setEmail(oldUser.getEmail());
        newUser.setPhoneNumber(oldUser.getPhoneNumber());
        newUser.setAbout(oldUser.getAbout());
        newUser.setProfilePrivate(oldUser.isProfilePrivate());
        newUser.setMessagesEnabled(oldUser.isMessagesEnabled());
        newUser.setTagsEnabled(oldUser.isTagsEnabled());
        userRepository.save(newUser);
        return Mono.just(true);
    }
}
