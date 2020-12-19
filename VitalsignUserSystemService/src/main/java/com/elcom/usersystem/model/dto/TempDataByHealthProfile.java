package com.elcom.usersystem.model.dto;

import java.io.Serializable;

/**
 *
 * @author anhdv
 */
public class TempDataByHealthProfile implements Serializable {
    
    private String healthProfileId;
    private Float temp;
    private Long ts;
    private Long lastUpdatedAt;
    
    public TempDataByHealthProfile() {
    }

    public TempDataByHealthProfile(Float temp, Long ts, Long lastUpdatedAt) {
        this.temp = temp;
        this.ts = ts;
        this.lastUpdatedAt = lastUpdatedAt;
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
     * @return the temp
     */
    public Float getTemp() {
        return temp;
    }

    /**
     * @param temp the temp to set
     */
    public void setTemp(Float temp) {
        this.temp = temp;
    }
}
