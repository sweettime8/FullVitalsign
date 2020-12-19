/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "sensor")
//@Proxy(lazy = false)
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@org.springframework.cache.annotation.Cacheable
public class Sensor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "gate_id")
    private String gateId;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "name")
    private String name;

    @Column(name = "model")
    private String model;

    @Column(name = "sensor_type")
    private String sensorType;

    @Column(name = "mac")
    private String mac;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "manufacture")
    private String manufacture;

    @Column(name = "firmware_version")
    private String firmwareVersion;

    @Column(name = "battery_value")
    private Integer batteryValue;

    @Column(name = "status")
    private Integer status;

    @Column(name = "measure_state")
    private Integer measureState;

    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    @PrePersist
    void preInsert() {
        if (this.isDeleted == null) {
            this.setIsDeleted(0);
        }
        if (this.status == null) {
            this.setStatus(1);
        }
        if (this.measureState == null) {
            this.setMeasureState(0);
        }
    }

    @PreUpdate
    void preUpdate() {
        if (this.isDeleted == null) {
            this.setIsDeleted(0);
        }
        if (this.status == null) {
            this.setStatus(1);
        }
        if (this.measureState == null) {
            this.setMeasureState(0);
        }
    }

    public Sensor(String id, String name, String gateId, String patientId,
            String model, String sensorType, String mac, String serialNumber,
            String manufacture, String firmwareVersion, Integer batteryValue) {
        this.id = id;
        this.name = name;
        this.gateId = gateId;
        this.patientId = patientId;
        this.model = model;
        this.sensorType = sensorType;
        this.mac = mac;
        this.serialNumber = serialNumber;
        this.manufacture = manufacture;
        this.firmwareVersion = firmwareVersion;
        this.batteryValue = batteryValue;
    }

    public Sensor() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @param serialNumber the serialNumber to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * @return the manufacture
     */
    public String getManufacture() {
        return manufacture;
    }

    /**
     * @param manufacture the manufacture to set
     */
    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    /**
     * @return the firmwareVersion
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * @param firmwareVersion the firmwareVersion to set
     */
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    /**
     * @return the batteryValue
     */
    public Integer getBatteryValue() {
        return batteryValue;
    }

    /**
     * @param batteryValue the batteryValue to set
     */
    public void setBatteryValue(Integer batteryValue) {
        this.batteryValue = batteryValue;
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

    /**
     * @return the isDeleted
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted the isDeleted to set
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
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
     * @return the gateId
     */
    public String getGateId() {
        return gateId;
    }

    /**
     * @param gateId the gateId to set
     */
    public void setGateId(String gateId) {
        this.gateId = gateId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the sensorType
     */
    public String getSensorType() {
        return sensorType;
    }

    /**
     * @param sensorType the sensorType to set
     */
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    /**
     * @return the mac
     */
    public String getMac() {
        return mac;
    }

    /**
     * @param mac the mac to set
     */
    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * @return the measureState
     */
    public Integer getMeasureState() {
        return measureState;
    }

    /**
     * @param measureState the measureState to set
     */
    public void setMeasureState(Integer measureState) {
        this.measureState = measureState;
    }

    @Override
    public String toString() {
        return "Sensor{"+ "sensorType:" + sensorType + ",patientId:" + patientId + ", mac=:" + mac + ", status:" + status  + '}';
    }

    
}
