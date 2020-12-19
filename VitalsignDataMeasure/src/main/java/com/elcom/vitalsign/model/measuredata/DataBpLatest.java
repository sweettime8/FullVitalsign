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
@Table(name = "data_bp_latest")
public class DataBpLatest implements Serializable {

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

    @Column(name = "dia")
    private Long dia;

    @Column(name = "sys")
    private Long sys;

    @Column(name = "map")
    private Long map;

    @Column(name = "pr")
    private Integer pr;

    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    public DataBpLatest() {
    }

    public DataBpLatest(String gateId, String userProfileId, String measureId, Integer ts,
            Long dia, Long sys, Long map, Integer pr) {
        this.gateId = gateId;
        this.userProfileId = userProfileId;
        this.measureId = measureId;
        this.ts = ts;
        this.dia = dia;
        this.sys = sys;
        this.map = map;
        this.pr = pr;
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

    public Long getDia() {
        return dia;
    }

    public void setDia(Long dia) {
        this.dia = dia;
    }

    public Long getSys() {
        return sys;
    }

    public void setSys(Long sys) {
        this.sys = sys;
    }

    public Long getMap() {
        return map;
    }

    public void setMap(Long map) {
        this.map = map;
    }

    public Integer getPr() {
        return pr;
    }

    public void setPr(Integer pr) {
        this.pr = pr;
    }

    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

}
