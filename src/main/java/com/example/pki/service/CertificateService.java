package com.example.pki.service;

import com.example.pki.model.dto.CertificateDto;

import java.util.List;

public interface CertificateService {
    void generate(CertificateDto dto);

    void revoke(String certificateAlias);

    boolean isCertificateValid(String certificateAlias);

    List<CertificateDto> getCertificates(boolean onlyCA);
}
