package com.elcom.vitalsign.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 *
 * @author anhdv
 */
public class DataTempRowsDTO implements Serializable {

    @JsonProperty("m")
    private String measureId;

    @JsonProperty("t")
    private Float temp;
    
    @JsonProperty("s")
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
