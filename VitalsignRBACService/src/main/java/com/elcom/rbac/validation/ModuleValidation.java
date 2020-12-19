/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.validation;

import com.elcom.rbac.exception.ValidationException;
import com.elcom.rbac.model.Module;
import com.elcom.rbac.utils.StringUtil;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class ModuleValidation extends AbstractValidation{
    
    public String validateUpsertModule(Module module, String actionType) throws ValidationException {
        if (module == null) {
            return "payLoad không hợp lệ";
        }
        
        if ("INSERT".equals(actionType) && (module.getServiceCode() == null 
                || StringUtil.isNullOrEmpty(module.getServiceCode().getServiceCode()))) {
            getMessageDes().add("serviceCode không được để trống");
        }

        if (StringUtil.isNullOrEmpty(module.getModuleCode())) {
            getMessageDes().add("moduleCode không được để trống");
        }

        if ("INSERT".equals(actionType) && StringUtil.isNullOrEmpty(module.getModuleName())) {
            getMessageDes().add("moduleName không được để trống");
        }

        /**/
        return !isValid() ? this.buildValidationMessage() : null;
    }
    
}
