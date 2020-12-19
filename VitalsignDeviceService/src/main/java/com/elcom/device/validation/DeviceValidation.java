package com.elcom.device.validation;

import com.elcom.device.utils.StringUtil;

public class DeviceValidation extends AbstractValidation {

    public String validateFindDeviceInfoByPatient(String patientIdLst) {

        if ( StringUtil.isNullOrEmpty(patientIdLst) )
            getMessageDes().add("patientIdLst không được để trống");
        else if ( patientIdLst.contains(",") ) {
            String[] patients = patientIdLst.split(",");
            for( String item : patients ) {
                if( !StringUtil.validUuid(item) ) {
                    getMessageDes().add("patientId (UUID) không hợp lệ: ["+item+"]");
                    break;
                }
            }
        }
        else if( !StringUtil.validUuid(patientIdLst) )
            getMessageDes().add("patientId (UUID) không hợp lệ: ["+patientIdLst+"]");
        
        return !isValid() ? this.buildValidationMessage() : null;
    }
    
    public String validateUpdateNewHealthProfileForGate(String homeAdminId, String currentHealthProfileId, String newHealthProfileId, String gateId) {
        
        if ( StringUtil.isNullOrEmpty(homeAdminId) )
            getMessageDes().add("homeAdminId không được trống");
        else if( !StringUtil.validUuid(homeAdminId) )
            getMessageDes().add("homeAdminId không hợp lệ");
        
        if ( StringUtil.isNullOrEmpty(currentHealthProfileId) )
            getMessageDes().add("currentHealthProfileId không được trống");
        else if( !StringUtil.validUuid(currentHealthProfileId) )
            getMessageDes().add("currentHealthProfileId không hợp lệ");
        
        if ( StringUtil.isNullOrEmpty(newHealthProfileId) )
            getMessageDes().add("newHealthProfileId không được trống");
        else if( !StringUtil.validUuid(newHealthProfileId) )
            getMessageDes().add("newHealthProfileId không hợp lệ");
        
        if ( StringUtil.isNullOrEmpty(gateId) )
            getMessageDes().add("gateId không được trống");
        
        return !isValid() ? this.buildValidationMessage() : null;
    }
}
