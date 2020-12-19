/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.id.controller;

import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.id.constant.Constant;
import com.elcom.id.messaging.rabbitmq.RabbitMQClient;
import com.elcom.id.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.id.model.dto.OtpExpiredDTO;
import com.elcom.id.sms.MontySmsConfig;
import com.elcom.id.sms.MontySmsRequest;
import com.elcom.id.sms.MontySmsResponse;
import com.elcom.id.utils.JSONConverter;
import com.elcom.id.utils.StringUtil;
import com.elcom.id.utils.encrypt.RSAEncrypter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Admin
 */
public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private RabbitMQClient rabbitMQClient;

    @Autowired
    private RedisTemplate redisTemplate;

    protected boolean authenDeviceToken(Map<String, String> headerMap) {
        
        RequestMessage requestMessage = new RequestMessage("GET", Constant.SRV_VER + Constant.API_DEVICE_DECRYPT_DEVICE_TOKEN, null, null, null, headerMap);
        String req = JSONConverter.toJSON(requestMessage);
        String deviceId = this.rabbitMQClient.callRpcService(RabbitMQProperties.VS_DEVICE_RPC_EXCHANGE
                                                    , RabbitMQProperties.VS_DEVICE_RPC_QUEUE_NAME
                                                    , RabbitMQProperties.VS_DEVICE_RPC_KEY, req);
        LOGGER.info(" Request ==> [{}]\n Response <== [{}]", req, deviceId);
        if( StringUtil.isNullOrEmpty(deviceId) ) {
            LOGGER.error("authToken after decrypt is invalid!");
            return false;
        }
        return true;
    }
    
    /**
     * Check quyền của user ứng với request method và api đang gọi
     *
     * @param requestMethod : Request method POST, GET, PUT, DELETE
     * @param userUuid user uuid
     * @param apiPath api link
     * @return
     */
    public boolean authorizeRBAC(String requestMethod, String userUuid, String apiPath) {
        //Set body param
        Map<String, Object> bodyParam = new HashMap<>();
        bodyParam.put("requestMethod", requestMethod);
        bodyParam.put("uuid", userUuid);
        bodyParam.put("apiPath", apiPath);

        //Authorize user action with api -> call rbac service
        RequestMessage rbacRpcRequest = new RequestMessage();
        rbacRpcRequest.setRequestMethod("POST");
        rbacRpcRequest.setRequestPath(RabbitMQProperties.RBAC_RPC_AUTHOR_URL);
        rbacRpcRequest.setBodyParam(bodyParam);
        rbacRpcRequest.setUrlParam(null);
        rbacRpcRequest.setHeaderParam(null);
        String result = rabbitMQClient.callRpcService(RabbitMQProperties.RBAC_RPC_EXCHANGE,
                RabbitMQProperties.RBAC_RPC_QUEUE, RabbitMQProperties.RBAC_RPC_KEY, rbacRpcRequest.toJsonString());
        LOGGER.info("authorizeRBAC - result: " + result);
        if (result != null) {
            ObjectMapper mapper = new ObjectMapper();
            ResponseMessage response = null;
            try {
                response = mapper.readValue(result, ResponseMessage.class);
            } catch (JsonProcessingException ex) {
                LOGGER.info("Lỗi parse json khi gọi kiểm tra quyền từ rbac service: " + ex.toString());
                return false;
            }
            return (response != null && response.getStatus() == HttpStatus.OK.value());
        }
        return false;
    }

    /**
     * Send SMS via SMS Service
     *
     * @param mobile : Số điện thoại
     * @param code: mã code
     * @return
     */
    /*public boolean sendSms(String mobile, String code) {
        //Set body param
        Map<String, Object> bodyParam = new HashMap<>();
        bodyParam.put("mobile", mobile);
        bodyParam.put("code", code);

        //Authorize user action with api -> call rbac service
        RequestMessage smsRpcRequest = new RequestMessage();
        smsRpcRequest.setRequestMethod("POST");
        smsRpcRequest.setRequestPath(RabbitMQProperties.SMS_RPC_URL);
        smsRpcRequest.setBodyParam(bodyParam);
        smsRpcRequest.setUrlParam(null);
        smsRpcRequest.setHeaderParam(null);
        String result = rabbitMQClient.callRpcService(RabbitMQProperties.SMS_RPC_EXCHANGE,
                RabbitMQProperties.SMS_RPC_QUEUE, RabbitMQProperties.SMS_RPC_KEY,
                smsRpcRequest.toJsonString());
        LOGGER.info("sendSms - result: " + result);
        if (result != null) {
            ObjectMapper mapper = new ObjectMapper();
            ResponseMessage response = null;
            try {
                response = mapper.readValue(result, ResponseMessage.class);
            } catch (JsonProcessingException ex) {
                LOGGER.info("Lỗi parse json khi gọi send sms từ SMS Service: " + ex.toString());
                return false;
            }
            return (response != null && response.getStatus() == HttpStatus.OK.value());
        }
        return false;
    }*/
    public boolean sendSms(String mobile, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestId = String.valueOf(System.currentTimeMillis());
        String data = requestId + "|" + mobile + "|" + code;
        String sign = RSAEncrypter.sign(data, MontySmsConfig.SMS_RSA_PRIVATE_KEY);

        MontySmsRequest montySmsRequest = new MontySmsRequest();
        montySmsRequest.setRequestId(requestId);
        montySmsRequest.setCode(code);
        montySmsRequest.setMobile(mobile);
        montySmsRequest.setService(MontySmsConfig.SMS_API_SERVICE);
        montySmsRequest.setSign(sign);

        // Dữ liệu đính kèm theo yêu cầu.
        HttpEntity<MontySmsRequest> requestBody = new HttpEntity<>(montySmsRequest, headers);

        // Gửi yêu cầu với phương thức POST.
        LOGGER.info("sendSms - request: " + montySmsRequest.toJsonString());
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject(MontySmsConfig.SMS_API_URL, requestBody, String.class);
        LOGGER.info("sendSms - response: " + result);
        if (result != null) {
            ObjectMapper mapper = new ObjectMapper();
            MontySmsResponse response = null;
            try {
                response = mapper.readValue(result, MontySmsResponse.class);
            } catch (JsonProcessingException ex) {
                LOGGER.info("Lỗi parse json khi gọi send sms từ SMS Service: " + ex.toString());
                return false;
            }
            return (response != null && response.getStatus() == HttpStatus.OK.value());
        }
        return false;
    }

    public OtpExpiredDTO getOtpExpiredTime(String userUuid) {
        String folderKey = Constant.LEANR_OTP_KEY;
        OtpExpiredDTO result = null;
        try {
            if (redisTemplate.opsForHash().hasKey(folderKey, userUuid)) {
                result = (OtpExpiredDTO) redisTemplate.opsForHash().get(folderKey, userUuid);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.info("Lỗi lấy dữ liệu OTP Expired từ Redis: " + ex.toString());
        }
        return result;
    }

    public boolean pushOtpExpiredTime(String userUuid, OtpExpiredDTO result) {
        String folderKey = Constant.LEANR_OTP_KEY;
        try {
            if (result != null) {
                redisTemplate.opsForHash().put(folderKey, userUuid, result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.info("Lỗi đẩy OTP Expired lên Redis: " + ex.toString());
            return false;
        }
        return true;
    }
}
