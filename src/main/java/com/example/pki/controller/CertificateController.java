package com.example.pki.controller;

import com.example.pki.model.dto.CertificateDto;
import com.example.pki.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/certificate", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/")
    public ResponseEntity<String> generateCertificate(@RequestBody CertificateDto promotionDto) {
        certificateService.generateCertificate(promotionDto);
        return new ResponseEntity<>("Promotion successfully added!", HttpStatus.OK);
    }
}
