package com.example.pki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;

public class FollowerMicroserviceController {

    @Value("${FOLLOWER}")
    private String followerMicroserviceURI;

    @GetMapping("/hitMicroservice") // Purpose of this method is to show communication between microservices
    public Flux<String> hitFollowerMicroservice() throws IOException {

        // Creating web client.
        WebClient client = WebClient.builder()
                .baseUrl(followerMicroserviceURI)
                .build();

        // Define a method.
        return client.get()
                .uri("/users/hit")
                .retrieve()
                .bodyToFlux(String.class);
    }
}
