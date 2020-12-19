/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.rbac.controller.PathController;
import com.elcom.rbac.controller.RBACController;
import com.elcom.rbac.controller.RoleController;
import com.elcom.rbac.controller.RoleModulePermissionController;
import com.elcom.rbac.controller.RoleUserController;
import com.elcom.rbac.controller.ServiceController;
import com.elcom.rbac.utils.GatewayDebugUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Admin
 */
public class RpcServer {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
    
    @Autowired
    private RBACController rbacController;
    
    @Autowired
    private ServiceController serviceController;
    
    @Autowired
    private RoleController roleController;
    
    @Autowired
    private PathController pathController;
    
    @Autowired
    private RoleUserController roleUserController;
    
    @Autowired
    private RoleModulePermissionController permissionController;
    
    @Autowired
    private RabbitMQClient rabbitMQClient;
    
    @RabbitListener(queues = "${rbac.rpc.queue}")
    public String processService(String json) {
        long start = System.currentTimeMillis();
        try {
            LOGGER.info(" [-->] Server received request for " + json);
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.setDateFormat(df);
            RequestMessage request = mapper.readValue(json, RequestMessage.class);
            //Process here
            //Test call user rpc
            //RequestMessage userRequest = new RequestMessage("GET", "/v1.0/user", null, "1", null, null);
            //String result = rabbitMQClient.callRpcService("user.rpc.exchange", "user_rpc_queue", "user_rpc", userRequest.toJsonString());
            ResponseMessage response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null);
            if (request != null) {
                String requestPath = request.getRequestPath();
                String urlParam = request.getUrlParam();
                String pathParam = request.getPathParam();
                Map<String, Object> bodyParam = request.getBodyParam();
                Map<String, String> headerParam = request.getHeaderParam();
                //GatewayDebugUtil.debug(requestPath, urlParam, pathParam, bodyParam, headerParam);
                
                switch (request.getRequestMethod()) {
                    case "GET":
                        if ("/v1.0/rbac".equalsIgnoreCase(requestPath) && urlParam != null && urlParam.length() > 0) // Get all
                            response = rbacController.getAllRBAC(urlParam);
                        else if ("/v1.0/rbac".equalsIgnoreCase(requestPath) && pathParam != null && pathParam.length() > 0) // Get details
                            response = rbacController.getDetailRBAC(pathParam);
                        else if("/v1.0/rbac/role".equalsIgnoreCase(requestPath)) //List Role
                            response = roleController.getListRole(headerParam, urlParam);
                        else if("/v1.0/rbac/role/userLst".equalsIgnoreCase(requestPath) && urlParam != null && urlParam.length() > 0) //List Role
                            response = roleController.getListUserByRole(headerParam, urlParam);
                        else if("/v1.0/rbac/permission".equalsIgnoreCase(requestPath) && urlParam != null && urlParam.length() > 0) //List Role
                            response = permissionController.getListPermission(headerParam, urlParam);
                        else if("/v1.0/rbac/path".equalsIgnoreCase(requestPath) && urlParam != null && urlParam.length() > 0) //List Role
                            response = pathController.getListPath(headerParam, urlParam);
                        else if("/v1.0/rbac/admin".equalsIgnoreCase(requestPath)) //List admin
                            response = roleUserController.findAdmin(urlParam, headerParam);
                        else if("/v1.0/rbac/role/user".equalsIgnoreCase(requestPath)) //List role user by user id
                            response = roleUserController.findRoleByUser(pathParam, headerParam);
                        break;
                    case "POST":
                        if ("/v1.0/rbac".equalsIgnoreCase(requestPath)) // Insert/update
                            response = rbacController.createRBAC(bodyParam);
                        else if("/v1.0/rbac/authorization".equalsIgnoreCase(requestPath)) //Authorization
                            response = rbacController.authorization(bodyParam);
                        else if("/v1.0/rbac/service".equalsIgnoreCase(requestPath)) //Service
                            response = serviceController.createService(bodyParam);
                        else if("/v1.0/rbac/role".equalsIgnoreCase(requestPath)) //Role
                            response = roleController.createRole(bodyParam, headerParam);
                        else if("/v1.0/rbac/role/user".equalsIgnoreCase(requestPath)) //Role User
                            response = roleUserController.createRoleUser(bodyParam, headerParam);
                        else if("/v1.0/rbac/permission".equalsIgnoreCase(requestPath)) //Role Module Permission
                            response = permissionController.createPermission(bodyParam, headerParam);
                        else if("/v1.0/rbac/path".equalsIgnoreCase(requestPath)) //Path
                            response = pathController.createPath(bodyParam, headerParam);
                        else if("/v1.0/rbac/role/user/default".equalsIgnoreCase(requestPath)) //Default User Role
                            response = roleUserController.createDefaultRole(bodyParam, headerParam);
                        break;
                    case "PUT":
                        if ("/v1.0/rbac".equalsIgnoreCase(requestPath))//Update
                            response = rbacController.updateRBAC(bodyParam);
                        else if("/v1.0/rbac/service".equalsIgnoreCase(requestPath)) //Service
                            response = serviceController.updateService(bodyParam, headerParam);
                        else if("/v1.0/rbac/role".equalsIgnoreCase(requestPath)) //Role
                            response = roleController.updateRole(bodyParam, headerParam);
                        else if("/v1.0/rbac/role/user".equalsIgnoreCase(requestPath)) //Role User
                            response = roleUserController.updateRoleUser(bodyParam, headerParam);
                        else if("/v1.0/rbac/permission".equalsIgnoreCase(requestPath)) //Role Module Permission
                            response = permissionController.updatePermission(bodyParam, headerParam);
                        else if("/v1.0/rbac/path".equalsIgnoreCase(requestPath)) //Path
                            response = pathController.updatePath(bodyParam, headerParam);
                        break;
                    case "PATCH":
                        break;
                    case "DELETE":
                        if ("/v1.0/rbac".equalsIgnoreCase(requestPath) && pathParam != null && pathParam.length() > 0) // Delete by uuid
                            response = rbacController.deleteRBAC(pathParam);
                        else if("/v1.0/rbac".equalsIgnoreCase(requestPath)) //Redis cache
                            response = rbacController.deleteRedis();
                        else if("/v1.0/rbac/service".equalsIgnoreCase(requestPath)) //Service
                            response = serviceController.deleteService(bodyParam);
                        else if("/v1.0/rbac/role".equalsIgnoreCase(requestPath)) //Role
                            response = roleController.deleteRole(pathParam, headerParam);
                        else if("/v1.0/rbac/role/user".equalsIgnoreCase(requestPath)) //Role User
                            response = roleUserController.deleteRoleUser(pathParam, headerParam);
                        else if("/v1.0/rbac/permission".equalsIgnoreCase(requestPath)) //Role Module Permission
                            response = permissionController.deletePermission(pathParam, headerParam);
                        else if("/v1.0/rbac/path".equalsIgnoreCase(requestPath)) //Path
                            response = pathController.deletePath(pathParam, headerParam);
                        break;
                    default:
                        break;
                }
            }
            //Response
            LOGGER.info(" [<--] Server returned " + response.toJsonString());
            long end = System.currentTimeMillis();
            LOGGER.info("[RpcServer] ================> Time to process data : " + (end - start) + " miliseconds");
            return response.toJsonString();
        } catch (Exception ex) {
            LOGGER.error("Error to process request >>> " + ex.toString());
            ex.printStackTrace();
        }
        return null;
    }
}
