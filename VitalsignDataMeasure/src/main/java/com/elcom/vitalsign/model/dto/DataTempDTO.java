package com.elcom.vitalsign.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author anhdv
 */
public class DataTempDTO implements Serializable {
    
    private String gateId;

    private String patientId;

    private List<DataTempRowsDTO> data;
    
    private String validationMsg;
    
    public DataTempDTO() {
    }
    
    public DataTempDTO(String validationMsg) {
        this.validationMsg = validationMsg;
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
    public List<DataTempRowsDTO> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<DataTempRowsDTO> data) {
        this.data = data;
    }

    /**
     * @return the validationMsg
     */
    public String getValidationMsg() {
        return validationMsg;
    }

    /**
     * @param validationMsg the validationMsg to set
     */
    public void setValidationMsg(String validationMsg) {
        this.validationMsg = validationMsg;
    }
}
