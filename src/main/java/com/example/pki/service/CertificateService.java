package com.example.pki.service;

import com.example.pki.model.dto.CertificateDto;
import com.example.pki.model.dto.DownloadCertificateDto;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.List;

public interface CertificateService {
    void generate(CertificateDto dto);

    void revoke(String certificateAlias);

    boolean isCertificateValid(String certificateAlias);

    List<CertificateDto> getCertificates(boolean onlyCA);

    HttpClient buildHttpClient() throws SSLException;

    void downloadCertificate(DownloadCertificateDto certificateDto) throws CertificateEncodingException, IOException;
}
