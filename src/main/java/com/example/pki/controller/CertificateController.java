package com.example.pki.controller;

import com.example.pki.exceptions.CertificateAlreadyExists;
import com.example.pki.exceptions.CertificateIsNotCA;
import com.example.pki.exceptions.CertificateIsNotValid;
import com.example.pki.exceptions.CouldNotGenerateCertificateException;
import com.example.pki.exceptions.CouldNotGenerateKeyPairException;
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
        } catch (CertificateAlreadyExists | CertificateIsNotValid | CertificateIsNotCA | CouldNotGenerateKeyPairException | CouldNotGenerateCertificateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getCA")
    public ResponseEntity<List<CertificateDto>> getCACertificates() {
        return new ResponseEntity<>(certificateService.getCertificates(true), HttpStatus.OK);
    }

    @GetMapping("/getAllCertificates")
    public ResponseEntity<List<CertificateDto>> getAllCertificates() {
        return new ResponseEntity<>(certificateService.getCertificates(false), HttpStatus.OK);
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