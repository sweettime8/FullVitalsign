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
import com.elcom.rbac.model.dto.RoleUserPagingDTO;
import com.elcom.rbac.service.RoleService;
import com.elcom.rbac.service.RoleUserService;
import com.elcom.rbac.utils.StringUtil;
import com.elcom.rbac.validation.RoleUserValidation;
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
public class RoleUserController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleUserController.class);

    @Autowired
    private RoleUserValidation roleUserValidation;

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private RoleService roleService;

    //Create One ModulePath
    private ResponseMessage createOneRoleUser(String roleCode, String uuidUser,
            AuthorizationResponseDTO dto, RoleUser roleUser, boolean inListRoleUser) {
        ResponseMessage response = null;
        String invalidData = roleUserValidation.validateUpsertRoleUser(roleUser, "INSERT");
        if (invalidData != null) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
        } else {
            String errorMsg = authorUserService(dto.getUuid());
            if (!StringUtil.isNullOrEmpty(errorMsg)) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
            } else {
                //Role
                Role role = roleService.findByRoleCode(roleCode);
                if (role == null || (role.getIsDelete() != null && role.getIsDelete() == 1)) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                    "Không tồn tại Role với roleCode " + roleCode));
                } else {
                    role.setRoleUserList(null);
                    role.setRoleModulePermissionList(null);
                    roleUser.setRoleCode(role);

                    RoleUser existRoleUser = roleUserService.findByUuidUserAndRoleCode(uuidUser, role);
                    if (existRoleUser != null && existRoleUser.getIsDelete() == 0) {
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                new MessageContent(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                        "RoleUser đã tồn tại với uuid " + uuidUser + " và role " + role.getRoleCode()));
                    } else {
                        if (existRoleUser != null && existRoleUser.getIsDelete() == 1) {
                            existRoleUser.setIsDelete(0);
                            try {
                                roleUserService.save(existRoleUser);
                                if (inListRoleUser) {
                                    response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                            new MessageContent("Thêm list role user thành công"));
                                } else {
                                    response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                            new MessageContent(roleUser));
                                }
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.toString()));
                                LOGGER.error("Error to save RoleUser >>> " + ex.toString());
                                ex.printStackTrace();
                            }
                        } else {
                            roleUser.setIsDelete(0);
                            roleUser.setCreatedAt(new Date());
                            try {
                                roleUserService.save(roleUser);
                                if (inListRoleUser) {
                                    response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                            new MessageContent("Thêm list role user thành công"));
                                } else {
                                    response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                            new MessageContent(roleUser));
                                }
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.toString()));
                                LOGGER.error("Error to save RoleUser >>> " + ex.toString());
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    //Create RoleUser
    public ResponseMessage createRoleUser(Map<String, Object> bodyParam, Map<String, String> headerMap) {
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
                String uuidUser = (String) bodyParam.get("uuidUser");
                List<String> userList = (List<String>) bodyParam.get("users");

                if (userList != null && !userList.isEmpty()) {
                    RoleUser roleUser = null;
                    for (String tmpUuid : userList) {
                        roleUser = new RoleUser(tmpUuid, roleCode);
                        response = createOneRoleUser(roleCode, tmpUuid, dto, roleUser, true);
                    }
                } else {
                    RoleUser roleUser = new RoleUser(uuidUser, roleCode);
                    response = createOneRoleUser(roleCode, uuidUser, dto, roleUser, false);
                }
            }
        }
        return response;
    }

    //Update RoleUser
    public ResponseMessage updateRoleUser(Map<String, Object> bodyParam, Map<String, String> headerMap) {
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
                String uuidUser = (String) bodyParam.get("uuidUser");
                String newRole = (String) bodyParam.get("newRole");
                RoleUser roleUser = new RoleUser(uuidUser, roleCode);

                String invalidData = roleUserValidation.validateUpsertRoleUser(roleUser, "UPDATE");
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    //Old Role
                    Role oldRoleObj = roleService.findByRoleCode(roleCode);
                    if (oldRoleObj == null || (oldRoleObj.getIsDelete() != null && oldRoleObj.getIsDelete() == 1)) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                        "Không tồn tại Role với roleCode " + roleCode));
                    } else {
                        String errorMsg = authorUserService(dto.getUuid());
                        if (!StringUtil.isNullOrEmpty(errorMsg)) {
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                        } else {
                            //New Role
                            Role newRoleObj = roleService.findByRoleCode(newRole);
                            if (newRoleObj == null || (newRoleObj.getIsDelete() != null && newRoleObj.getIsDelete() == 1)) {
                                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                                "Không tồn tại Role với newRole " + newRole));
                            } else {
                                errorMsg = authorUserService(dto.getUuid());

                                if (!StringUtil.isNullOrEmpty(errorMsg)) {
                                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                                } else {
                                    try {
                                        if (roleUserService.update(roleUser, newRole)) {
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
                                        LOGGER.error("Error to update role user >>> " + ex.toString());
                                        ex.printStackTrace();
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

    //Delete RoleUser
    public ResponseMessage deleteRoleUser(String pathParam, Map<String, String> headerMap) {
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
                    //Role User 
                    RoleUser roleUser = roleUserService.findById(id).get();
                    if (roleUser == null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                        "Không tồn tại RoleUser với id " + id));
                    } else {
                        String errorMsg = authorUserService(dto.getUuid());
                        if (!StringUtil.isNullOrEmpty(errorMsg)) {
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                        } else {
                            if (roleUserService.remove(roleUser)) {
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

    //Create Default User Role
    public ResponseMessage createDefaultRole(Map<String, Object> bodyParam, Map<String, String> headerMap) {
        ResponseMessage response = null;

        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            //String roleCode = (String) bodyParam.get("roleCode");
            String roleCode = Constant.DEFAULT_ROLE;
            String uuidUser = (String) bodyParam.get("uuidUser");
            RoleUser roleUser = new RoleUser(uuidUser, roleCode);

            String invalidData = roleUserValidation.validateUpsertRoleUser(roleUser, "INSERT");
            if (invalidData != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                //Role
                Role role = roleService.findByRoleCode(roleCode);
                if (role == null || (role.getIsDelete() != null && role.getIsDelete() == 1)) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                    "Không tồn tại Role với roleCode " + roleCode));
                } else {
                    role.setRoleUserList(null);
                    role.setRoleModulePermissionList(null);
                    roleUser.setRoleCode(role);

                    RoleUser existRoleUser = roleUserService.findByUuidUserAndRoleCode(uuidUser, role);
                    if (existRoleUser != null) {
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                new MessageContent(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                        "RoleUser đã tồn tại với uuid " + uuidUser + " và role " + role.getRoleCode()));
                    } else {
                        roleUser.setIsDelete(0);
                        roleUser.setCreatedAt(new Date());
                        try {
                            roleUserService.save(roleUser);
                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                    new MessageContent(roleUser));
                        } catch (Exception ex) {
                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                    new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.toString()));
                            LOGGER.error("Error to save Default User Role >>> " + ex.toString());
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return response;
    }

    //Find RoleUser
    public ResponseMessage findAdmin(String urlParam, Map<String, String> headerMap) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerMap);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            boolean rbacStatus = authorizeRBAC("GET", dto.getUuid(), "/v1.0/rbac/admin");
            if (rbacStatus) {
                if (urlParam == null || urlParam.isEmpty()) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
                } else {
                    Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                    int currentPage = Integer.parseInt(params.get("currentPage"));
                    int rowsPerPage = Integer.parseInt(params.get("rowsPerPage"));
                    String sort = params.get("sort");
                    String roleCode = params.get("roleCode");
                    String keyword = params.get("keyword");

                    if (currentPage == 0 || rowsPerPage == 0) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                        Constant.VALIDATION_INVALID_PARAM_VALUE));
                    } else {
                        if (!StringUtil.isNullOrEmpty(sort) && !"id".equalsIgnoreCase(sort)
                                && !"uuidUser".equalsIgnoreCase(sort) && !"roleCode".equalsIgnoreCase(sort)
                                && !"createdAt".equalsIgnoreCase(sort)) {
                            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                            "Không có kiểu sort theo " + sort));
                        } else {
                            RoleUserPagingDTO roleUserPagingDTO = roleUserService.findAdmin(roleCode, currentPage, rowsPerPage, sort);
                            if (roleUserPagingDTO == null || roleUserPagingDTO.getDataRows() == null || roleUserPagingDTO.getDataRows().isEmpty()) {
                                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                        new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                                Constant.VALIDATION_DATA_NOT_FOUND));
                            } else {
                                List<RoleUser> roleUserList = roleUserPagingDTO.getDataRows();
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
                                    if (!StringUtil.isNullOrEmpty(keyword)) {
                                        keyword = keyword.trim().toLowerCase();
                                    }
                                    List<RoleUser> searchList = new ArrayList<>();
                                    for (RoleUser tmp : roleUserList) {
                                        if (dtoMap != null && dtoMap.containsKey(tmp.getUuidUser())) {
                                            AuthorizationResponseDTO tmpDto = dtoMap.get(tmp.getUuidUser());
                                            tmp.setFullName(tmpDto.getFullName());
                                            tmp.setEmail(tmpDto.getEmail());
                                            tmp.setMobile(tmpDto.getMobile());
                                            tmp.setAvatar(tmpDto.getAvatar());

                                            if (!StringUtil.isNullOrEmpty(keyword) && ((tmpDto.getFullName() != null && tmpDto.getFullName().contains(keyword))
                                                    || (tmpDto.getEmail() != null && tmpDto.getEmail().contains(keyword))
                                                    || (tmpDto.getMobile() != null && tmpDto.getMobile().contains(keyword)))) {
                                                tmp.setInSearch(1);
                                                searchList.add(tmp);
                                            }
                                        }
                                    }
                                    if (!StringUtil.isNullOrEmpty(keyword)) {
                                        response = new ResponseMessage(new MessageContent(searchList, new Long(searchList.size())));
                                    } else {
                                        response = new ResponseMessage(new MessageContent(roleUserList, roleUserPagingDTO.getTotalRows()));
                                    }
                                } else {
                                    response = new ResponseMessage(new MessageContent(roleUserList, roleUserPagingDTO.getTotalRows()));
                                }
                            }
                        }
                    }
                }
            } else {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn không có quyền quản lý danh sách admin"));
            }
        }
        return response;
    }
    
    //Find list role by user
    public ResponseMessage findRoleByUser(String pathParam, Map<String, String> headerMap) {
        ResponseMessage response = null;

        if (pathParam == null || pathParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            List<RoleUser> roleUserList = roleUserService.findByUuidUser(pathParam);
            response = new ResponseMessage(new MessageContent(roleUserList));
        }
        return response;
    }
}
