/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author admin
 */
public class ReportDataBpDTO implements Serializable {

    private String gateId;
    private String sensorId;
    private Integer sysValue;
    private Integer diaValue;
    private Integer mapValue;
    private String measureId;
    private String stepId;
    private Timestamp measureAt;
    //private String measureAtStr;

    public ReportDataBpDTO() {
    }

    /**
     * @return the sysValue
     */
    public Integer getSysValue() {
        return sysValue;
    }

    /**
     * @param sysValue the sysValue to set
     */
    public void setSysValue(Integer sysValue) {
        this.sysValue = sysValue;
    }

    /**
     * @return the diaValue
     */
    public Integer getDiaValue() {
        return diaValue;
    }

    /**
     * @param diaValue the diaValue to set
     */
    public void setDiaValue(Integer diaValue) {
        this.diaValue = diaValue;
    }

    /**
     * @return the mapValue
     */
    public Integer getMapValue() {
        return mapValue;
    }

    /**
     * @param mapValue the mapValue to set
     */
    public void setMapValue(Integer mapValue) {
        this.mapValue = mapValue;
    }

    /**
     * @return the measureId
     */
    public String getMeasureId() {
        return measureId;
    }

    /**
     * @param measureId the measureId to set
     */
    public void setMeasureId(String measureId) {
        this.measureId = measureId;
    }

    /**
     * @return the stepId
     */
    public String getStepId() {
        return stepId;
    }

    /**
     * @param stepId the stepId to set
     */
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    /**
     * @return the measureAt
     */
    public Timestamp getMeasureAt() {
        return measureAt;
    }

    /**
     * @param measureAt the measureAt to set
     */
    public void setMeasureAt(Timestamp measureAt) {
        this.measureAt = measureAt;
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

    /**
     * @return the sensorId
     */
    public String getSensorId() {
        return sensorId;
    }

    /**
     * @param sensorId the sensorId to set
     */
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * @return the measureAtStr
     */
    /*public String getMeasureAtStr() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.S").format(this.getMeasureAt());
    }*/
}
