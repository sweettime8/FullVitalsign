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
@Table(name = "data_spo2_latest")
public class DataSpo2Latest implements Serializable {

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

    @Column(name = "spo2")
    private Integer spo2;

    @Column(name = "pi")
    private Integer pi;

    @Column(name = "pr")
    private Double pr;

    @Column(name = "step")
    private int step;

    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    public DataSpo2Latest() {
    }

    public DataSpo2Latest(String gateId, String userProfileId, String measureId, Integer ts,
            Integer spo2, Integer pi, Double pr, int step) {
        this.gateId = gateId;
        this.userProfileId = userProfileId;
        this.measureId = measureId;
        this.ts = ts;
        this.spo2 = spo2;
        this.pi = pi;
        this.pr = pr;
        this.step = step;
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

    public String getGateId() {
        return gateId;
    }

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

    public Integer getSpo2() {
        return spo2;
    }

    public void setSpo2(Integer spo2) {
        this.spo2 = spo2;
    }

    public Integer getPi() {
        return pi;
    }

    public void setPi(Integer pi) {
        this.pi = pi;
    }

    public Double getPr() {
        return pr;
    }

    public void setPr(Double pr) {
        this.pr = pr;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

}
