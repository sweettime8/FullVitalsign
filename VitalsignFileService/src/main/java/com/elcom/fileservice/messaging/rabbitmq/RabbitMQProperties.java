package com.elcom.fileservice.messaging.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class RabbitMQProperties {

    //User service
    @Value("${user.rpc.exchange}")
    public static String USER_RPC_EXCHANGE;

    @Value("${user.rpc.queue}")
    public static String USER_RPC_QUEUE;

    @Value("${user.rpc.key}")
    public static String USER_RPC_KEY;

    @Value("${user.rpc.authen.url}")
    public static String USER_RPC_AUTHEN_URL;
    
    /** Device */
    @Value("${vs.device.rpc.exchange}")
    public static String VS_DEVICE_RPC_EXCHANGE;
    
    @Value("${vs.device.rpc.queue}")
    public static String VS_DEVICE_RPC_QUEUE_NAME;
    
    @Value("${vs.device.rpc.key}")
    public static String VS_DEVICE_RPC_KEY;
    
    @Value("${vs.device.worker.queue}")
    public static String VS_DEVICE_WORK_QUEUE_NAME;

    @Autowired
    public RabbitMQProperties(@Value("${user.rpc.exchange}") String userRpcExchange,
                    @Value("${user.rpc.queue}") String userRpcQueue,
                    @Value("${user.rpc.key}") String userRpcKey,
                    @Value("${user.rpc.authen.url}") String userRpcAuthenUrl,
                    @Value("${vs.device.rpc.exchange}") String deviceRpcExchange,
                    @Value("${vs.device.rpc.queue}") String deviceRpcQueue,
                    @Value("${vs.device.rpc.key}") String deviceRpcKey,
                    @Value("${vs.device.worker.queue}") String deviceWorkerQueue) {
        USER_RPC_EXCHANGE = userRpcExchange;
        USER_RPC_QUEUE = userRpcQueue;
        USER_RPC_KEY = userRpcKey;
        USER_RPC_AUTHEN_URL = userRpcAuthenUrl;
        
        VS_DEVICE_RPC_EXCHANGE = deviceRpcExchange;
        VS_DEVICE_RPC_QUEUE_NAME = deviceRpcQueue;
        VS_DEVICE_RPC_KEY = deviceRpcKey;
        VS_DEVICE_WORK_QUEUE_NAME = deviceWorkerQueue;
    }
}
