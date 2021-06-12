package com.example.pki.controller;

import com.example.pki.keystore.Keystore;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;

@RestController
@RequestMapping(value = "/followerMicroservice", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class FollowerMicroserviceController {

    @Value("${KEYSTORE_CERT}")
    public String KEYSTORE_PATH;

    @Value("${FOLLOWER}")
    private String followerMicroserviceURI;
    private final Keystore keystore = new Keystore("/data/certificates/");

    @GetMapping("/hitMicroservice") // Purpose of this method is to show communication between microservices
    public Flux<String> hitFollowerMicroservice() throws IOException {
        // SSL
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(keystore.readCertificateFromPfx("root"))
                .build();
        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

        // Creating web client.
        WebClient client = WebClient.builder()
                .baseUrl(followerMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        // Define a method.
        return client.get()
                .uri("/users/hit")
                .retrieve()
                .bodyToFlux(String.class);
    }
}