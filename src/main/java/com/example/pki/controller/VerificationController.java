package com.example.pki.controller;

import com.example.pki.model.dto.RegisterAgentDTO;
import com.example.pki.model.dto.VerificationRequestDto;
import com.example.pki.model.enums.VerificationStatus;
import com.example.pki.service.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/verification", produces = MediaType.APPLICATION_JSON_VALUE)

public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/status/{username}")
    public ResponseEntity<VerificationStatus> verificationStatus(@PathVariable String username) {
        var status = verificationService.getVerificationStatus(username);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/")
    public ResponseEntity<String> requestVerification(@RequestBody VerificationRequestDto dto) {
        System.out.println(dto);
        verificationService.requestVerification(dto);
        return ResponseEntity.ok("Validations successful");
    }

    @GetMapping("/")
    public ResponseEntity<List<Long>> requestIds() {
        var requests = verificationService.getUnresolvedVerificationRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VerificationRequestDto> getRequest(@PathVariable Long id) {
        var request = verificationService.getVerification(id);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        verificationService.approve(id);
        return ResponseEntity.ok("Validation approved");
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        verificationService.reject(id);
        return ResponseEntity.ok("Validation rejected");
    }

    @GetMapping("/agentsAll")
    public ResponseEntity<List<RegisterAgentDTO>> getAgents() {
        return new ResponseEntity<>(verificationService.getAgents(), HttpStatus.OK);
    }

    @PutMapping("/approveAgent/{username}")
    public ResponseEntity<String> verifyAgent(@PathVariable String username) {
        verificationService.approveAgent(username);
        return ResponseEntity.ok("Validation approved");
    }
}
