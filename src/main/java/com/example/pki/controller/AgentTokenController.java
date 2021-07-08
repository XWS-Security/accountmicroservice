package com.example.pki.controller;

import com.example.pki.service.AgentTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
public class AgentTokenController {
    private final AgentTokenService agentTokenService;

    public AgentTokenController(AgentTokenService agentTokenService) {
        this.agentTokenService = agentTokenService;
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('CAMPAIGN_PRIVILEGE')")
    ResponseEntity<String> getToken() {
        try {
            var result = agentTokenService.get();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
