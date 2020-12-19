package com.elcom.fileservice.model.dto;

import java.io.Serializable;

/**
 *
 * @author anhdv
 */
public class DataSpo2RowsDTO implements Serializable {

    private String measureId;
    private Integer spo2;
    private Integer pi;
    private Double pr;
    private Integer step;
    private Long ts;
    
    public DataSpo2RowsDTO() {
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
     * @return the spo2
     */
    public Integer getSpo2() {
        return spo2;
    }

    /**
     * @param spo2 the spo2 to set
     */
    public void setSpo2(Integer spo2) {
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
    public Integer getStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(Integer step) {
        this.step = step;
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
}
