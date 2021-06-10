package com.example.pki.model.dto;

import com.example.pki.model.enums.CA;
import com.example.pki.util.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

public class CertificateDto implements Serializable {
    private CA ca;
    private Date startDate;
    private Date endDate;
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String issuerUid;
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String subjectUid;
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    @NotNull
    private String certificateName;
    @Pattern(regexp = Constants.PLAIN_TEXT_PATTERN, message = Constants.INVALID_CHARACTER_MESSAGE)
    private String parentName;
    private boolean revoked;
    private boolean valid;
    @Valid
    private SubjectDataDto subjectData;

    public CertificateDto() {
    }

    public CertificateDto(CA ca, Date startDate, Date endDate, String certificateName,
                          String parentName, boolean revoked, boolean valid) {
        this.ca = ca;
        this.startDate = startDate;
        this.endDate = endDate;
        this.certificateName = certificateName;
        this.parentName = parentName;
        this.revoked = revoked;
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
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

    public SubjectDataDto getSubjectData() {
        return subjectData;
    }

    public void setSubjectData(SubjectDataDto subjectData) {
        this.subjectData = subjectData;
    }
}
