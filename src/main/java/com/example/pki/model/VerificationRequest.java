package com.example.pki.model;

import com.example.pki.model.enums.VerificationCategory;

import javax.persistence.*;

@Entity
@DiscriminatorValue("VERIFICATION_REQUEST")
public class VerificationRequest {

    @Id
    @SequenceGenerator(name = "verification_status_sequence_generator", sequenceName = "verification_status_sequence", initialValue = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_status_sequence_generator")
    private Long id;

    @Column
    private String officialDocumentImageName;

    @Column
    private VerificationCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private NistagramUser user;

    @Column
    private boolean resolved;

    @Column
    private boolean approved;

    public VerificationRequest(String officialDocumentImageName, VerificationCategory category, NistagramUser user) {
        this.officialDocumentImageName = officialDocumentImageName;
        this.category = category;
        this.user = user;
        this.resolved = false;
        this.approved = false;
    }

    public VerificationRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOfficialDocumentImageName() {
        return officialDocumentImageName;
    }

    public void setOfficialDocumentImageName(String officialDocumentImageName) {
        this.officialDocumentImageName = officialDocumentImageName;
    }

    public VerificationCategory getStatus() {
        return category;
    }

    public void setStatus(VerificationCategory category) {
        this.category = category;
    }

    public NistagramUser getUser() {
        return user;
    }

    public void setUser(NistagramUser user) {
        this.user = user;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public VerificationCategory getCategory() {
        return category;
    }

    public void setCategory(VerificationCategory category) {
        this.category = category;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
