package com.elcom.device.messaging.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class RabbitMQProperties {

    /** User & RBAC */
    @Value("${user.rpc.exchange}")
    public static String USER_RPC_EXCHANGE;

    @Value("${user.rpc.queue}")
    public static String USER_RPC_QUEUE;

    @Value("${user.rpc.key}")
    public static String USER_RPC_KEY;

    @Value("${user.rpc.authen.url}")
    public static String USER_RPC_AUTHEN_URL;
    
    @Value("${user.rpc.uuidLst.url}")
    public static String USER_RPC_UUIDLIST_URL;

    @Value("${rbac.rpc.exchange}")
    public static String RBAC_RPC_EXCHANGE;

    @Value("${rbac.rpc.queue}")
    public static String RBAC_RPC_QUEUE;

    @Value("${rbac.rpc.key}")
    public static String RBAC_RPC_KEY;

    @Value("${rbac.rpc.author.url}")
    public static String RBAC_RPC_AUTHOR_URL;
    /** ------------------ */
    
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
    public RabbitMQProperties(@Value("${user.rpc.exchange}") String userRpcExchange,
            @Value("${user.rpc.queue}") String userRpcQueue,
            @Value("${user.rpc.key}") String userRpcKey,
            @Value("${user.rpc.authen.url}") String userRpcAuthenUrl,
            @Value("${user.rpc.uuidLst.url}") String userRpcUuidLstUrl,
            @Value("${rbac.rpc.exchange}") String rbacRpcExchange,
            @Value("${rbac.rpc.queue}") String rbacRpcQueue,
            @Value("${rbac.rpc.key}") String rbacRpcKey,
            @Value("${rbac.rpc.author.url}") String rbacRpcAuthorUrl,
            @Value("${vs.device.rpc.exchange}") String deviceRpcExchange,
            @Value("${vs.device.rpc.queue}") String deviceRpcQueue,
            @Value("${vs.device.rpc.key}") String deviceRpcKey,
            @Value("${vs.device.worker.queue}") String deviceWorkerQueue) {
        
        USER_RPC_EXCHANGE = userRpcExchange;
        USER_RPC_QUEUE = userRpcQueue;
        USER_RPC_KEY = userRpcKey;
        USER_RPC_AUTHEN_URL = userRpcAuthenUrl;
        USER_RPC_UUIDLIST_URL = userRpcUuidLstUrl;
        
        RBAC_RPC_EXCHANGE = rbacRpcExchange;
        RBAC_RPC_QUEUE = rbacRpcQueue;
        RBAC_RPC_KEY = rbacRpcKey;
        RBAC_RPC_AUTHOR_URL = rbacRpcAuthorUrl;
        
        VS_DEVICE_RPC_EXCHANGE = deviceRpcExchange;
        VS_DEVICE_RPC_QUEUE_NAME = deviceRpcQueue;
        VS_DEVICE_RPC_KEY = deviceRpcKey;
        VS_DEVICE_WORK_QUEUE_NAME = deviceWorkerQueue;
    }
}
