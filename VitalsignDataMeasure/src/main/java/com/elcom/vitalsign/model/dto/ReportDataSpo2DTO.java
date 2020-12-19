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
public class ReportDataSpo2DTO implements Serializable {

    private String gateId;
    private String sensorId;
    private Integer spo2Value;
    private Integer piValue;
    private Integer ppgValue;
    private Integer pviValue;
    private String measureId;
    private String stepId;
    private Timestamp measureAt;

    public ReportDataSpo2DTO() {
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
     * @return the spo2Value
     */
    public Integer getSpo2Value() {
        return spo2Value;
    }

    /**
     * @param spo2Value the spo2Value to set
     */
    public void setSpo2Value(Integer spo2Value) {
        this.spo2Value = spo2Value;
    }

    /**
     * @return the piValue
     */
    public Integer getPiValue() {
        return piValue;
    }

    /**
     * @param piValue the piValue to set
     */
    public void setPiValue(Integer piValue) {
        this.piValue = piValue;
    }

    /**
     * @return the ppgValue
     */
    public Integer getPpgValue() {
        return ppgValue;
    }

    /**
     * @param ppgValue the ppgValue to set
     */
    public void setPpgValue(Integer ppgValue) {
        this.ppgValue = ppgValue;
    }

    /**
     * @return the pviValue
     */
    public Integer getPviValue() {
        return pviValue;
    }

    /**
     * @param pviValue the pviValue to set
     */
    public void setPviValue(Integer pviValue) {
        this.pviValue = pviValue;
    }
}
