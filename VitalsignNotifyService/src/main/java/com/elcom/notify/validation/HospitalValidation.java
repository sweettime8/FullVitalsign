package com.elcom.notify.validation;

import com.elcom.notify.utils.StringUtil;

public class HospitalValidation extends AbstractValidation {

    public String validateFindPatientByEmployee(String employeeId) {

        if ( StringUtil.isNullOrEmpty(employeeId) )
            getMessageDes().add("employeeId không được để trống");
        else if( !StringUtil.validUuid(employeeId) )
            getMessageDes().add("employeeId (UUID) chứa giá trị không hợp lệ: ["+employeeId.trim()+"]");
        
        return !isValid() ? this.buildValidationMessage() : null;
    }
}
