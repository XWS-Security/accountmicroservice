package com.example.pki.controller;

import com.example.pki.exceptions.*;
import com.example.pki.logging.LoggerService;
import com.example.pki.logging.LoggerServiceImpl;
import com.example.pki.model.User;
import com.example.pki.model.dto.CertificateDto;
import com.example.pki.model.dto.DownloadCertificateDto;
import com.example.pki.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/certificate", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {

    private final CertificateService certificateService;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/createCertificate")
    public ResponseEntity<String> generateCertificate(@RequestBody CertificateDto certificateDto) {
        try {
            certificateService.generate(certificateDto);
            loggerService.createCertificateSuccess(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
            return new ResponseEntity<>("Certificate successfully added!", HttpStatus.OK);

        } catch (CertificateAlreadyExists | CertificateIsNotValid | CertificateIsNotCA | CouldNotGenerateKeyPairException
                | CouldNotGenerateCertificateException | KeystoreErrorException e) {
            loggerService.createCertificateFailed(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getCA")
    public ResponseEntity<List<CertificateDto>> getCACertificates() {
        try {
            List<CertificateDto> certificates = certificateService.getCertificates(true);
            loggerService.getCACertificatesSuccess(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
            return new ResponseEntity<>(certificateService.getCertificates(true), HttpStatus.OK);

        } catch (KeystoreErrorException e) {
            loggerService.getCACertificatesFailed(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllCertificates")
    public ResponseEntity<List<CertificateDto>> getAllCertificates() {
        try {
            List<CertificateDto> certificates = certificateService.getCertificates(false);
            loggerService.getAllCertificatesSuccess(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
            return new ResponseEntity<>(certificateService.getCertificates(false), HttpStatus.OK);

        } catch (KeystoreErrorException e) {
            loggerService.getAllCertificatesFailed(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<String> revokeCertificate(@RequestBody CertificateDto certificateDto) {
        try {
            certificateService.revoke(certificateDto.getCertificateName());
            loggerService.revokeCertificateSuccess(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
            return new ResponseEntity<>("Certificate successfully revoked!", HttpStatus.OK);

        } catch (Exception e) {
            loggerService.revokeCertificateFailed(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail(), e.getMessage());
            return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/download")
    public ResponseEntity<String> downloadCertificate(@RequestBody DownloadCertificateDto certificateDto) {
        try {
            certificateService.downloadCertificate(certificateDto);
            loggerService.downloadCertificateSuccess(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
            return new ResponseEntity<>("Certificate successfully downloaded.", HttpStatus.OK);
        } catch (Exception e) {
            loggerService.downloadCertificateFailed(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername(), e.getMessage());
            return new ResponseEntity<>("Check password! Something went wrong!", HttpStatus.BAD_REQUEST);
        }
    }
}