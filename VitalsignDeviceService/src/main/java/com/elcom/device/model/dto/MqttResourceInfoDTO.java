package com.elcom.device.model.dto;

/**
 *
 * @author anhdv
 */
public interface MqttResourceInfoDTO {
    
    String getName();
    String getProtocol();
    String getIpAddress();
    String getPortNumber();
    String getAccount();
    String getPassword();
}
