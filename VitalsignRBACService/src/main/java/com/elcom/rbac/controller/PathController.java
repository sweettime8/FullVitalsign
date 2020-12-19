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
import com.elcom.rbac.model.Service;
import com.elcom.rbac.model.dto.AuthorizationResponseDTO;
import com.elcom.rbac.service.PathService;
import com.elcom.rbac.utils.StringUtil;
import com.elcom.rbac.validation.PathValidation;
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
 * @author admin
 */
@Controller
public class PathController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathController.class);

    @Autowired
    private PathValidation pathValidation;

    @Autowired
    private com.elcom.rbac.service.Service service;

    @Autowired
    private PathService pathService;

    //Create One Path
    private ResponseMessage createOnePath(String serviceCode, String apiPath, AuthorizationResponseDTO dto,
            Path path, boolean inListPath) {
        ResponseMessage response = null;
        String invalidData = pathValidation.validateUpsertPath(path, "INSERT");
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
                    path.setServiceCode(existService);
                    Path existPath = pathService.findByServiceCodeAndApiPath(existService, apiPath);
                    if (existPath != null) {
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                new MessageContent(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), "Path đã tồn tại"));
                    } else {
                        path.setIsDelete(0);
                        path.setCreatedAt(new Date());
                        try {
                            pathService.save(path);

                            if (inListPath) {
                                response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                        new MessageContent("Thêm list path thành công"));
                            } else {
                                response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                        new MessageContent(path));
                            }
                        } catch (Exception ex) {
                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                    new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.toString()));
                            LOGGER.error("Error to save path >>> " + ex.toString());
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return response;
    }

    //Create Path
    public ResponseMessage createPath(Map<String, Object> bodyParam, Map<String, String> headerMap) {
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

                List<String> pathList = (List<String>) bodyParam.get("paths");
                if (pathList != null && !pathList.isEmpty()) {
                    Path path = null;
                    for (String tmpPath : pathList) {
                        path = new Path(serviceCode, tmpPath);
                        response = createOnePath(serviceCode, tmpPath, dto, path, true);
                    }
                } else {
                    Path path = new Path(serviceCode, apiPath);
                    response = createOnePath(serviceCode, apiPath, dto, path, false);
                }
            }
        }
        return response;
    }
    
    //Update Path
    public ResponseMessage updatePath(Map<String, Object> bodyParam, Map<String, String> headerMap) {
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
                String newPath = (String) bodyParam.get("newPath");
                Path path = new Path(serviceCode, apiPath);

                String invalidData = pathValidation.validateUpsertPath(path, "UPDATE");
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    Path existPath = pathService.findByServiceCodeAndApiPath(new Service(serviceCode), apiPath);
                    if (existPath == null || (existPath.getIsDelete() != null && existPath.getIsDelete() == 1)) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                        "Không tồn tại Path với serviceCode " + serviceCode + " và apiPath " + apiPath));
                    } else {
                        String errorMsg = authorUserService(dto.getUuid());

                        if (!StringUtil.isNullOrEmpty(errorMsg)) {
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                        } else {
                            try {
                                if (pathService.update(path, newPath)) {
                                    response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(),
                                            new MessageContent(bodyParam));
                                } else {
                                    response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                                            new MessageContent(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                                                    bodyParam));
                                }
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                                        new MessageContent(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                                                ex.toString()));
                                LOGGER.error("Error to update path >>> " + ex.toString());
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return response;
    }
    
    //Delete Path
    public ResponseMessage deletePath(String pathParam, Map<String, String> headerMap) {
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
                Integer id = new Integer(pathParam);
                if (id == null || id.equals(0)) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                    "Thiếu tham số id"));
                } else {
                    Path path = pathService.findById(id).get();
                    if (path == null) {
                        response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                        "Không tìm thấy Path ứng với id " + id));
                    } else {
                        String errorMsg = authorUserService(dto.getUuid());

                        if (!StringUtil.isNullOrEmpty(errorMsg)) {
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), 
                                            errorMsg + "ứng với id = " + id));
                        } else {
                            if (pathService.remove(path)) {
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

    //List Path
    public ResponseMessage getListPath(Map<String, String> headerMap, String urlParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerMap);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String serviceCode = params.get("serviceCode");

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
                    List<Path> pathList = pathService.findByServiceCode(serviceCode);
                    if (pathList != null && !pathList.isEmpty()) {
                        for (Path tmp : pathList) {
                            tmp.setRolePathPermissionList(null);
                        }
                    }
                    response = new ResponseMessage(new MessageContent(pathList));
                }
            }
        }
        return response;
    }
}
