/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.validation;

import com.elcom.rbac.exception.ValidationException;
import com.elcom.rbac.model.ModulePath;
import com.elcom.rbac.utils.StringUtil;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class ModulePathValidation extends AbstractValidation {

    public String validateUpsertModulePath(ModulePath modulePath, String actionType) throws ValidationException {
        if (modulePath == null) {
            return "payLoad không hợp lệ";
        }

        if ("INSERT".equals(actionType) && (modulePath.getServiceCode() == null
                || StringUtil.isNullOrEmpty(modulePath.getServiceCode().getServiceCode()))) {
            getMessageDes().add("serviceCode không được để trống");
        }

        if (modulePath.getModuleCode() == null || StringUtil.isNullOrEmpty(modulePath.getModuleCode().getModuleCode())) {
            getMessageDes().add("moduleCode không được để trống");
        }

        if (StringUtil.isNullOrEmpty(modulePath.getApiPath())) {
            getMessageDes().add("apiPath không được để trống");
        }

        /**/
        return !isValid() ? this.buildValidationMessage() : null;
    }

}
