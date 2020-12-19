/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.controller;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.rbac.constant.Constant;
import com.elcom.rbac.model.Service;
import com.elcom.rbac.model.dto.AuthorizationResponseDTO;
import com.elcom.rbac.utils.StringUtil;
import com.elcom.rbac.validation.ServiceValidation;
import java.util.Date;
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
public class ServiceController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    private com.elcom.rbac.service.Service service;

    @Autowired
    private ServiceValidation serviceValidation;

    //Create service
    public ResponseMessage createService(Map<String, Object> bodyParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            String serviceCode = (String) bodyParam.get("serviceCode");
            String serviceName = (String) bodyParam.get("serviceName");

            Service serviceObj = new Service();
            serviceObj.setServiceCode(serviceCode);
            serviceObj.setServiceName(serviceName);

            String invalidData = serviceValidation.validateUpsertService(serviceObj, "INSERT");
            if (invalidData != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                Service existService = service.findByServiceCode(serviceCode);
                if (existService != null) {
                    response = new ResponseMessage(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                            new MessageContent(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), "Service đã tồn tại"));
                } else {
                    serviceObj.setCreatedAt(new Date());
                    serviceObj.setIsDelete(0);
                    try {
                        //service.save(serviceObj);
                        response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.toString(),
                                new MessageContent(serviceObj));
                    } catch (Exception ex) {
                        response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.toString()));
                        LOGGER.error("Error to save service obj >>> " + ex.toString());
                        ex.printStackTrace();
                    }
                }
            }
        }
        
        // Tạm thời ko cho tạo qua API ==> Tạo tay
        response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null));
        return response;
    }

    //Update service
    public ResponseMessage updateService(Map<String, Object> bodyParam, Map<String, String> headerMap) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenToken(headerMap);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            System.out.println("JWT Token UUID: " + dto.getUuid());
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                String serviceCode = (String) bodyParam.get("serviceCode");
                String serviceName = (String) bodyParam.get("serviceName");

                Service serviceObj = new Service();
                serviceObj.setServiceCode(serviceCode);
                serviceObj.setServiceName(serviceName);

                String invalidData = serviceValidation.validateUpsertService(serviceObj, "UPDATE");
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    String errorMsg = authorUserService(dto.getUuid());
                    
                    if(!StringUtil.isNullOrEmpty(errorMsg)){
                        response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), errorMsg));
                    } else {
                        try {
                            if (service.update(serviceObj)) {
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
                            LOGGER.error("Error to update service obj >>> " + ex.toString());
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return response;
    }

    //Delete service
    public ResponseMessage deleteService(Map<String, Object> bodyParam) {
        ResponseMessage response = null;

        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            String serviceCode = (String) bodyParam.get("serviceCode");
            if (StringUtil.isNullOrEmpty(serviceCode)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                Service serviceObj = service.findByServiceCode(serviceCode);
                if (serviceObj == null) {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                            new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                } else {
                    //if (service.remove(serviceObj)) {
                    //    response = new ResponseMessage(new MessageContent(bodyParam));
                    //} else {
                    //    response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                    //            new MessageContent(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                    //                    bodyParam));
                    //}
                }
            }
        }
        // Tạm thời ko cho tạo qua API ==> Xóa tay
        response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null));
        return response;
    }
}
