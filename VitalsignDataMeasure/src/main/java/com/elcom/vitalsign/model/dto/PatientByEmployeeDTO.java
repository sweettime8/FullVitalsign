package com.elcom.vitalsign.model.dto;

/**
 *
 * @author anhdv
 */
public class PatientByEmployeeDTO {
    
    private String patientId;
    private String patientName;
    private String patientCode;
    private String birthDate;
    private String gender;
    private String displayId;
    private String gateId;
    private String lstSensor;
    
    public PatientByEmployeeDTO() {
    }

    public PatientByEmployeeDTO(String patientId, String patientName, String patientCode, String birthDate
            , String gender, String displayId, String gateId, String lstSensor) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientCode = patientCode;
        this.birthDate = patientCode;
        this.gender = patientCode;
        this.displayId = displayId;
        this.gateId = gateId;
        this.lstSensor = lstSensor;
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
     * @return the patientName
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * @param patientName the patientName to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * @return the patientCode
     */
    public String getPatientCode() {
        return patientCode;
    }

    /**
     * @param patientCode the patientCode to set
     */
    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    /**
     * @return the displayId
     */
    public String getDisplayId() {
        return displayId;
    }

    /**
     * @param displayId the displayId to set
     */
    public void setDisplayId(String displayId) {
        this.displayId = displayId;
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
     * @return the birthDate
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return the lstSensor
     */
    public String getLstSensor() {
        return lstSensor;
    }

    /**
     * @param lstSensor the lstSensor to set
     */
    public void setLstSensor(String lstSensor) {
        this.lstSensor = lstSensor;
    }
}
