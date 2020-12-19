/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.validation;

import com.elcom.rbac.exception.ValidationException;
import com.elcom.rbac.model.Path;
import com.elcom.rbac.utils.StringUtil;
import org.springframework.stereotype.Service;

/**
 *
 * @author admin
 */
@Service
public class PathValidation extends AbstractValidation {
    
    public String validateUpsertPath(Path path, String actionType) throws ValidationException {
        if (path == null) {
            return "payLoad không hợp lệ";
        }

        if ("INSERT".equals(actionType) && (path.getServiceCode() == null
                || StringUtil.isNullOrEmpty(path.getServiceCode().getServiceCode()))) {
            getMessageDes().add("serviceCode không được để trống");
        }

        if (StringUtil.isNullOrEmpty(path.getApiPath())) {
            getMessageDes().add("apiPath không được để trống");
        }

        /**/
        return !isValid() ? this.buildValidationMessage() : null;
    }
}
