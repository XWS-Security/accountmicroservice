package com.example.pki.service;

import com.example.pki.model.OCSPCertificate;
import com.example.pki.model.dto.CertificateDto;

import java.util.List;

public interface CertificateService {
    void generateCertificate(CertificateDto dto);

    void changeCertificateStatus(String certificateAlias);

    boolean isAnyInChainRevoked(String certificateAlias);

    boolean isAnyInChainOutdated(String certificateAlias);

    boolean isCertificateValid(String certificateAlias);

    Iterable<OCSPCertificate> getAllCertificates();

    List<CertificateDto> getCACertificates();
}
