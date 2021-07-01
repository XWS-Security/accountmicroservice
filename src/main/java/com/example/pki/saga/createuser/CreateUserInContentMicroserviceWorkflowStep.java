package com.example.pki.saga.createuser;

import com.example.pki.model.dto.FollowerMicroserviceUserDto;
import com.example.pki.model.dto.saga.FollowerMicroserviceCreateUserResponse;
import com.example.pki.saga.WorkflowStep;
import com.example.pki.saga.WorkflowStepStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class CreateUserInContentMicroserviceWorkflowStep implements WorkflowStep {
    private final WebClient webClient;
    private final FollowerMicroserviceUserDto userDto;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public CreateUserInContentMicroserviceWorkflowStep(WebClient webClient, FollowerMicroserviceUserDto userDto) {
        this.webClient = webClient;
        this.userDto = userDto;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public Mono<Boolean> process() {
        return webClient.post()
                .uri("/profile/createNistagramUser")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(userDto), FollowerMicroserviceUserDto.class)
                .retrieve()
                .bodyToMono(FollowerMicroserviceCreateUserResponse.class)
                .map(FollowerMicroserviceCreateUserResponse::isSuccess)
                .doOnNext(b -> this.status = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED);
    }

    @Override
    public Mono<Boolean> revert() {
        return this.webClient
                .post()
                .uri("/profile/deleteNistagramUser")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(this.userDto), CreateUserInFollowerMicroserviceWorkflowStep.class)
                .retrieve()
                .bodyToMono(Void.class)
                .map(r -> true)
                .onErrorReturn(false);
    }
}
