/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.validation;

import com.elcom.rbac.exception.ValidationException;
import com.elcom.rbac.model.Service;
import com.elcom.rbac.utils.StringUtil;

/**
 *
 * @author Admin
 */
@org.springframework.stereotype.Service
public class ServiceValidation extends AbstractValidation {

    public String validateUpsertService(Service service, String actionType) throws ValidationException {
        if (service == null) {
            return "payLoad không hợp lệ";
        }

        if (StringUtil.isNullOrEmpty(service.getServiceCode())) {
            getMessageDes().add("serviceCode không được để trống");
        }

        if ("INSERT".equals(actionType) && StringUtil.isNullOrEmpty(service.getServiceName())) {
            getMessageDes().add("serviceName không được để trống");
        }

        /**/
        return !isValid() ? this.buildValidationMessage() : null;
    }
}
