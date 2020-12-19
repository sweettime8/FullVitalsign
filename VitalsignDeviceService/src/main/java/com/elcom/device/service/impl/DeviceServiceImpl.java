package com.elcom.device.service.impl;

import com.elcom.device.model.Gate;
import com.elcom.device.model.dto.DeviceHaDTO;
import com.elcom.device.repository.DeviceCustomizeRepository;
import com.elcom.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.elcom.device.utils.StringUtil;
import com.elcom.device.service.DeviceService;
import com.elcom.device.model.dto.MqttResourceInfoDTO;
import com.elcom.device.repository.GateCustomizeRepository;
import com.elcom.device.utils.RsaDecryption;

/**
 *
 * @author anhdv
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceCustomizeRepository deviceCustomizeRepository;

    @Autowired
    private GateCustomizeRepository gateCustomizeRepository;

    @Override
    public String validateGateReturnHomeAdminId(String deviceId) {
        return this.deviceCustomizeRepository.validateGateReturnHomeAdminId(deviceId);
    }

    @Override
    public String decryptDeviceToken(String deviceToken) {
        String deviceId = RsaDecryption.decrypt(deviceToken);
        LOGGER.info("deviceId decrypted: [{}]", deviceId);
        return deviceId;
    }

    @Override
    public MqttResourceInfoDTO findResourceInfo(String resourceCode) {
        return this.deviceRepository.findResourceInfo(resourceCode);
    }

//    @Override
//    public Gate findDeviceInfoByGateId(String gateId) {
//        return this.gateCustomizeRepository.findBySerialNumber(gateId);
//    }
    @Override
    public boolean updateNewHealthProfileForGate(String homeAdminId, String currentHealthProfileId, String newHealthProfileId, String gateId) {
        return this.deviceCustomizeRepository.updateNewHealthProfileForGate(homeAdminId, currentHealthProfileId, newHealthProfileId, gateId);
    }

    @Override
    public List<Gate> findGateByBuId(String buId) {
        return this.gateCustomizeRepository.findGateByBuId(buId);
    }

    @Override
    public Gate findGateById(String gateId) {
        return this.gateCustomizeRepository.findById(gateId);
    }

    @Override
    public List<DeviceHaDTO> getCountDeviceByHaId(String[] homeAdminIdLst) {
        return this.gateCustomizeRepository.getCountDeviceByHaId(homeAdminIdLst);
    }

    @Override
    public List<Gate> findDeviceforAddCustomer(String buid,String customerId, String gateNameOrSerial) {
        List<Gate> lst = null;
        try {
            lst = this.gateCustomizeRepository.findDeviceforAddCustomer(buid, customerId, gateNameOrSerial);
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return lst;
    }
}
