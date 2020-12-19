/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.controller;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.rbac.constant.Constant;
import com.elcom.rbac.messaging.rabbitmq.RabbitMQClient;
import com.elcom.rbac.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.rbac.model.Path;
import com.elcom.rbac.model.Role;
import com.elcom.rbac.model.RolePathPermission;
import com.elcom.rbac.model.RoleUser;
import com.elcom.rbac.model.dto.AuthorizationResponseDTO;
import com.elcom.rbac.service.PathService;
import com.elcom.rbac.service.RoleModulePermissionService;
import com.elcom.rbac.service.RoleService;
import com.elcom.rbac.service.RoleUserService;
import com.elcom.rbac.utils.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Admin
 */
public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private RabbitMQClient rabbitMQClient;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private PathService pathService;

    @Autowired
    private RoleModulePermissionService roleModulePermissionService;

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
                LOGGER.info("Lỗi parse json khi gọi user service verify: " + ex.toString());
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
                    LOGGER.info("Lỗi giải mã AuthorizationResponseDTO khi gọi user service verify: " + ex.toString());
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

        if (StringUtil.isNullOrEmpty(userUuid) || StringUtil.isNullOrEmpty(apiPath) || StringUtil.isNullOrEmpty(requestMethod)) {
            return false;
        } else {
            //Get role user with uuid
            List<RoleUser> roleUserList = roleUserService.findByUuidUser(userUuid);
            if (roleUserList != null && !roleUserList.isEmpty()) {
                List<Role> roleList = new ArrayList<>();
                for (RoleUser tmp : roleUserList) {
                    roleList.add(tmp.getRoleCode());
                }
                //Get path
                List<Path> pathList = pathService.findByApiPath(apiPath);
                if (pathList != null && !pathList.isEmpty()) {

                    //Get role module permission
                    List<RolePathPermission> permissionList = roleModulePermissionService.findByRoleAndPath(requestMethod, roleList, pathList);
                    if (permissionList != null && !permissionList.isEmpty()) {
                        RolePathPermission firstRow = permissionList.get(0);
                        boolean status = false;

                        switch (requestMethod) {
                            case "GET":
                                if (firstRow.getCanRead() == 1) {
                                    status = true;//Read
                                }
                                break;
                            case "POST":
                                if (firstRow.getCanCreate() == 1) {
                                    status = true;//Create
                                }
                                break;
                            case "PUT":
                                if (firstRow.getCanUpdate() == 1) {
                                    status = true;//Update put
                                }
                                break;
                            case "PATCH":
                                if (firstRow.getCanUpdate() == 1) {
                                    status = true;//Update patch
                                }
                                break;
                            case "DELETE":
                                if (firstRow.getCanDelete() == 1) {
                                    status = true;//Delete
                                }
                                break;
                            default:
                                break;
                        }

                        bodyParam.put("permission", status);
                        return status;
                    } else {
                        bodyParam.put("permission", HttpStatus.NOT_FOUND.getReasonPhrase());
                        return false;
                    }
                } else {
                    bodyParam.put("permission", HttpStatus.NOT_FOUND.getReasonPhrase());
                    return false;
                }
            } else {
                bodyParam.put("permission", HttpStatus.NOT_FOUND.getReasonPhrase());
                return false;
            }
        }
    }

    /**
     * Trả về lỗi check quyền admin của uuid user
     *
     * @param uuid uuid user
     * @return null nếu có quyền, kiểu lỗi nếu không có quyền
     */
    public String authorUserService(String uuid) {
        String errorMsg = null;
        //Get role admin
        List<Role> adminRoleList = roleService.findAdminRoleList();
        if (adminRoleList == null || adminRoleList.isEmpty()) {
            errorMsg = "Hệ thống chưa có user admin nào. Vui lòng liên hệ kỹ thuật xử lý để tạo admin";
        } else {
            //Check uuid - admin role
            RoleUser requestRoleUser = roleUserService.findByUuidUserAndRoleCodeInAndIsDelete(uuid, adminRoleList, 0);
            if (requestRoleUser == null || (requestRoleUser.getIsDelete() != null && requestRoleUser.getIsDelete() == 1)) {
                errorMsg = "Chỉ có tài khoản admin mới được phép thao tác cho nội dung này";
            }
        }
        return errorMsg;
    }

    /**
     * Get list user from ID service with user uuid list
     *
     * @param uuidList
     * @param headerMap map contains jwt token to authen
     * @return
     */
    public Map<String, AuthorizationResponseDTO> getUserMap(List<String> uuidList, Map<String, String> headerMap) {
        if (uuidList != null && !uuidList.isEmpty()) {
            Map<String, Object> requestIdBodyParam = new HashMap<>();
            requestIdBodyParam.put("uuids", uuidList);
            RequestMessage request = new RequestMessage("POST", RabbitMQProperties.USER_RPC_UUIDLIST_URL,
                    null, null, requestIdBodyParam, headerMap);
            String result = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                    RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY,
                    request.toJsonString());
            System.out.println("Result: " + result);
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
                            Map<String, AuthorizationResponseDTO> dtoMap = new HashMap<>();
                            for (AuthorizationResponseDTO tmpDto : dtoList) {
                                dtoMap.put(tmpDto.getUuid(), tmpDto);
                            }
                            return dtoMap;
                        }
                    }
                } catch (JsonProcessingException ex) {
                    LOGGER.error("Error to parse json >>> " + ex.toString());
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
}
