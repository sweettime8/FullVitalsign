package com.elcom.usersystem.model.dto;

import java.io.Serializable;

/**
 *
 * @author anhdv
 */
public class BpDataByHealthProfile implements Serializable {
    
    private String healthProfileId;
    private Long sys;
    private Long dia;
    private Long map;
    private Long pr;
    private Long ts;
    private Long lastUpdatedAt;
    
    public BpDataByHealthProfile() {
    }

    public BpDataByHealthProfile(Long sys, Long dia, Long map, Long pr, Long ts, Long lastUpdatedAt) {
        this.sys = sys;
        this.dia = dia;
        this.map = map;
        this.pr = pr;
        this.ts = ts;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    /**
     * @return the sys
     */
    public Long getSys() {
        return sys;
    }

    /**
     * @param sys the sys to set
     */
    public void setSys(Long sys) {
        this.sys = sys;
    }

    /**
     * @return the dia
     */
    public Long getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(Long dia) {
        this.dia = dia;
    }

    /**
     * @return the map
     */
    public Long getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(Long map) {
        this.map = map;
    }

    /**
     * @return the pr
     */
    public Long getPr() {
        return pr;
    }

    /**
     * @param pr the pr to set
     */
    public void setPr(Long pr) {
        this.pr = pr;
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
}
