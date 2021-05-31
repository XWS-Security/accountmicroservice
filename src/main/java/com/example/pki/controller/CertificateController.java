package com.example.pki.controller;

import com.example.pki.exceptions.*;
import com.example.pki.model.dto.CertificateDto;
import com.example.pki.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/certificate", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/createCertificate")
    public ResponseEntity<String> generateCertificate(@RequestBody CertificateDto certificateDto) {
        try {
            certificateService.generate(certificateDto);
            return new ResponseEntity<>("Certificate successfully added!", HttpStatus.OK);
        } catch (CertificateAlreadyExists | CertificateIsNotValid | CertificateIsNotCA | CouldNotGenerateKeyPairException
                | CouldNotGenerateCertificateException | KeystoreErrorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getCA")
    public ResponseEntity<List<CertificateDto>> getCACertificates() {
        try {
            return new ResponseEntity<>(certificateService.getCertificates(true), HttpStatus.OK);
        } catch (KeystoreErrorException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllCertificates")
    public ResponseEntity<List<CertificateDto>> getAllCertificates() {
        try {
            return new ResponseEntity<>(certificateService.getCertificates(false), HttpStatus.OK);
        } catch (KeystoreErrorException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<String> revokeCertificate(@RequestBody CertificateDto certificateDto) {
        try {
            certificateService.revoke(certificateDto.getCertificateName());
            return new ResponseEntity<>("Certificate successfully revoked!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }
    }
}