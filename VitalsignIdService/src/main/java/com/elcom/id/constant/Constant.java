/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.id.constant;

/**
 *
 * @author Admin
 */
public class Constant {

    public static final String SRV_VER = "/v1.0";
    
    public static final int USER_SIGNUP_NORMAL = 0;
    public static final int USER_SIGNUP_FACEBOOK = 1;
    public static final int USER_SIGNUP_GOOGLE = 2;
    public static final int USER_SIGNUP_APPLE = 3;

    // Validation message
    public static final String VALIDATION_INVALID_PARAM_VALUE = "Invalid param value";
    public static final String VALIDATION_DATA_NOT_FOUND = "Data not found";
    public static final String VALIDATION_ACCOUNT_LOCKED = "Tài khoản đang bị khóa";
    
    // Response messages
    public static final String RESPONSE_UNKNOW_ERR = "Lỗi không xác định";

    //Mail server
    public static final String MAIL_SEND_ACC = "hoangyen918588@gmail.com";
    public static final String MAIL_SEND_PW = "khokhanquanhi123";
    public static final String MAIL_PORT = "465";
    public static final String MAIL_HOST = "smtp.gmail.com";

    //Forgot password
    public static final String MAIL_FORGOT_PW_SEND_FROM = "CoLearn support";
    public static final String MAIL_FORGOT_PW_TITLE = "Qu\u00EAn m\u1EADt kh\u1EA9u t\u00E0i kho\u1EA3n %s";
    public static final String MAIL_FORGOT_PW_CONTENT = "Xin ch\u00E0o %s,"
            + "<br />\u0110\u00E2y l\u00E0 m\u1EADt kh\u1EA9u c\u1EE7a b\u1EA1n: <b>%s</b>"
            + "<br />Xin c\u1EA3m \u01A1n!";
    public static final String MAIL_FORGOT_PW_CONTENT_LINK = "Xin ch\u00E0o %s,"
            + "<br />Link \u0111\u1EB7t l\u1EA1i m\u1EADt kh\u1EA9u c\u1EE7a b\u1EA1n l\u00E0 : <a href='%s'>%s</a>"
            + "<br />Link t\u1ED3n t\u1EA1i trong v\u00F2ng <b>%s ph\u00FAt</b>."
            + "<br />Xin c\u1EA3m \u01A1n!";

    // Score action code
    public static final String SCORE_REG_ACCOUNT = "CL_LOGGED_1ST";
    public static final String SCORE_DAILY_LOGIN = "CL_LOGGED_DAILY";
    public static final String SCORE_INFO_COMPLETE = "CL_INFO_COMPLETED";
    public static final String SCORE_UPDATE_AVATAR = "CL_FILL_AVATAR";
    
    // User Log Type map
    public static final String USER_LOG_TYPE_LOGIN = "LOGIN";
    public static final String USER_LOG_TYPE_REGISTER = "REGISTER";
    
    // OTP time expired
    public static final long OTP_TIME_EXIPRED = 15 * 60 * 1000;//15ph
    public static final String LEANR_OTP_KEY = "LEARN_OTP_KEY";
    public static final int MAX_SMS_PER_EXPIRED = 5;//Trong 1 ngưỡng Expired cho 3 lần gửi SMS
    
    // AES Key
    public static final String AES_KEY = "Elc0m2020@321456";
    
    public static final String API_DEVICE_DECRYPT_DEVICE_TOKEN  = "/device/decrypt-device-token";
}
