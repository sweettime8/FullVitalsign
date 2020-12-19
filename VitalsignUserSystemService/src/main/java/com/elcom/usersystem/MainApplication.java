package com.elcom.usersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableCaching
//@EnableScheduling
public class MainApplication {
    
    public static void main(String[] args) {
        // Fix lỗi "UDP failed setting ip_ttl | Method not implemented" khi start app trên Windows
//        System.setProperty("java.net.preferIPv4Stack", "true");
        
        SpringApplication.run(MainApplication.class, args);
    }
}
