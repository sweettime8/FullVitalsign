package com.elcom.fileservice.model.dto;

import java.io.Serializable;

/**
 *
 * @author anhdv
 */
public class DataBpRowsDTO implements Serializable {

    private String measureId;
    private Integer sys;
    private Integer dia;
    private Integer map;
    private Integer pr;
    private Long ts;
    
    public DataBpRowsDTO() {
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
     * @return the sys
     */
    public Integer getSys() {
        return sys;
    }

    /**
     * @param sys the sys to set
     */
    public void setSys(Integer sys) {
        this.sys = sys;
    }

    /**
     * @return the dia
     */
    public Integer getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(Integer dia) {
        this.dia = dia;
    }

    /**
     * @return the map
     */
    public Integer getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(Integer map) {
        this.map = map;
    }

    /**
     * @return the pr
     */
    public Integer getPr() {
        return pr;
    }

    /**
     * @param pr the pr to set
     */
    public void setPr(Integer pr) {
        this.pr = pr;
    }
}
