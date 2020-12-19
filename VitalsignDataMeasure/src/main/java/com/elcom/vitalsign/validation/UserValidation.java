package com.elcom.vitalsign.validation;

import com.elcom.vitalsign.exception.ValidationException;
import com.elcom.vitalsign.model.Account;
import com.elcom.vitalsign.utils.StringUtil;

public class UserValidation extends AbstractValidation {

    public void validateUpsertUser(Account user, String actionType) throws ValidationException {

        if (user == null) {
            getMessageDes().add("payLoad không hợp lệ");
            throw new ValidationException(this.buildValidationMessage());
        }
        
        if( "INSERT".equals(actionType) && StringUtil.isNullOrEmpty(user.getUsername()) )
            getMessageDes().add("userName không được để trống");
        
        if( StringUtil.isNullOrEmpty(user.getPassword()) )
            getMessageDes().add("password không được để trống");
        
        if( StringUtil.isNullOrEmpty(user.getFullName()) )
            getMessageDes().add("fullName không được để trống");

        /**/
        if (!isValid())
            throw new ValidationException(this.buildValidationMessage());
    }
    
    public void validateLogin(String userName, String password) throws ValidationException {

        if( StringUtil.isNullOrEmpty(userName) )
            getMessageDes().add("userName không được để trống");
        
        if( StringUtil.isNullOrEmpty(password) )
            getMessageDes().add("password không được để trống");

        /**/
        if (!isValid())
            throw new ValidationException(this.buildValidationMessage());
    }
}
