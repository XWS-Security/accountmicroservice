package com.example.pki.model.dto;

public class DownloadCertificateDto {

    private String certificateName;

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
