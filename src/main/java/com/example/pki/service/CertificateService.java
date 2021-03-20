package com.example.pki.service;

import com.example.pki.model.dto.CertificateDto;

public interface CertificateService {
    void generateCertificate(CertificateDto dto);
}
