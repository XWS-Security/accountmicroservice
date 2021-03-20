package com.example.pki.model.dto;

import com.example.pki.model.enums.CA;

import java.util.Date;

public class CertificateDto {

    private CA ca;
    private Date startDate;
    private Date endDate;
    private String issuerUid;
    private String subjectUid;

    public CertificateDto() {
    }

    public CertificateDto(CA ca, Date startDate, Date endDate, String issuerUid, String subjectUid) {
        this.ca = ca;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issuerUid = issuerUid;
        this.subjectUid = subjectUid;
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
}
