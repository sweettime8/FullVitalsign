package com.elcom.fileservice.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author anhdv
 */
public class DataBpDTO implements Serializable {
    
    private String gateId;
    private String patientId;
    private List<DataBpRowsDTO> data;
    
    public DataBpDTO() {
    }
    
    public DataBpDTO(List<DataBpRowsDTO> data) {
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
    public List<DataBpRowsDTO> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<DataBpRowsDTO> data) {
        this.data = data;
    }
}
