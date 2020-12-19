/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.controller;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.rbac.constant.Constant;
import com.elcom.rbac.model.Role;
import com.elcom.rbac.model.RoleUser;
import com.elcom.rbac.model.dto.AuthorizationResponseDTO;
import com.elcom.rbac.service.RoleService;
import com.elcom.rbac.service.RoleUserService;
import com.elcom.rbac.utils.StringUtil;
import com.elcom.rbac.validation.RoleValidation;
import java.util.ArrayList;
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
public class RoleController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleValidation roleValidation;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleUserService roleUserService;

    private ResponseMessage createOneRole(String roleCode, AuthorizationResponseDTO dto,
            Role role, boolean inListRole) {
        ResponseMessage response = null;
        String invalidData = roleValidation.validateUpsertRole(role, "INSERT");
        if (invalidData != null) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
        } else {
            String errorMsg = authorUserService(dto.getUuid());
            if (!StringUtil.isNullOrEmpty(errorMsg)) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
            } else {
                Role existRole = roleService.findByRoleCode(roleCode);
                if (existRole != null) {
                    response = new ResponseMessage(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                            new MessageContent(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), "Role đã tồn tại"));
                } else {
                    role.setIsAdmin(0);
                    role.setIsDelete(0);
                    role.setCreatedAt(new Date());
                    try {
                        roleService.save(role);
                        if (inListRole) {
                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                    new MessageContent("Thêm list role thành công"));
                        } else {
                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                    new MessageContent(role));
                        }
                    } catch (Exception ex) {
                        response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.toString()));
                        LOGGER.error("Error to save role >>> " + ex.toString());
                        ex.printStackTrace();
                    }
                }
            }
        }
        return response;
    }

    //Create Role
    public ResponseMessage createRole(Map<String, Object> bodyParam, Map<String, String> headerMap) {
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
                String roleCode = (String) bodyParam.get("roleCode");
                String roleName = (String) bodyParam.get("roleName");
                List<Map<String, Object>> roleList = (List<Map<String, Object>>) bodyParam.get("roles");

                //Check insert one or list
                if (roleList != null && !roleList.isEmpty()) {
                    for (Map<String, Object> tmp : roleList) {
                        Role role = new Role((String) tmp.get("roleCode"), (String) tmp.get("roleName"));
                        response = createOneRole((String) tmp.get("roleCode"), dto, role, true);
                    }
                } else {
                    Role role = new Role(roleCode, roleName);
                    response = createOneRole(roleCode, dto, role, false);
                }
            }
        }
        return response;
    }

    //Update role
    public ResponseMessage updateRole(Map<String, Object> bodyParam, Map<String, String> headerMap) {
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
                String roleCode = (String) bodyParam.get("roleCode");
                String roleName = (String) bodyParam.get("roleName");
                Role role = new Role(roleCode, roleName);

                String invalidData = roleValidation.validateUpsertRole(role, "UPDATE");
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    //Role exist
                    Role existRole = roleService.findByRoleCode(roleCode);
                    if (existRole == null || (existRole.getIsDelete() != null && existRole.getIsDelete() == 1)) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                        "Không tồn tại Role với roleCode " + roleCode));
                    } else {
                        String errorMsg = authorUserService(dto.getUuid());

                        if (!StringUtil.isNullOrEmpty(errorMsg)) {
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                        } else {
                            try {
                                if (roleService.update(role)) {
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
                                LOGGER.error("Error to update role >>> " + ex.toString());
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    //Delete role
    public ResponseMessage deleteRole(String pathParam, Map<String, String> headerMap) {
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
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
                } else {
                    Role role = roleService.findById(id).get();
                    if (role == null) {
                        response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                        "Không tìm thấy Role ứng với id " + id));
                    } else {
                        String errorMsg = authorUserService(dto.getUuid());

                        if (!StringUtil.isNullOrEmpty(errorMsg)) {
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                        } else {
                            if (role.getIsAdmin() != null && role.getIsAdmin() == 1) {
                                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                                "Không thể xóa quyền ADMIN"));
                            } else {
                                if (roleService.remove(role)) {
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
        }
        return response;
    }

    //List Role
    public ResponseMessage getListRole(Map<String, String> headerMap, String urlParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerMap);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            String errorMsg = authorUserService(dto.getUuid());
            if (!StringUtil.isNullOrEmpty(errorMsg)) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
            } else {
                List<Role> roleList = roleService.findActive();
                if (roleList != null && !roleList.isEmpty()) {
                    for (Role tmp : roleList) {
                        tmp.setRoleModulePermissionList(null);
                        tmp.setRoleUserList(null);
                    }
                }
                response = new ResponseMessage(new MessageContent(roleList));
            }
        }
        return response;
    }

    //List User By Role
    public ResponseMessage getListUserByRole(Map<String, String> headerMap, String urlParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerMap);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String roleCode = params.get("roleCode");

            //Role
            Role role = roleService.findByRoleCode(roleCode);
            if (role == null || (role.getIsDelete() != null && role.getIsDelete() == 1)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                "Không tồn tại Role với roleCode " + roleCode));
            } else {
                String errorMsg = authorUserService(dto.getUuid());
                if (!StringUtil.isNullOrEmpty(errorMsg)) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                } else {
                    List<RoleUser> roleUserList = roleUserService.findByRoleCode(role);
                    if (roleUserList != null && !roleUserList.isEmpty()) {
                        List<String> uuidList = new ArrayList<>();
                        for (RoleUser tmp : roleUserList) {
                            tmp.getRoleCode().setRoleUserList(null);
                            tmp.getRoleCode().setRoleModulePermissionList(null);
                            if (!uuidList.contains(tmp.getUuidUser())) {
                                uuidList.add(tmp.getUuidUser());
                            }
                        }
                        //Call to User service get list user by list uuid
                        Map<String, AuthorizationResponseDTO> dtoMap = getUserMap(uuidList, headerMap);
                        for (RoleUser tmp : roleUserList) {
                            if (dtoMap != null && dtoMap.containsKey(tmp.getUuidUser())) {
                                AuthorizationResponseDTO tmpDto = dtoMap.get(tmp.getUuidUser());
                                tmp.setFullName(tmpDto.getFullName());
                                tmp.setEmail(tmpDto.getEmail());
                                tmp.setMobile(tmpDto.getMobile());
                                tmp.setAvatar(tmpDto.getAvatar());
                            }
                        }
                    }
                    response = new ResponseMessage(new MessageContent(roleUserList));
                }
            }
        }
        return response;
    }

}
