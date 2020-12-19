/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.id.sms;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class MontySmsConfig {

    //SMS service
    @Value("${sms.api.service}")
    public static String SMS_API_SERVICE;

    @Value("${sms.api.url}")
    public static String SMS_API_URL;

    @Value("${sms.api.private.key.file}")
    public static String SMS_RSA_PRIVATE_KEY;

    @Autowired
    public MontySmsConfig(@Value("${sms.api.service}") String smsApiService,
            @Value("${sms.api.url}") String smsApiUrl,
            @Value("${sms.api.private.key.file}") String smsRsaPrivateKeyFile) {
        SMS_API_SERVICE = smsApiService;
        SMS_API_URL = smsApiUrl;
        SMS_RSA_PRIVATE_KEY = readfile(smsRsaPrivateKeyFile);
    }

    private static String readfile(String path) {
        String result = "";
        try {
            try (FileInputStream fstream = new FileInputStream(path);
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    result += strLine + " ";
                }
                result = result.trim();
                result = result.replace(" ", "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
