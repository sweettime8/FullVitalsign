/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.id.messaging.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class RabbitMQProperties {

    //RBAC service
    @Value("${rbac.rpc.exchange}")
    public static String RBAC_RPC_EXCHANGE;

    @Value("${rbac.rpc.queue}")
    public static String RBAC_RPC_QUEUE;

    @Value("${rbac.rpc.key}")
    public static String RBAC_RPC_KEY;
    
    @Value("${rbac.rpc.default.role.url}")
    public static String RBAC_RPC_DEFAULT_ROLE_URL;
    
    @Value("${rbac.rpc.author.url}")
    public static String RBAC_RPC_AUTHOR_URL;
    
    //SMS service
    @Value("${sms.rpc.exchange}")
    public static String SMS_RPC_EXCHANGE;

    @Value("${sms.rpc.queue}")
    public static String SMS_RPC_QUEUE;

    @Value("${sms.rpc.key}")
    public static String SMS_RPC_KEY;
    
    @Value("${sms.rpc.url}")
    public static String SMS_RPC_URL;
    
    /** Device */
    @Value("${vs.device.rpc.exchange}")
    public static String VS_DEVICE_RPC_EXCHANGE;
    
    @Value("${vs.device.rpc.queue}")
    public static String VS_DEVICE_RPC_QUEUE_NAME;
    
    @Value("${vs.device.rpc.key}")
    public static String VS_DEVICE_RPC_KEY;
    
    @Value("${vs.device.worker.queue}")
    public static String VS_DEVICE_WORK_QUEUE_NAME;
    /** ------------------ */

    @Autowired
    public RabbitMQProperties(@Value("${rbac.rpc.exchange}") String rbacRpcExchange,
            @Value("${rbac.rpc.queue}") String rbacRpcQueue,
            @Value("${rbac.rpc.key}") String rbacRpcKey,
            @Value("${rbac.rpc.default.role.url}") String rbacRpcDefaultRoleUrl,
            @Value("${rbac.rpc.author.url}") String rbacRpcAuthorUrl,
            @Value("${sms.rpc.exchange}") String smsRpcExchange,
            @Value("${sms.rpc.queue}") String smsRpcQueue,
            @Value("${sms.rpc.key}") String smsRpcKey,
            @Value("${sms.rpc.url}") String smsRpcUrl,
            @Value("${vs.device.rpc.exchange}") String deviceRpcExchange,
            @Value("${vs.device.rpc.queue}") String deviceRpcQueue,
            @Value("${vs.device.rpc.key}") String deviceRpcKey,
            @Value("${vs.device.worker.queue}") String deviceWorkerQueue) {
        //RBAC
        RBAC_RPC_EXCHANGE = rbacRpcExchange;
        RBAC_RPC_QUEUE = rbacRpcQueue;
        RBAC_RPC_KEY = rbacRpcKey;
        RBAC_RPC_DEFAULT_ROLE_URL = rbacRpcDefaultRoleUrl;
        RBAC_RPC_AUTHOR_URL = rbacRpcAuthorUrl;
        
        //SMS
        SMS_RPC_EXCHANGE = smsRpcExchange;
        SMS_RPC_QUEUE = smsRpcQueue;
        SMS_RPC_KEY = smsRpcKey;
        SMS_RPC_URL = smsRpcUrl;
        
        //Device
        VS_DEVICE_RPC_EXCHANGE = deviceRpcExchange;
        VS_DEVICE_RPC_QUEUE_NAME = deviceRpcQueue;
        VS_DEVICE_RPC_KEY = deviceRpcKey;
        VS_DEVICE_WORK_QUEUE_NAME = deviceWorkerQueue;
    }
}
