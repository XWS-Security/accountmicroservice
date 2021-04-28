package com.example.pki.controller;

import com.example.pki.exceptions.CertificateAlreadyExists;
import com.example.pki.exceptions.CertificateIsNotCA;
import com.example.pki.exceptions.CertificateIsNotValid;
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
            certificateService.generateCertificate(certificateDto);
            return new ResponseEntity<>("Certificate successfully added!", HttpStatus.OK);
        } catch (CertificateAlreadyExists e) {
            return new ResponseEntity<>("Certificate with that name already exists!", HttpStatus.BAD_REQUEST);
        } catch (CertificateIsNotValid e) {
            return new ResponseEntity<>("Certificate is not valid!", HttpStatus.BAD_REQUEST);
        } catch (CertificateIsNotCA e) {
            return new ResponseEntity<>("Certificate is not CA!", HttpStatus.BAD_REQUEST);
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
            certificateService.changeCertificateStatus(certificateDto.getCertificateName());
            return new ResponseEntity<>("Certificate successfully revoked!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }
    }
}