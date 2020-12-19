/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.device.bussiness;

import com.elcom.device.constant.Constant;
import com.elcom.device.controller.BaseController;
import com.elcom.device.model.Gate;
import com.elcom.device.model.dto.AuthorizationResponseDTO;
import com.elcom.device.model.dto.DeviceHaDTO;
import com.elcom.device.service.DeviceService;
import com.elcom.device.utils.StringUtil;
import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.ResponseMessage;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

/**
 *
 * @author ducnh
 */
@Controller
public class BuDeviceBusiness extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuDeviceBusiness.class);

    @Autowired
    private DeviceService deviceService;

    public ResponseMessage addDeviceForCustomer(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        return response;
    }

    public ResponseMessage findDeviceforAddCustomer(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {
            //authen
            AuthorizationResponseDTO dto = authenToken(headerParam);
            if (dto == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn chưa đăng nhập"));
            } else {

                //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String buId = (String) params.get("businessUnitId");
                String customerId = (String) params.get("customerId");
                String gateNameOrSerial = (String) params.get("deviceName");

                List<Gate> lstGate = this.deviceService.findDeviceforAddCustomer(buId, customerId, gateNameOrSerial);

                if (lstGate == null || lstGate.isEmpty()) {
                    return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                            new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                }

                return new ResponseMessage(new MessageContent(lstGate));
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return response;
    }

    public ResponseMessage getDeviceByBuId(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        try {
            if (StringUtil.isNullOrEmpty(urlParam)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                AuthorizationResponseDTO dto = authenToken(headerParam);
                if (dto == null) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    "Bạn chưa đăng nhập"));
                } else {
                    Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                    String buId = (String) params.get("businessUnitId");

                    List<Gate> lstGate = this.deviceService.findGateByBuId(buId);

                    if (lstGate == null) {
                        return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    }

                    return new ResponseMessage(new MessageContent(lstGate));
                }

            }

        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }

        return response;
    }

    public ResponseMessage getDetailDevice(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        try {
            if (StringUtil.isNullOrEmpty(urlParam)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                AuthorizationResponseDTO dto = authenToken(headerParam);
                if (dto == null) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    "Bạn chưa đăng nhập"));
                } else {
                    Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                    String id = (String) params.get("id");

                    Gate gate = this.deviceService.findGateById(id);

                    if (gate == null) {
                        return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    }

                    return new ResponseMessage(new MessageContent(gate));
                }

            }

        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }

        return response;
    }

    public List<DeviceHaDTO> getCountDeviceByHaId(String urlParam, Map<String, String> headerParam) {

        if (StringUtil.isNullOrEmpty(urlParam)) {
            LOGGER.error(Constant.VALIDATION_INVALID_PARAM_VALUE);
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String homeAdminIdLst = (String) params.get("homeAdminIdLst");
            if (StringUtil.isNullOrEmpty(homeAdminIdLst)) {
                return null;
            }

            List<DeviceHaDTO> lstDeviceHaDTO = this.deviceService.getCountDeviceByHaId(homeAdminIdLst.split(","));
            if (lstDeviceHaDTO == null) {
                return null;
            }

            return lstDeviceHaDTO;
        }

        return null;
    }
}
