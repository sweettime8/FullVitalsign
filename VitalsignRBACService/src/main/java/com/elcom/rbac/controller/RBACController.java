/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.controller;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.rbac.constant.Constant;
import com.elcom.rbac.model.Path;
import com.elcom.rbac.model.Role;
import com.elcom.rbac.model.RolePathPermission;
import com.elcom.rbac.model.RoleUser;
import com.elcom.rbac.service.ModulePathService;
import com.elcom.rbac.service.PathService;
import com.elcom.rbac.service.RoleModulePermissionService;
import com.elcom.rbac.service.RoleUserService;
import com.elcom.rbac.utils.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class RBACController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RBACController.class);

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private PathService pathService;

    @Autowired
    private ModulePathService modulePathService;

    @Autowired
    private RoleModulePermissionService roleModulePermissionService;

    @Autowired
    private RedisTemplate redisTemplate;

    public ResponseMessage deleteRedis() {
        redisTemplate.delete(Constant.REDIS_ROLE_USER_KEY + "_f8b3ef52-a8c5-499f-a204-7e825b653155");
        redisTemplate.delete(Constant.REDIS_MODULE_PATH_KEY + "_/v1.0/user/list");
        redisTemplate.delete(Constant.REDIS_ROLE_MODULE_PERMISSION_KEY + "_GET_nulnull");
        redisTemplate.delete("ROLE_MODULE_PERMISSION_GET_USER_VIEW-USER_MANAGE-");
        redisTemplate.delete("ROLE_MODULE_PERMISSION_POST_USER_VIEW-USER_MANAGE-");
        redisTemplate.delete("ROLE_MODULE_PERMISSION_PUT_USER_VIEW-USER_MANAGE-");
        redisTemplate.delete("ROLE_MODULE_PERMISSION_PATCH_USER_VIEW-USER_MANAGE-");
        redisTemplate.delete("ROLE_MODULE_PERMISSION_DELETE_USER_VIEW-USER_MANAGE-");

        return new ResponseMessage(new MessageContent("Xóa redis cache thành công"));
    }

    public ResponseMessage getAllRBAC(String urlParam) {
        ResponseMessage response = new ResponseMessage(HttpStatus.NOT_FOUND.value(),
                Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(),
                        Constant.VALIDATION_DATA_NOT_FOUND, null));
        Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
        int currentPage = Integer.parseInt(params.get("currentPage"));
        int rowsPerPage = Integer.parseInt(params.get("rowsPerPage"));
        String sort = params.get("sort");

        if (currentPage == 0 || rowsPerPage == 0 || sort == null) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            if (!StringUtil.isNullOrEmpty(sort) && !"uuid".equalsIgnoreCase(sort)
                    && !"email".equalsIgnoreCase(sort) && !"createdAt".equalsIgnoreCase(sort)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                "Không có kiểu sort theo " + sort));
            } else {
                //UserPagingDTO userDTO = userService.findAll(currentPage, rowsPerPage, sort);
                //if (userDTO == null || userDTO.getDataRows() == null || userDTO.getDataRows().isEmpty()) {
                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                //} else {
                //    response = new ResponseMessage(new MessageContent(userDTO.getDataRows(), userDTO.getTotalRows()));
                //}
            }
        }
        return response;
    }

    public ResponseMessage getDetailRBAC(String pathParam) {
        ResponseMessage response = new ResponseMessage(HttpStatus.NOT_FOUND.value(),
                Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(),
                        Constant.VALIDATION_DATA_NOT_FOUND, null));

        return response;
    }

    public ResponseMessage createRBAC(Map<String, Object> bodyParam) {
        ResponseMessage response = new ResponseMessage(HttpStatus.NOT_FOUND.value(),
                Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(),
                        Constant.VALIDATION_DATA_NOT_FOUND, null));

        return response;
    }

    public ResponseMessage updateRBAC(Map<String, Object> bodyParam) {
        ResponseMessage response = new ResponseMessage(HttpStatus.NOT_FOUND.value(),
                Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(),
                        Constant.VALIDATION_DATA_NOT_FOUND, null));

        return response;
    }

    public ResponseMessage deleteRBAC(String pathParam) {
        ResponseMessage response = new ResponseMessage(HttpStatus.NOT_FOUND.value(),
                Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(),
                        Constant.VALIDATION_DATA_NOT_FOUND, null));

        return response;
    }

    //Authorization
    public ResponseMessage authorization(Map<String, Object> bodyParam) {
        ResponseMessage response = new ResponseMessage(HttpStatus.NOT_FOUND.value(),
                Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(),
                        Constant.VALIDATION_DATA_NOT_FOUND, null));
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            String requestMethod = (String) bodyParam.get("requestMethod");
            String uuid = (String) bodyParam.get("uuid");
            String apiPath = (String) bodyParam.get("apiPath");
            if (StringUtil.isNullOrEmpty(uuid) || StringUtil.isNullOrEmpty(apiPath) || StringUtil.isNullOrEmpty(requestMethod)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                //Get role user with uuid
                List<RoleUser> roleUserList = roleUserService.findByUuidUser(uuid);
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
                            response = new ResponseMessage(status ? HttpStatus.OK.value() : HttpStatus.FORBIDDEN.value(), status ? HttpStatus.OK.getReasonPhrase() : HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(status ? HttpStatus.OK.value() : HttpStatus.FORBIDDEN.value(), status ? HttpStatus.OK.getReasonPhrase() : HttpStatus.FORBIDDEN.getReasonPhrase(), bodyParam));
                        } else {
                            bodyParam.put("permission", HttpStatus.NOT_FOUND.getReasonPhrase());
                            response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                                    new MessageContent(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), bodyParam));
                        }
                    } else {
                        bodyParam.put("permission", HttpStatus.NOT_FOUND.getReasonPhrase());
                        response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                                new MessageContent(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), bodyParam));
                    }
                } else {
                    bodyParam.put("permission", HttpStatus.NOT_FOUND.getReasonPhrase());
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                            new MessageContent(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), bodyParam));
                }
            }
        }
        return response;
    }
}
