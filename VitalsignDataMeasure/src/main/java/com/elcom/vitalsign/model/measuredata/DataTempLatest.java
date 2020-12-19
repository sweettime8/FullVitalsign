/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.model.measuredata;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author admin
 */
@Entity
@Table(name = "data_temp_latest")
public class DataTempLatest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigDecimal id;

    @Column(name = "gate_id")
    private String gateId;

    @Column(name = "user_profile_id", length = 36, updatable = false, nullable = false)
    private String userProfileId;

    @Column(name = "measure_id")
    private String measureId;

    @Column(name = "ts")
    private Integer ts;

    @Column(name = "temp")
    private Float temp;

    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    public DataTempLatest() {
    }

    public DataTempLatest(String gateId, String userProfileId, String measureId, Integer ts, Float temp) {
        this.gateId = gateId;
        this.userProfileId = userProfileId;
        this.measureId = measureId;
        this.ts = ts;
        this.temp = temp;
    }

    @PrePersist
    void preInsert() {
        if (this.lastUpdatedAt == null) {
            this.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
        }
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
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

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getMeasureId() {
        return measureId;
    }

    public void setMeasureId(String measureId) {
        this.measureId = measureId;
    }

    public Integer getTs() {
        return ts;
    }

    public void setTs(Integer ts) {
        this.ts = ts;
    }

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }

    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

}
