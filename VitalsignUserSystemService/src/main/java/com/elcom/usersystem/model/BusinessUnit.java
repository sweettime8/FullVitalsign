package com.elcom.usersystem.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 *
 * @author anhdv
 */
@Entity
@Table(name = "business_unit")
public class BusinessUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "license_no")
    private String licenseNo;

    @Column(name = "license_issued_by")
    private String licenseIssuedBy;

    @Column(name = "email")
    private String email;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "created_at")
    private Timestamp createdAt;
    
    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    @PrePersist
    void preInsert() {
        if (this.getId() == null)
            this.setId(UUID.randomUUID().toString());
        if( this.getStatus()==null )
            this.setStatus(1);
    }

    @PreUpdate
    void preUpdate() {
    }

    public BusinessUnit() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the accountId
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * @return the licenseNo
     */
    public String getLicenseNo() {
        return licenseNo;
    }

    /**
     * @param licenseNo the licenseNo to set
     */
    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    /**
     * @return the licenseIssuedBy
     */
    public String getLicenseIssuedBy() {
        return licenseIssuedBy;
    }

    /**
     * @param licenseIssuedBy the licenseIssuedBy to set
     */
    public void setLicenseIssuedBy(String licenseIssuedBy) {
        this.licenseIssuedBy = licenseIssuedBy;
    }

    /**
     * @return the createdAt
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the lastUpdatedAt
     */
    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    /**
     * @param lastUpdatedAt the lastUpdatedAt to set
     */
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}
