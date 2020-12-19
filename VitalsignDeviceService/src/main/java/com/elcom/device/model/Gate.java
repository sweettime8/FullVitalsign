package com.elcom.device.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 *
 * @author anhdv
 */
@Entity
@Table(name = "gate")
public class Gate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "business_unit_id")
    private String businessUnitId;

    @Column(name = "home_admin_id")
    private String homeAdminId;

    @Column(name = "health_profile_id")
    private String healthProfileId;

    @Column(name = "model")
    private String model;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "manufacture")
    private String manufacture;

    @Column(name = "firmware_version")
    private String firmwareVersion;

    @Column(name = "battery_value")
    private Integer batteryValue;

    @Column(name = "additional_info")
    private String additionalInfo;

    @Column(name = "own_type")
    private String ownType;
    
    @Column(name = "borrow_start_date")
    private Timestamp borrowStartDate;
    
    @Column(name = "borrow_end_date")
    private Timestamp borrowEndDate;
    
    @Column(name = "status")
    private Integer status;

    @Column(name = "activity_state")
    private Integer activityState;

    @Column(name = "activity_last_time")
    private Timestamp activityLastTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    @PrePersist
    void preInsert() {
        if (this.getId() == null)
            this.setId(UUID.randomUUID().toString());
        if (this.getIsDeleted() == null)
            this.setIsDeleted(0);
        if (this.getStatus() == null)
            this.setStatus(1);
        if (this.getOwnType()== null)
            this.setOwnType("SOLD");
        if (this.getActivityState()== null)
            this.setActivityState(0);
    }

    @PreUpdate
    void preUpdate() {
        if (this.getIsDeleted() == null)
            this.setIsDeleted(0);
        if (this.getStatus() == null)
            this.setStatus(1);
        if (this.getOwnType()== null)
            this.setOwnType("SOLD");
        if (this.getActivityState()== null)
            this.setActivityState(0);
    }

    public Gate() {
    }

    public Gate(String name, String businessUnitId, String homeAdminId, String healthProfileId, String model, String serialNumber, String manufacture, String firmwareVersion, Integer batteryValue, String additionalInfo, String ownType, Timestamp borrowStartDate, Timestamp borrowEndDate, Timestamp activityLastTime) {
        this.name = name;
        this.businessUnitId = businessUnitId;
        this.homeAdminId = homeAdminId;
        this.healthProfileId = healthProfileId;
        this.model = model;
        this.serialNumber = serialNumber;
        this.manufacture = manufacture;
        this.firmwareVersion = firmwareVersion;
        this.batteryValue = batteryValue;
        this.additionalInfo = additionalInfo;
        this.ownType = ownType;
        this.borrowStartDate = borrowStartDate;
        this.borrowEndDate = borrowEndDate;
        this.activityLastTime = activityLastTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public Integer getBatteryValue() {
        return batteryValue;
    }

    public void setBatteryValue(Integer batteryValue) {
        this.batteryValue = batteryValue;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getActivityState() {
        return activityState;
    }

    public void setActivityState(Integer activityState) {
        this.activityState = activityState;
    }

    public Timestamp getActivityLastTime() {
        return activityLastTime;
    }

    public void setActivityLastTime(Timestamp activityLastTime) {
        this.activityLastTime = activityLastTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    /**
     * @return the businessUnitId
     */
    public String getBusinessUnitId() {
        return businessUnitId;
    }

    /**
     * @param businessUnitId the businessUnitId to set
     */
    public void setBusinessUnitId(String businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    /**
     * @return the homeAdminId
     */
    public String getHomeAdminId() {
        return homeAdminId;
    }

    /**
     * @param homeAdminId the homeAdminId to set
     */
    public void setHomeAdminId(String homeAdminId) {
        this.homeAdminId = homeAdminId;
    }

    /**
     * @return the healthProfileId
     */
    public String getHealthProfileId() {
        return healthProfileId;
    }

    /**
     * @param healthProfileId the healthProfileId to set
     */
    public void setHealthProfileId(String healthProfileId) {
        this.healthProfileId = healthProfileId;
    }

    /**
     * @return the ownType
     */
    public String getOwnType() {
        return ownType;
    }

    /**
     * @param ownType the ownType to set
     */
    public void setOwnType(String ownType) {
        this.ownType = ownType;
    }

    /**
     * @return the borrowStartDate
     */
    public Timestamp getBorrowStartDate() {
        return borrowStartDate;
    }

    /**
     * @param borrowStartDate the borrowStartDate to set
     */
    public void setBorrowStartDate(Timestamp borrowStartDate) {
        this.borrowStartDate = borrowStartDate;
    }

    /**
     * @return the borrowEndDate
     */
    public Timestamp getBorrowEndDate() {
        return borrowEndDate;
    }

    /**
     * @param borrowEndDate the borrowEndDate to set
     */
    public void setBorrowEndDate(Timestamp borrowEndDate) {
        this.borrowEndDate = borrowEndDate;
    }
}
