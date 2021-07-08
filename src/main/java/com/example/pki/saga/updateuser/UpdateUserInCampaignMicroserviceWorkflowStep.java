package com.example.pki.saga.updateuser;

import com.example.pki.model.dto.FollowerMicroserviceUpdateUserDto;
import com.example.pki.model.dto.saga.FollowerMicroserviceCreateUserResponse;
import com.example.pki.saga.WorkflowStep;
import com.example.pki.saga.WorkflowStepStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class UpdateUserInCampaignMicroserviceWorkflowStep implements WorkflowStep {
    private final WebClient webClient;
    private final FollowerMicroserviceUpdateUserDto oldUser;
    private final FollowerMicroserviceUpdateUserDto newUser;
    private final String token;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public UpdateUserInCampaignMicroserviceWorkflowStep(WebClient webClient, FollowerMicroserviceUpdateUserDto oldUser,
                                                        FollowerMicroserviceUpdateUserDto newUser, String token) {
        this.webClient = webClient;
        this.oldUser = oldUser;
        this.newUser = newUser;
        this.token = token;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public Mono<Boolean> process() {
        return webClient
                .put()
                .uri("/users/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(h -> h.setBearerAuth(token))
                .body(Mono.just(newUser), FollowerMicroserviceUpdateUserDto.class)
                .retrieve()
                .bodyToMono(FollowerMicroserviceCreateUserResponse.class)
                .map(FollowerMicroserviceCreateUserResponse::isSuccess)
                .doOnNext(b -> this.status = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED);
    }

    @Override
    public Mono<Boolean> revert() {
        return this.webClient
                .put()
                .uri("/users/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(h -> h.setBearerAuth(token))
                .body(Mono.just(oldUser), FollowerMicroserviceUpdateUserDto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .map(r -> true)
                .onErrorReturn(false);
    }
}
