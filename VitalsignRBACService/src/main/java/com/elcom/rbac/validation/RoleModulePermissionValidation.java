/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.validation;

import com.elcom.rbac.exception.ValidationException;
import com.elcom.rbac.model.RolePathPermission;
import com.elcom.rbac.utils.StringUtil;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class RoleModulePermissionValidation extends AbstractValidation {

    public String validateUpsertPermission(RolePathPermission permission, String actionType) throws ValidationException {
        if (permission == null) {
            return "payLoad không hợp lệ";
        }

        if ("INSERT".equals(actionType) && (permission.getServiceCode() == null
                || StringUtil.isNullOrEmpty(permission.getServiceCode().getServiceCode()))) {
            getMessageDes().add("serviceCode không được để trống");
        }

        if (permission.getApiPath() == null || StringUtil.isNullOrEmpty(permission.getApiPath().getApiPath())) {
            getMessageDes().add("apiPath không được để trống");
        }

        if (permission.getRoleCode() == null || StringUtil.isNullOrEmpty(permission.getRoleCode().getRoleCode())) {
            getMessageDes().add("roleCode không được để trống");
        }
        
        if ("INSERT".equals(actionType) && permission.getCanCreate() == null) {
            getMessageDes().add("canCreate không được để trống");
        }
        
        if ("INSERT".equals(actionType) && permission.getCanRead() == null) {
            getMessageDes().add("canRead không được để trống");
        }
        
        if ("INSERT".equals(actionType) && permission.getCanUpdate() == null) {
            getMessageDes().add("canUpdate không được để trống");
        }
        
        if ("INSERT".equals(actionType) && permission.getCanDelete() == null) {
            getMessageDes().add("canDelete không được để trống");
        }

        /**/
        return !isValid() ? this.buildValidationMessage() : null;
    }
}
