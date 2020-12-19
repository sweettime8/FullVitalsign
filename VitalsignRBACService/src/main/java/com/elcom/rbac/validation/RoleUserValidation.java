/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.validation;

import com.elcom.rbac.exception.ValidationException;
import com.elcom.rbac.model.RoleUser;
import com.elcom.rbac.utils.StringUtil;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class RoleUserValidation extends AbstractValidation{
    
    public String validateUpsertRoleUser(RoleUser roleUser, String actionType) throws ValidationException {
        if (roleUser == null) {
            return "payLoad không hợp lệ";
        }
        
        if ("INSERT".equals(actionType) && (roleUser.getRoleCode() == null 
                || StringUtil.isNullOrEmpty(roleUser.getRoleCode().getRoleCode()))) {
            getMessageDes().add("roleCode không được để trống");
        }

        if (StringUtil.isNullOrEmpty(roleUser.getUuidUser())) {
            getMessageDes().add("uuidUser không được để trống");
        }
        
        /**/
        return !isValid() ? this.buildValidationMessage() : null;
    }
}
