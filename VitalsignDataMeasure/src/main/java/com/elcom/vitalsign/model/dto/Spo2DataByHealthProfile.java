package com.elcom.vitalsign.model.dto;

import java.io.Serializable;

/**
 *
 * @author anhdv
 */
public class Spo2DataByHealthProfile implements Serializable {
    
    private String healthProfileId;
    private Long spo2;
    private Integer pi;
    private Double pr;
    private Long step;
    private Long ts;
    private Long lastUpdatedAt;
    
    public Spo2DataByHealthProfile() {
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
     * @return the ts
     */
    public Long getTs() {
        return ts;
    }

    /**
     * @param ts the ts to set
     */
    public void setTs(Long ts) {
        this.ts = ts;
    }

    /**
     * @return the lastUpdatedAt
     */
    public Long getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    /**
     * @param lastUpdatedAt the lastUpdatedAt to set
     */
    public void setLastUpdatedAt(Long lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    /**
     * @return the spo2
     */
    public Long getSpo2() {
        return spo2;
    }

    /**
     * @param spo2 the spo2 to set
     */
    public void setSpo2(Long spo2) {
        this.spo2 = spo2;
    }

    /**
     * @return the pi
     */
    public Integer getPi() {
        return pi;
    }

    /**
     * @param pi the pi to set
     */
    public void setPi(Integer pi) {
        this.pi = pi;
    }

    /**
     * @return the pr
     */
    public Double getPr() {
        return pr;
    }

    /**
     * @param pr the pr to set
     */
    public void setPr(Double pr) {
        this.pr = pr;
    }

    /**
     * @return the step
     */
    public Long getStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(Long step) {
        this.step = step;
    }
}
