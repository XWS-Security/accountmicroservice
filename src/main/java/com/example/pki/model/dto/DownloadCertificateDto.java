package com.example.pki.model.dto;

import com.example.pki.util.Constants;

import javax.validation.constraints.Pattern;

public class DownloadCertificateDto {
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String certificateName;
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String keystorePass;

    public DownloadCertificateDto() {

    }

    public DownloadCertificateDto(String certificateName, String keystorePass) {
        this.certificateName = certificateName;
        this.keystorePass = keystorePass;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }
}
