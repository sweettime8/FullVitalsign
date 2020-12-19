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
import java.util.UUID;

/**
 *
 * @author admin
 */
@Entity
@Table(name = "data_spo2")
public class DataSpo2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
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

    @Column(name = "created_at")
    private Timestamp createdAt;

    @PrePersist
    void preInsert() {
        if (this.createdAt == null) {
            this.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
    }

    public DataSpo2() {
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
