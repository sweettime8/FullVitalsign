package com.elcom.device.service;

import com.elcom.device.model.Gate;
import com.elcom.device.model.dto.DeviceHaDTO;
import java.util.List;
import com.elcom.device.model.dto.MqttResourceInfoDTO;

/**
 *
 * @author anhdv
 */
public interface DeviceService {

    //Gate findDeviceInfoByGateId(String gateId);
    
    String validateGateReturnHomeAdminId(String deviceId);
    
    String decryptDeviceToken(String deviceToken);
    
    MqttResourceInfoDTO findResourceInfo(String resourceCode);
    
    boolean updateNewHealthProfileForGate(String homeAdminId, String currentHealthProfileId, String newHealthProfileId, String gateId);

    List<Gate> findGateByBuId(String buId);
    
    Gate findGateById(String gateId);
    
    List<DeviceHaDTO> getCountDeviceByHaId(String[] homeAdminIdLst);
    
    List<Gate> findDeviceforAddCustomer(String buid,String customerId, String gateNameOrSerial);
}