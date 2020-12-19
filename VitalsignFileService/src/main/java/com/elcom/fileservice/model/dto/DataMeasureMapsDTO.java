package com.elcom.fileservice.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Admin
 */

public class DataMeasureMapsDTO implements Serializable {

    private String gateId;
    private String patientId;
    private List<String> cols;
    private List<String> rows;

    public String getGateId() {
        return gateId;
    }

    public void setGateId(String gateId) {
        this.gateId = gateId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public List<String> getCols() {
        return cols;
    }

    public void setCols(List<String> cols) {
        this.cols = cols;
    }

    public List<String> getRows() {
        return rows;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
    }

}
