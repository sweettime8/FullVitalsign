/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.notify.utils;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class GatewayDebugUtil {

    public static void debug(String requestPath, String urlParam, String pathParam,
            Map<String, Object> bodyParam, Map<String, String> headerParam) {
        System.out.println("requestPath: " + requestPath + ", urlParam: " + urlParam + ", pathParam: " + pathParam);
        if (bodyParam != null && !bodyParam.isEmpty()) {
            System.out.println("bodyParam");
            Iterator<Map.Entry<String, Object>> iterator = bodyParam.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                System.out.println(entry.getKey() + " => " + entry.getValue());
            }
        }
        if (headerParam != null && !headerParam.isEmpty()) {
            System.out.println("headerParam");
            Iterator<Map.Entry<String, String>> iterator = headerParam.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                System.out.println(entry.getKey() + " => " + entry.getValue());
            }
        }
    }
}
