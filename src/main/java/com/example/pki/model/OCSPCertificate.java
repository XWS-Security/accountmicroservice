package com.example.pki.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ocsp_cert")
public class OCSPCertificate {
    @Id
    @SequenceGenerator(name = "ocsp_cert_sequence_generator", sequenceName = "ocsp_cert_sequence", initialValue = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ocsp_cert_sequence_generator")
    private Long id;

    @Column
    private String fileName;

    @Column
    private boolean revoked;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "issuer_id", referencedColumnName = "id")
    private OCSPCertificate issuer;

    public OCSPCertificate() {
    }

    public OCSPCertificate(String fileName, OCSPCertificate issuer) {
        this.fileName = fileName;
        this.issuer = issuer;
        this.revoked = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public OCSPCertificate getIssuer() {
        return issuer;
    }

    public void setIssuer(OCSPCertificate issuer) {
        this.issuer = issuer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OCSPCertificate)) return false;
        OCSPCertificate that = (OCSPCertificate) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(issuer, that.issuer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, issuer);
    }

    public void revoke() {
        this.revoked = true;
    }
}
