package com.elcom.usersystem.constant;

/**
 *
 * @author anhdv
 */
public class Constant {
    
    public static final String SRV_VER = "/v1.0";
    
    /** Validation message */
    public static final String VALIDATION_INVALID_PARAM_VALUE = "Invalid param value";
    public static final String VALIDATION_DATA_NOT_FOUND = "Data not found";
    
    /* MQTT */
    public static final String MQTT_BROKER_PROTOCOL = "ssl";
//    public static final String MQTT_BROKER_HOST     = "103.21.151.182";
//    public static final String MQTT_BROKER_HOST     = "192.168.9.126";
      public static final String MQTT_BROKER_HOST     = "103.21.151.131";
//    public static final String MQTT_BROKER_HOST     = "localhost";
    public static final int    MQTT_BROKER_PORT     = 8883;
    public static final String MQTT_USR             = "broker-01";
    //public static final String MQTT_PW              = "Elcom@123";
    public static final String MQTT_PW              = "";
    
    public static final int    MQTT_QOS             = 2;
    public static final String MQTT_GROUP_TOPIC     = "g0002";
    public static final String MQTT_TOPIC           = "t0002";
    
    public static final String API_DEVICE_DECRYPT_DEVICE_TOKEN  = "/device/decrypt-device-token";
    
    public static final String API_MEASURE_DATA_FIND_BP_DATA_LATEST   = "/measure-data/bp/data-latest-by-health-profile-id-list";
    public static final String API_MEASURE_DATA_FIND_SPO2_DATA_LATEST = "/measure-data/spo2/data-latest-by-health-profile-id-list";
    public static final String API_MEASURE_DATA_FIND_TEMP_DATA_LATEST = "/measure-data/temp/data-latest-by-health-profile-id-list";
}
