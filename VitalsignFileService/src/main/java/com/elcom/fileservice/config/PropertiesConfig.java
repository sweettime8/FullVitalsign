package com.elcom.fileservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author anhdv
 */
@Component
public class PropertiesConfig {
    
    public static boolean APP_MASTER;
    public static int     HIBERNATE_BATCH_SIZE;
    public static String  FILE_UPLOAD_BASE_DIR;
    
    public static String  MQTT_SSL_CA_CERT;
    public static String  MQTT_SSL_CLIENT_CERT;
    public static String  MQTT_SSL_CLIENT_KEY;
    
    @Autowired
    public PropertiesConfig(@Value("${app.master}") boolean isAppMaster
                          , @Value("${hibernate.batchSize}") int hibernateBatchSize
                          , @Value("${file.upload-dir}") String fileUploadBaseDir
                          , @Value("${mqtt.ssl.ca.cert}") String mqttSslCaCert
                          , @Value("${mqtt.ssl.client.cert}") String mqttSslClientCert
                          , @Value("${mqtt.ssl.client.key}") String mqttSslClientKey) {
        PropertiesConfig.APP_MASTER = isAppMaster;
        
        PropertiesConfig.HIBERNATE_BATCH_SIZE = hibernateBatchSize;
        
        PropertiesConfig.FILE_UPLOAD_BASE_DIR = fileUploadBaseDir;
        
        PropertiesConfig.MQTT_SSL_CA_CERT = mqttSslCaCert;
        PropertiesConfig.MQTT_SSL_CLIENT_CERT = mqttSslClientCert;
        PropertiesConfig.MQTT_SSL_CLIENT_KEY = mqttSslClientKey;
    }
}
