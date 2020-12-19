package com.elcom.usersystem.controller;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.usersystem.constant.Constant;
import com.elcom.usersystem.messaging.rabbitmq.RabbitMQClient;
import com.elcom.usersystem.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.usersystem.model.dto.AuthorizationResponseDTO;
import com.elcom.usersystem.utils.JSONConverter;
import com.elcom.usersystem.utils.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${user.authen.use}")
    private String authenUse;

    @Value("${user.authen.http.url}")
    private String authenHttpUrl;

    protected boolean authenDeviceToken(Map<String, String> headerMap) {

        RequestMessage requestMessage = new RequestMessage("GET", Constant.SRV_VER + Constant.API_DEVICE_DECRYPT_DEVICE_TOKEN, null, null, null, headerMap);
        String req = JSONConverter.toJSON(requestMessage);
        String deviceId = this.rabbitMQClient.callRpcService(RabbitMQProperties.VS_DEVICE_RPC_EXCHANGE,
                RabbitMQProperties.VS_DEVICE_RPC_QUEUE_NAME,
                RabbitMQProperties.VS_DEVICE_RPC_KEY, req);
        LOGGER.info(" Request ==> [{}]\n Response <== [{}]", req, deviceId);
        if (StringUtil.isNullOrEmpty(deviceId)) {
            LOGGER.error("authToken after decrypt is invalid!");
            return false;
        }
        return true;
    }

    /**
     * Check token qua id service => Trả về detail user
     *
     * @param headerMap header chứa jwt token
     * @return detail user
     */
    public AuthorizationResponseDTO authenToken(Map<String, String> headerMap) {
        //Authen -> call rpc authen headerMap
        RequestMessage userRpcRequest = new RequestMessage();
        userRpcRequest.setRequestMethod("POST");
        userRpcRequest.setRequestPath(RabbitMQProperties.USER_RPC_AUTHEN_URL);
        userRpcRequest.setBodyParam(null);
        userRpcRequest.setUrlParam(null);
        userRpcRequest.setHeaderParam(headerMap);
        String result = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, userRpcRequest.toJsonString());
        LOGGER.info("authenToken - result: " + result);
        if (result != null) {
            ObjectMapper mapper = new ObjectMapper();
            //DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //mapper.setDateFormat(df);
            ResponseMessage response = null;
            try {
                response = mapper.readValue(result, ResponseMessage.class);
            } catch (JsonProcessingException ex) {
                LOGGER.info("Lỗi parse json khi gọi user service verify: " + ex.getCause().toString());
                return null;
            }

            if (response != null && response.getStatus() == HttpStatus.OK.value()) {
                try {
                    //Process
                    MessageContent content = response.getData();
                    Object data = content.getData();
                    if (data != null) {
                        AuthorizationResponseDTO dto = null;
                        if (data.getClass() == LinkedHashMap.class) {
                            dto = new AuthorizationResponseDTO((Map<String, Object>) data);
                        } else if (data.getClass() == AuthorizationResponseDTO.class) {
                            dto = (AuthorizationResponseDTO) data;
                        }
                        if (dto != null && !StringUtil.isNullOrEmpty(dto.getUuid())) {
                            return dto;
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.info("Lỗi giải mã AuthorizationResponseDTO khi gọi user service verify: " + ex.getCause().toString());
                    return null;
                }
            } else {
                //Forbidden
                return null;
            }
        } else {
            //Forbidden
            return null;
        }
        return null;
    }

    /**
     * Get list user from ID service with user uuid list
     *
     * @param uuidList
     * @param headerMap map contains jwt token to authen
     * @return
     */
    public Map<String, AuthorizationResponseDTO> getUserMap(List<String> uuidList, Map<String, String> headerMap) {
        Map<String, AuthorizationResponseDTO> dtoMap = null;
        try {
            if (uuidList != null && !uuidList.isEmpty()) {
                Map<String, Object> requestIdBodyParam = new HashMap<>();
                requestIdBodyParam.put("uuids", uuidList);
                RequestMessage request = new RequestMessage("POST", RabbitMQProperties.USER_RPC_UUIDLIST_URL,
                        null, null, requestIdBodyParam, headerMap);
                String result = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                        RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, request.toJsonString());
                //LOGGER.info("Result: " + result);
                if (!StringUtil.isNullOrEmpty(result)) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        ResponseMessage resultResponse = mapper.readValue(result, ResponseMessage.class);
                        if (resultResponse != null && resultResponse.getStatus() == HttpStatus.OK.value()
                                && resultResponse.getData() != null) {
                            JsonNode jsonNode = mapper.readTree(result);
                            List<AuthorizationResponseDTO> dtoList = Arrays.asList(mapper.treeToValue(jsonNode.get("data").get("data"),
                                    AuthorizationResponseDTO[].class));
                            if (dtoList != null && !dtoList.isEmpty()) {
                                dtoMap = new HashMap<>();
                                for (AuthorizationResponseDTO tmpDto : dtoList) {
                                    dtoMap.put(tmpDto.getUuid(), tmpDto);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        LOGGER.error("Error to parse json >>> " + ex.getCause().toString());
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return dtoMap;
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
                LOGGER.info("Lỗi parse json khi gọi kiểm tra quyền từ rbac service: " + ex.getCause().toString());
                return false;
            }
            return (response != null && response.getStatus() == HttpStatus.OK.value());
        }
        return false;
    }

    public String getElapsedTime(long miliseconds) {
        //return (miliseconds / 1000.0) + "(s)";
        return miliseconds + " (ms)";
    }
}
