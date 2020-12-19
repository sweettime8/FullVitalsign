package com.elcom.device.constant;

/**
 *
 * @author anhdv
 */
public class Constant {
    
    public static final String SRV_VER = "/v1.0";
    
    /** Validation message */
    public static final String VALIDATION_INVALID_PARAM_VALUE = "Invalid param value";
    public static final String VALIDATION_DATA_NOT_FOUND = "Data not found";
    
    public static final String API_DEVICE_CHECK_VALID_DEVICE_ID = "/device/validate-device-id";
    public static final String API_DEVICE_FIND_RESOURCE_INFO    = "/device/resource-info";
    public static final String API_DEVICE_DECRYPT_DEVICE_TOKEN  = "/device/decrypt-device-token";
    public static final String API_DEVICE_UPDATE_PATIENT_PROFILE_ID_FOR_GATE = "/device/gate/update-patient-profile-id";
}
