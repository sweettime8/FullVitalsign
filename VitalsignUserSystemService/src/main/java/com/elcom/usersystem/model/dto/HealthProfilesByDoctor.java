package com.elcom.usersystem.model.dto;

/**
 *
 * @author anhdv
 */
public class HealthProfilesByDoctor {
    
    private String healthProfileId;
    private String homeAdminId;
    private String fullName;
    private BpDataByHealthProfile bpDataLatest;
    private Spo2DataByHealthProfile spo2DataLatest;
    private TempDataByHealthProfile tempDataLatest;
    
    public HealthProfilesByDoctor() {
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
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the bpDataLatest
     */
    public BpDataByHealthProfile getBpDataLatest() {
        return bpDataLatest;
    }

    /**
     * @param bpDataLatest the bpDataLatest to set
     */
    public void setBpDataLatest(BpDataByHealthProfile bpDataLatest) {
        this.bpDataLatest = bpDataLatest;
    }

    /**
     * @return the spo2DataLatest
     */
    public Spo2DataByHealthProfile getSpo2DataLatest() {
        return spo2DataLatest;
    }

    /**
     * @param spo2DataLatest the spo2DataLatest to set
     */
    public void setSpo2DataLatest(Spo2DataByHealthProfile spo2DataLatest) {
        this.spo2DataLatest = spo2DataLatest;
    }

    /**
     * @return the tempDataLatest
     */
    public TempDataByHealthProfile getTempDataLatest() {
        return tempDataLatest;
    }

    /**
     * @param tempDataLatest the tempDataLatest to set
     */
    public void setTempDataLatest(TempDataByHealthProfile tempDataLatest) {
        this.tempDataLatest = tempDataLatest;
    }
}
