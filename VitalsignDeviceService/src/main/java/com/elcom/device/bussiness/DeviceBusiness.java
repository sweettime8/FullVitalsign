package com.elcom.device.bussiness;

import com.elcom.device.constant.Constant;
import com.elcom.device.controller.BaseController;
import com.elcom.device.model.Gate;
import com.elcom.device.utils.StringUtil;
import com.elcom.device.validation.DeviceValidation;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.elcom.device.service.DeviceService;
import com.elcom.device.model.dto.MqttResourceInfoDTO;
import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.ResponseMessage;
import org.springframework.http.HttpStatus;

/**
 *
 * @author anhdv
 */
@Controller
public class DeviceBusiness extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceBusiness.class);

    @Autowired
    private DeviceService deviceService;

    /** Update lại patient_profile_id cho gate
     *  Check Auth, ko check RBAC
     * @param bodyParam
     * @param headerMap
     * @param methodType
     * @param requestPath
     * @return 200|400|500 */
    public ResponseMessage updateNewHealthProfileForGate(Map<String, Object> bodyParam, Map<String, String> headerMap, String methodType, String requestPath) {
        ResponseMessage response = null;
        try {
            String homeAdminId = authenDeviceToken(headerMap);
            if( StringUtil.isNullOrEmpty(homeAdminId) )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            /*else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));*/
            else {
                if (bodyParam == null || bodyParam.isEmpty())
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                               new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
                else {
                    String currentHealthProfileId = (String) bodyParam.get("currentHealthProfileId");
                    String newHealthProfileId = (String) bodyParam.get("newHealthProfileId");
                    String gateId = (String) bodyParam.get("gateId");
                    
                    String validationMsg = new DeviceValidation().validateUpdateNewHealthProfileForGate(homeAdminId, currentHealthProfileId, newHealthProfileId, gateId);
                    if( validationMsg != null )
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), validationMsg,
                                   new MessageContent(HttpStatus.BAD_REQUEST.value(), validationMsg, null));
                    else {
                        boolean result = this.deviceService.updateNewHealthProfileForGate(homeAdminId, currentHealthProfileId, newHealthProfileId, gateId);
                        response = new ResponseMessage(HttpStatus.OK.value(), result ? HttpStatus.OK.toString() : HttpStatus.NOT_MODIFIED.toString(),
                                        new MessageContent(HttpStatus.OK.value(), result ? HttpStatus.OK.toString() : HttpStatus.NOT_MODIFIED.toString(), result));
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return response;
    }
    
//    public Gate findDeviceInfoByGateId(String urlParam) {
//        if (StringUtil.isNullOrEmpty(urlParam)) {
//            LOGGER.error(Constant.VALIDATION_INVALID_PARAM_VALUE);
//        } else {
//            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
//            String gateId = (String) params.get("gateId");
//            return this.deviceService.findDeviceInfoByGateId(gateId);
//        }
//        return null;
//    }

    /**
     * Check deviceId(gate | display) này có tồn tại trong hệ thống và đang hoạt
     * động hay không? Service để VitalsignFileService gọi chéo sang, Ko check
     * Auth, Ko check RBAC
     *
     * @param urlParam (deviceType, deviceId)
     * @return boolean */
    public String validateGateReturnHomeAdminId(String urlParam) {
        if( StringUtil.isNullOrEmpty(urlParam) ) {
            LOGGER.error(Constant.VALIDATION_INVALID_PARAM_VALUE);
            return "";
        }else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String deviceId = (String) params.get("deviceId");
            if( StringUtil.isNullOrEmpty(deviceId) ) {
                LOGGER.error("deviceId không được trống");
                return "";
            }
            String result = this.deviceService.validateGateReturnHomeAdminId(deviceId);
            return result!=null ? result : "";
        }
    }
    
    /**
     * Giải mã deviceToken(được gen ra theo publicKey) bằng privateKey
     *
     * @param headerParam (deviceToken)
     * @return boolean */
    public String decryptDeviceToken(Map<String, String> headerParam) {
        if( headerParam==null || headerParam.isEmpty() )
            LOGGER.error(Constant.VALIDATION_INVALID_PARAM_VALUE);
        else {
            String deviceToken = headerParam.get("device-token");
            if( StringUtil.isNullOrEmpty(deviceToken) ) {
                LOGGER.error("device-token (header) không được trống");
                return "";
            }
            return this.deviceService.decryptDeviceToken(deviceToken);
        }
        return "";
    }

    /**
     * Lấy thông tin kết nối MQTT Service để VitalsignFileService gọi chéo sang,
     * Ko check Auth, Ko check RBAC
     *
     * @param urlParam (resourceCode)
     * @return MqttResourceInfoDTO
     */
    public MqttResourceInfoDTO findResourceInfo(String urlParam) {
        if (StringUtil.isNullOrEmpty(urlParam)) {
            LOGGER.error(Constant.VALIDATION_INVALID_PARAM_VALUE);
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String resourceCode = (String) params.get("resourceCode");
            return this.deviceService.findResourceInfo(resourceCode);
        }
        return null;
    } 
}
