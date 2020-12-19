/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.validation;

import com.elcom.rbac.exception.ValidationException;
import com.elcom.rbac.model.Role;
import com.elcom.rbac.utils.StringUtil;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class RoleValidation extends AbstractValidation{
    
    public String validateUpsertRole(Role role, String actionType) throws ValidationException {
        if (role == null) {
            return "payLoad không hợp lệ";
        }
        
        if (StringUtil.isNullOrEmpty(role.getRoleCode())) {
            getMessageDes().add("roleCode không được để trống");
        }

        if ("INSERT".equals(actionType) && StringUtil.isNullOrEmpty(role.getRoleName())) {
            getMessageDes().add("roleName không được để trống");
        }

        /**/
        return !isValid() ? this.buildValidationMessage() : null;
    }
}
