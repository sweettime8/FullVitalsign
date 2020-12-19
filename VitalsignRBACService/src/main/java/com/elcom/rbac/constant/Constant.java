package com.elcom.rbac.constant;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class Constant {
    
    // Validation message
    public static final String VALIDATION_INVALID_PARAM_VALUE = "Invalid param value";
    public static final String VALIDATION_DATA_NOT_FOUND = "Data not found";
    
    // Redis constant
    public static final String REDIS_ROLE_USER_KEY = "VITALSIGN_ROLE_USER";
    public static final String REDIS_MODULE_PATH_KEY = "VITALSIGN_MODULE_PATH";
    public static final String REDIS_SERVICE_PATH_KEY = "VITALSIGN_SERVICE_PATH";
    public static final String REDIS_ROLE_MODULE_PERMISSION_KEY = "VITALSIGN_ROLE_MODULE_PERMISSION";
    
    // Default Role
    public static final String DEFAULT_ROLE = "DOCTOR";
    
    // Fix ADMIN service
    // USER_SERVICE ==> USER_ADMIN, STORE_SERVICE ==> STORE_ADMIN,...
    public static final Map<String, String> SERVICE_ADMIN_MAP = new HashMap<>();
}
