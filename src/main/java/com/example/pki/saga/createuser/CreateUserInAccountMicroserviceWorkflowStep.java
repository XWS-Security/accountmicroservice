package com.example.pki.saga.createuser;

import com.example.pki.model.NistagramUser;
import com.example.pki.repository.UserRepository;
import com.example.pki.saga.WorkflowStep;
import com.example.pki.saga.WorkflowStepStatus;
import reactor.core.publisher.Mono;

public class CreateUserInAccountMicroserviceWorkflowStep implements WorkflowStep {
    private final NistagramUser user;
    private final UserRepository userRepository;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public CreateUserInAccountMicroserviceWorkflowStep(NistagramUser user, UserRepository userRepository) {
        this.user = user;
        this.userRepository = userRepository;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public Mono<Boolean> process() {
        userRepository.save(user);
        this.status = WorkflowStepStatus.COMPLETE;
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> revert() {
        userRepository.delete(user);
        return Mono.just(true);
    }
}
