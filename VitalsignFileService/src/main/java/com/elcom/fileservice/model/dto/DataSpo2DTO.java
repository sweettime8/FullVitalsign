package com.elcom.fileservice.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author anhdv
 */
public class DataSpo2DTO implements Serializable {
    
    private String gateId;
    private String patientId;
    private List<DataSpo2RowsDTO> data;
    
    public DataSpo2DTO() {
    }
    
    public DataSpo2DTO(List<DataSpo2RowsDTO> data) {
        this.data = data;
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
     * @return the patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the data
     */
    public List<DataSpo2RowsDTO> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<DataSpo2RowsDTO> data) {
        this.data = data;
    }
}
