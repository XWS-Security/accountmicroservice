package com.example.pki.saga.createuser;

import com.example.pki.exceptions.CreateUserWorkflowException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.dto.FollowerMicroserviceUserDto;
import com.example.pki.model.dto.saga.CreateUserOrchestratorResponse;
import com.example.pki.repository.UserRepository;
import com.example.pki.saga.Workflow;
import com.example.pki.saga.WorkflowStep;
import com.example.pki.saga.WorkflowStepStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class CreateUserOrchestrator {
    private final WebClient followerMicroserviceWebClient;
    private final WebClient contentMicroserviceWebClient;
    private final WebClient messagingMicroserviceWebClient;
    private final WebClient campaignMicroserviceWebClient;
    private final UserRepository userRepository;

    public CreateUserOrchestrator(WebClient followerMicroserviceWebClient, WebClient contentMicroserviceWebClient, WebClient messagingMicroserviceWebClient, WebClient campaignMicroserviceWebClient, UserRepository userRepository) {
        this.followerMicroserviceWebClient = followerMicroserviceWebClient;
        this.contentMicroserviceWebClient = contentMicroserviceWebClient;
        this.messagingMicroserviceWebClient = messagingMicroserviceWebClient;
        this.campaignMicroserviceWebClient = campaignMicroserviceWebClient;
        this.userRepository = userRepository;
    }

    public Mono<CreateUserOrchestratorResponse> createUser(NistagramUser user) {
        Workflow workflow = this.getCreateUserWorkflow(user);
        return Flux.fromStream(() -> workflow.getSteps().stream())
                .flatMap(WorkflowStep::process)
                .handle(((success, synchronousSink) -> {
                    if (success) synchronousSink.next(true);
                }))
                .then(Mono.fromCallable(() -> {
                    if (workflow.getSteps().stream().anyMatch(step -> step.getStatus() == WorkflowStepStatus.FAILED))
                        throw new CreateUserWorkflowException();
                    return getResponse(getUserDto(user), true, "");
                }))
                .onErrorResume(ex -> this.revertCreateUser(workflow, getUserDto(user)));
    }

    private Mono<CreateUserOrchestratorResponse> revertCreateUser(Workflow workflow, FollowerMicroserviceUserDto userDto) {
        return Flux.fromStream(() -> workflow.getSteps().stream())
                .filter(wf -> !wf.getStatus().equals(WorkflowStepStatus.FAILED))
                .flatMap(WorkflowStep::revert)
                .retry(3)
                .then(Mono.just(this.getResponse(userDto, false, "Registration error.")));
    }

    private Workflow getCreateUserWorkflow(NistagramUser user) {
        var userDto = getUserDto(user);
        var accountMicroserviceStep = new CreateUserInAccountMicroserviceWorkflowStep(user, userRepository);
        var followerMicroserviceStep = new CreateUserInFollowerMicroserviceWorkflowStep(followerMicroserviceWebClient, userDto);
        var contentMicroserviceStep = new CreateUserInContentMicroserviceWorkflowStep(contentMicroserviceWebClient, userDto);
        var messagingMicroserviceStep = new CreateUserInMessagingMicroserviceWorkflowStep(messagingMicroserviceWebClient, userDto);
        var campaignMicroserviceStep = new CreateUserInCampaignMicroserviceWorkflowStep(campaignMicroserviceWebClient, userDto);
        return new Workflow(List.of(accountMicroserviceStep, followerMicroserviceStep, contentMicroserviceStep, messagingMicroserviceStep, campaignMicroserviceStep));
    }

    private CreateUserOrchestratorResponse getResponse(FollowerMicroserviceUserDto userDto, boolean success, String message) {
        return new CreateUserOrchestratorResponse(userDto.getUsername(), success, message);
    }

    private FollowerMicroserviceUserDto getUserDto(NistagramUser user) {
        return new FollowerMicroserviceUserDto(user.getUsername(), user.isProfilePrivate(), user.getAbout());
    }
}
