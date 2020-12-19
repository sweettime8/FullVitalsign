package com.elcom.device.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author anhdv
 */
@Component
public class PropertiesConfig {
    
    public static String TECH_ENV;
    public static boolean  APP_MASTER;
    public static String  RSA_PRIVATE_KEY;

    @Autowired
    public PropertiesConfig(@Value("${tech.env}") String techEnv
                          , @Value("${app.master}") boolean isAppMaster
                          , @Value("${rsa.private.key}") String rsaPrivateKey) {
        PropertiesConfig.TECH_ENV = techEnv;
        PropertiesConfig.APP_MASTER = isAppMaster;
        PropertiesConfig.RSA_PRIVATE_KEY = rsaPrivateKey;
    }
}
