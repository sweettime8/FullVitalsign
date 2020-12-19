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
import com.elcom.rbac.model.Service;
import com.elcom.rbac.model.dto.AuthorizationResponseDTO;
import com.elcom.rbac.service.PathService;
import com.elcom.rbac.service.RoleModulePermissionService;
import com.elcom.rbac.service.RoleService;
import com.elcom.rbac.utils.StringUtil;
import com.elcom.rbac.validation.RoleModulePermissionValidation;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Admin
 */
@Controller
public class RoleModulePermissionController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleModulePermissionController.class);

    @Autowired
    private RoleModulePermissionValidation permissionValidation;

    @Autowired
    private com.elcom.rbac.service.Service service;

    @Autowired
    private PathService pathService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleModulePermissionService permissionService;

    //Create RoleModulePermission
    public ResponseMessage createPermission(Map<String, Object> bodyParam, Map<String, String> headerMap) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerMap);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                String serviceCode = (String) bodyParam.get("serviceCode");
                String apiPath = (String) bodyParam.get("apiPath");
                String roleCode = (String) bodyParam.get("roleCode");
                Integer canCreate = (Integer) bodyParam.get("canCreate");
                Integer canRead = (Integer) bodyParam.get("canRead");
                Integer canUpdate = (Integer) bodyParam.get("canUpdate");
                Integer canDelete = (Integer) bodyParam.get("canDelete");

                RolePathPermission permission = new RolePathPermission(serviceCode, apiPath, roleCode);
                permission.setCanCreat(canCreate);
                permission.setCanRead(canRead);
                permission.setCanUpdate(canUpdate);
                permission.setCanDelete(canDelete);

                String invalidData = permissionValidation.validateUpsertPermission(permission, "INSERT");
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    //Service
                    Service existService = service.findByServiceCode(serviceCode);
                    if (existService == null || (existService.getIsDelete() != null && existService.getIsDelete() == 1)) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                        "Không tồn tại service với serviceCode " + serviceCode));
                    } else {
                        String errorMsg = authorUserService(dto.getUuid());
                        if (!StringUtil.isNullOrEmpty(errorMsg)) {
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                        } else {
                            existService.setRoleModulePermissionList(null);
                            permission.setServiceCode(existService);
                            //Path
                            Path path = pathService.findByServiceCodeAndApiPath(existService, apiPath);
                            if (path == null || (path.getIsDelete() != null && path.getIsDelete() == 1)) {
                                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                                "Không tồn tại module với path " + apiPath));
                            } else {
                                path.setRolePathPermissionList(null);
                                permission.setApiPath(path);
                                //Role
                                Role role = roleService.findByRoleCode(roleCode);
                                if (role == null || (role.getIsDelete() != null && role.getIsDelete() == 1)) {
                                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                                    "Không tồn tại Role với roleCode " + roleCode));
                                } else {
                                    role.setRoleModulePermissionList(null);
                                    permission.setRoleCode(role);

                                    RolePathPermission existPermission = permissionService.findByRoleAndPath(role, path);
                                    if (existPermission != null) {
                                        if (existPermission.getIsDelete() != null && existPermission.getIsDelete() == 1) {
                                            existPermission.setIsDelete(0);
                                            existPermission.setCanCreat(canCreate);
                                            existPermission.setCanRead(canRead);
                                            existPermission.setCanUpdate(canUpdate);
                                            existPermission.setCanDelete(canDelete);
                                            try {
                                                permissionService.save(existPermission);
                                                existPermission.getApiPath().setRolePathPermissionList(null);
                                                existPermission.getRoleCode().setRoleUserList(null);
                                                existPermission.getRoleCode().setRoleModulePermissionList(null);
                                                response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                                        new MessageContent(existPermission));
                                            } catch (Exception ex) {
                                                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.toString()));
                                                LOGGER.error("Error to save role path permission >>> " + ex.toString());
                                                ex.printStackTrace();
                                            }
                                        } else {
                                            response = new ResponseMessage(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                                    new MessageContent(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                                            "RoleModulePermission đã tồn tại"));
                                        }
                                    } else {
                                        permission.setIsDelete(0);
                                        permission.setCreatedAt(new Date());
                                        try {
                                            permissionService.save(permission);
                                            permission.getApiPath().setRolePathPermissionList(null);
                                            permission.getRoleCode().setRoleUserList(null);
                                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                                    new MessageContent(permission));
                                        } catch (Exception ex) {
                                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                                    new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.toString()));
                                            LOGGER.error("Error to save role path permission >>> " + ex.toString());
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    //Update RoleModulePermission
    public ResponseMessage updatePermission(Map<String, Object> bodyParam, Map<String, String> headerMap) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerMap);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                String serviceCode = (String) bodyParam.get("serviceCode");
                String apiPath = (String) bodyParam.get("apiPath");
                String roleCode = (String) bodyParam.get("roleCode");
                Integer canCreate = (Integer) bodyParam.get("canCreate");
                Integer canRead = (Integer) bodyParam.get("canRead");
                Integer canUpdate = (Integer) bodyParam.get("canUpdate");
                Integer canDelete = (Integer) bodyParam.get("canDelete");

                RolePathPermission permission = new RolePathPermission(serviceCode, apiPath, roleCode);
                permission.setCanCreat(canCreate);
                permission.setCanRead(canRead);
                permission.setCanUpdate(canUpdate);
                permission.setCanDelete(canDelete);

                String invalidData = permissionValidation.validateUpsertPermission(permission, "UPDATE");
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    RolePathPermission existPermission = permissionService.findByRoleAndPath(new Role(roleCode), new Path(serviceCode, apiPath));
                    if (existPermission == null || (existPermission.getIsDelete() != null && existPermission.getIsDelete() == 1)) {
                        response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                        "Không tìm thấy RoleModulePermission ứng với " + apiPath + " và " + roleCode));
                    } else {
                        String errorMsg = authorUserService(dto.getUuid());

                        if (!StringUtil.isNullOrEmpty(errorMsg)) {
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                        } else {
                            try {
                                if (permissionService.update(permission, canCreate, canRead, canUpdate, canDelete)) {
                                    response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(),
                                            new MessageContent(bodyParam));
                                } else {
                                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                                            new MessageContent(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                                                    bodyParam));
                                }
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                                        new MessageContent(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                                                ex.toString()));
                                LOGGER.error("Error to update role path permission >>> " + ex.toString());
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    //Delete RoleModulePermission
    public ResponseMessage deletePermission(String pathParam, Map<String, String> headerMap) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerMap);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            if (pathParam == null || pathParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                Long id = new Long(pathParam);

                if (id == null || id.equals(0L)) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                    "Thiếu tham số id"));
                } else {
                    RolePathPermission permission = permissionService.findById(id).get();
                    if (permission == null || (permission.getIsDelete() != null && permission.getIsDelete() == 1)) {
                        response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                        "Không tìm thấy RoleModulePermission ứng với id " + id));
                    } else {
                        String errorMsg = authorUserService(dto.getUuid());

                        if (!StringUtil.isNullOrEmpty(errorMsg)) {
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                        } else {
                            if (permissionService.remove(permission)) {
                                response = new ResponseMessage(new MessageContent(id));
                            } else {
                                response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                                        new MessageContent(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                                                id));
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    //List Role Path
    public ResponseMessage getListPermission(Map<String, String> headerMap, String urlParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerMap);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String serviceCode = params.get("serviceCode");
            String roleCode = params.get("roleCode");
            String apiPath = params.get("apiPath");

            //Service
            Service existService = service.findByServiceCode(serviceCode);
            if (existService == null || (existService.getIsDelete() != null && existService.getIsDelete() == 1)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                "Không tồn tại service với serviceCode " + serviceCode));
            } else {
                String errorMsg = authorUserService(dto.getUuid());
                if (!StringUtil.isNullOrEmpty(errorMsg)) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                } else {
                    List<RolePathPermission> permissionList = permissionService.findByServiceAndRoleAndPath(serviceCode, roleCode, apiPath);
                    if (permissionList != null && !permissionList.isEmpty()) {
                        for (RolePathPermission tmp : permissionList) {
                            tmp.getApiPath().setRolePathPermissionList(null);
                            tmp.getApiPath().setServiceCode(null);
                            tmp.getRoleCode().setRoleModulePermissionList(null);
                            tmp.getRoleCode().setRoleUserList(null);
                        }
                    }
                    response = new ResponseMessage(new MessageContent(permissionList));
                }
            }
        }
        return response;
    }
}
