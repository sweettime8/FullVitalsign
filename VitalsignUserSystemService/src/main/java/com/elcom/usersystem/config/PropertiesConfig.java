package com.elcom.usersystem.config;

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
    public static boolean APP_MASTER;
    
    @Autowired
    public PropertiesConfig(@Value("${tech.env}") String techEnv
                          , @Value("${app.master}") boolean isAppMaster) {
        PropertiesConfig.TECH_ENV = techEnv;
        PropertiesConfig.APP_MASTER = isAppMaster;
    }
}
