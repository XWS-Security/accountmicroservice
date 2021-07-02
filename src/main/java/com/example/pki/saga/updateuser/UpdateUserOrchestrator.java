package com.example.pki.saga.updateuser;

import com.example.pki.exceptions.CreateUserWorkflowException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.dto.FollowerMicroserviceUpdateUserDto;
import com.example.pki.model.dto.saga.CreateUserOrchestratorResponse;
import com.example.pki.repository.UserRepository;
import com.example.pki.saga.Workflow;
import com.example.pki.saga.WorkflowStep;
import com.example.pki.saga.WorkflowStepStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class UpdateUserOrchestrator {
    private final WebClient followerMicroserviceWebClient;
    private final WebClient contentMicroserviceWebClient;
    private final UserRepository userRepository;
    private final String token;

    public UpdateUserOrchestrator(WebClient followerMicroserviceWebClient, WebClient contentMicroserviceWebClient, UserRepository userRepository, String token) {
        this.followerMicroserviceWebClient = followerMicroserviceWebClient;
        this.contentMicroserviceWebClient = contentMicroserviceWebClient;
        this.userRepository = userRepository;
        this.token = token;
    }

    public Mono<CreateUserOrchestratorResponse> createUser(NistagramUser oldUser, NistagramUser newUser) {
        Workflow workflow = this.getCreateUserWorkflow(oldUser, newUser);
        return Flux.fromStream(() -> workflow.getSteps().stream())
                .flatMap(WorkflowStep::process)
                .handle(((success, synchronousSink) -> {
                    if (success) synchronousSink.next(true);
                }))
                .then(Mono.fromCallable(() -> {
                    if (workflow.getSteps().stream().anyMatch(step -> step.getStatus() == WorkflowStepStatus.FAILED))
                        throw new CreateUserWorkflowException();
                    return getResponse(newUser.getNistagramUsername(), true, "");
                }))
                .onErrorResume(ex -> this.revertCreateUser(workflow, oldUser.getNistagramUsername()));
    }

    private Mono<CreateUserOrchestratorResponse> revertCreateUser(Workflow workflow, String username) {
        return Flux.fromStream(() -> workflow.getSteps().stream())
                .filter(wf -> !wf.getStatus().equals(WorkflowStepStatus.FAILED))
                .flatMap(WorkflowStep::revert)
                .retry(3)
                .then(Mono.just(this.getResponse(username, false, "Update user error.")));
    }

    private Workflow getCreateUserWorkflow(NistagramUser oldUser, NistagramUser newUser) {
        var oldUserDto = getUserDto(oldUser, newUser);
        var newUserDto = getUserDto(newUser, oldUser);
        var accountMicroserviceStep = new UpdateUserInAccountMicroserviceWorkflowStep(oldUser, newUser, userRepository);
        var followerMicroserviceStep = new UpdateUserInFollowerMicroserviceWorkflowStep(followerMicroserviceWebClient, oldUserDto, newUserDto, token);
        var contentMicroserviceStep = new UpdateUserInContentMicroserviceWorkflowStep(contentMicroserviceWebClient, oldUserDto, newUserDto, token);
        // TODO: add other microservices (messaging)
        return new Workflow(List.of(accountMicroserviceStep, followerMicroserviceStep, contentMicroserviceStep));
    }

    private CreateUserOrchestratorResponse getResponse(String username, boolean success, String message) {
        return new CreateUserOrchestratorResponse(username, success, message);
    }

    private FollowerMicroserviceUpdateUserDto getUserDto(NistagramUser user, NistagramUser otherUser) {
        return new FollowerMicroserviceUpdateUserDto(user.getUsername(), otherUser.getUsername(), user.getAbout(), user.isProfilePrivate(), user.isTagsEnabled());
    }
}
