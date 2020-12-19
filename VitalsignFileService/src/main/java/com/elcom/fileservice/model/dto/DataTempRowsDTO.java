package com.elcom.fileservice.model.dto;

import java.io.Serializable;

/**
 *
 * @author anhdv
 */
public class DataTempRowsDTO implements Serializable {

    private String measureId;
    private Float temp;
    private Long ts;
    
    public DataTempRowsDTO() {
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
