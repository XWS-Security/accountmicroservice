package com.example.pki.controller;

import com.example.pki.keystore.Keystore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/contentMicroservice", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentMicroserviceController {

    @Value("${CONTENT}")
    private String contentMicroserviceURI;
    private final Keystore keystore = new Keystore();

}
