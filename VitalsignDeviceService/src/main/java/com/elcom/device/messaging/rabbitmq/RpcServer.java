package com.elcom.device.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elcom.device.utils.StringUtil;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.device.bussiness.DeviceBusiness;
import com.elcom.device.bussiness.BuDeviceBusiness;
import com.elcom.device.constant.Constant;
import com.elcom.device.model.dto.DeviceHaDTO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.elcom.device.model.dto.MqttResourceInfoDTO;
import com.elcom.device.utils.JSONConverter;
import java.util.List;

/**
 *
 * @author Admin
 */
public class RpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    @Autowired
    private DeviceBusiness deviceBusiness;

    @Autowired
    private BuDeviceBusiness buDeviceBusiness;

    @RabbitListener(queues = "${vs.device.rpc.queue}")
    public String rpcReceive(String json) {
        try {
            LOGGER.info(" [-->] Server received request for " + json);
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.setDateFormat(df);
            RequestMessage request = mapper.readValue(json, RequestMessage.class);
            ResponseMessage response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null);
            if (request != null) {
                String requestPath = request.getRequestPath();
                String urlParam = request.getUrlParam();
                String pathParam = request.getPathParam();
                Map<String, Object> bodyParam = request.getBodyParam();
                Map<String, String> headerParam = request.getHeaderParam();

                switch (request.getRequestMethod()) {
                    case "GET":
                        if ((Constant.SRV_VER + Constant.API_DEVICE_CHECK_VALID_DEVICE_ID).equalsIgnoreCase(requestPath)) {
                            if (pathParam != null && pathParam.length() > 0) {
                                response = null;
                            } else {
                                String userId = this.deviceBusiness.validateGateReturnHomeAdminId(urlParam);
                                LOGGER.info(" [<--] Server returned " + userId);
                                return userId;
                            }
                        } else if ((Constant.SRV_VER + Constant.API_DEVICE_FIND_RESOURCE_INFO).equalsIgnoreCase(requestPath)) {
                            if (pathParam != null && pathParam.length() > 0) {
                                response = null;
                            } else {
                                MqttResourceInfoDTO mqttResourceInfo = this.deviceBusiness.findResourceInfo(urlParam);
                                if(mqttResourceInfo==null) {
                                    LOGGER.info(" [<--] Server returned null, mqttResourceInfo is null");
                                    return "";
                                }
                                
                                String res = new ObjectMapper().writerFor(MqttResourceInfoDTO.class).writeValueAsString(mqttResourceInfo);
                                LOGGER.info(" [<--] Server returned " + res);
                                return res;
                            }
                        } else if ((Constant.SRV_VER + Constant.API_DEVICE_DECRYPT_DEVICE_TOKEN).equalsIgnoreCase(requestPath)) {
                            if (pathParam != null && pathParam.length() > 0) {
                                response = null;
                            } else {
                                String res = this.deviceBusiness.decryptDeviceToken(headerParam);
                                LOGGER.info(" [<--] Server returned " + res);
                                return res;
                            }
                        }

                        //for WEB
                        if ((Constant.SRV_VER + "/device/get-device-by-bu-id").equalsIgnoreCase(requestPath)) //get list Doctor
                        {
                            response = this.buDeviceBusiness.getDeviceByBuId(urlParam, headerParam);
                        } else if ((Constant.SRV_VER + "/device/get-detail-device").equalsIgnoreCase(requestPath)) {
                            response = this.buDeviceBusiness.getDetailDevice(urlParam, headerParam);
                        } else if ((Constant.SRV_VER + "/device/count-gate-by-ha-id").equalsIgnoreCase(requestPath)) {
                            if (pathParam != null && pathParam.length() > 0) {
                                response = null;
                            } else {
                                List<DeviceHaDTO> res = this.buDeviceBusiness.getCountDeviceByHaId(urlParam, headerParam);
                                LOGGER.info(" [<--] Server returned " + res);
                                return res != null && !res.isEmpty() ? JSONConverter.toJSON(res) : null;
                            }
                        }else if ((Constant.SRV_VER + "/device/find-device-for-add-customer").equalsIgnoreCase(requestPath)) {
                            response = this.buDeviceBusiness.findDeviceforAddCustomer(urlParam, headerParam);
                        }

                        break;
                    case "POST":
                        if ((Constant.SRV_VER + "/score-management/point-type").equalsIgnoreCase(requestPath)) //response = this.deviceBusiness.savePointType(bodyParam, headerParam, "POST", "INSERT", requestPath);
                        {
                            response = null;
                        } else if ((Constant.SRV_VER + "/score-management/level").equalsIgnoreCase(requestPath)) {
                            response = null;
                        } else if ((Constant.SRV_VER + "/device/add-device-for-customer").equalsIgnoreCase(requestPath)) {
                            response = this.buDeviceBusiness.addDeviceForCustomer(bodyParam, headerParam);
                        }
                        break;
                    case "PUT":
                        if ((Constant.SRV_VER + Constant.API_DEVICE_UPDATE_PATIENT_PROFILE_ID_FOR_GATE).equalsIgnoreCase(requestPath)) {
                            response = this.deviceBusiness.updateNewHealthProfileForGate(bodyParam, headerParam, "PUT", requestPath);
                        } else if ((Constant.SRV_VER + "/score-management/level").equalsIgnoreCase(requestPath)) {
                            response = null;
                        }
                        break;
                    case "PATCH":
                        response = null;
                        break;
                    case "DELETE":
                        response = null;
                        break;
                    default:
                        break;
                }
            }
            LOGGER.info(" [<--] Server returned " + (response != null ? response.toJsonString() : null));
            return response != null ? response.toJsonString() : null;
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return null;
    }
}
