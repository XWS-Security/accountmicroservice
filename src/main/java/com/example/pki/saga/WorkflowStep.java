package com.example.pki.saga;

import reactor.core.publisher.Mono;

public interface WorkflowStep {
    WorkflowStepStatus getStatus();

    Mono<Boolean> process();

    Mono<Boolean> revert();
}
