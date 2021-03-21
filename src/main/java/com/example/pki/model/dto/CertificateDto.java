package com.example.pki.model.dto;

import com.example.pki.model.enums.CA;

import java.io.Serializable;
import java.util.Date;

public class CertificateDto implements Serializable {

    private CA ca;
    private Date startDate;
    private Date endDate;
    private String issuerUid;
    private String subjectUid;
    private String certificateName;
    private String parentName;
    private boolean revoked;

    public CertificateDto() {
    }

    public CertificateDto(CA ca, Date startDate, Date endDate, String certificateName,
    String parentName, boolean revoked) {
        this.ca = ca;
        this.startDate = startDate;
        this.endDate = endDate;
        this.certificateName = certificateName;
        this.parentName = parentName;
        this.revoked = revoked;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public CA getCa() {
        return ca;
    }

    public void setCa(CA ca) {
        this.ca = ca;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getIssuerUid() {
        return issuerUid;
    }

    public void setIssuerUid(String issuerUid) {
        this.issuerUid = issuerUid;
    }

    public String getSubjectUid() {
        return subjectUid;
    }

    public void setSubjectUid(String subjectUid) {
        this.subjectUid = subjectUid;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
