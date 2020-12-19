package com.elcom.vitalsign.messaging.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class RabbitMQProperties {

    /**
     * User & RBAC
     */
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
    /**
     * ------------------
     */

    /**
     * Measure data service
     */
    @Value("${vs.measuredata.rpc.exchange}")
    public static String VS_MEASURE_DATA_RPC_EXCHANGE;

    @Value("${vs.measuredata.rpc.queue}")
    public static String VS_MEASURE_DATA_RPC_QUEUE_NAME;

    @Value("${vs.measuredata.rpc.key}")
    public static String VS_MEASURE_DATA_RPC_KEY;

    @Value("${vs.measuredata.worker.queue}")
    public static String VS_MEASURE_DATA_WORK_QUEUE_NAME;

    @Value("${vs.notify.worker.queue}")
    public static String VS_NOTIFY_WORK_QUEUE_NAME;

    /**
     * ------------------
     */

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
            @Value("${vs.measuredata.rpc.exchange}") String measureDataRpcExchange,
            @Value("${vs.measuredata.rpc.queue}") String measureDataRpcQueue,
            @Value("${vs.measuredata.rpc.key}") String measureDataRpcKey,
            @Value("${vs.measuredata.worker.queue}") String measureDataWorkerQueue,
            @Value("${vs.notify.worker.queue}") String notifyWorkerQueue) {

        USER_RPC_EXCHANGE = userRpcExchange;
        USER_RPC_QUEUE = userRpcQueue;
        USER_RPC_KEY = userRpcKey;
        USER_RPC_AUTHEN_URL = userRpcAuthenUrl;
        USER_RPC_UUIDLIST_URL = userRpcUuidLstUrl;

        RBAC_RPC_EXCHANGE = rbacRpcExchange;
        RBAC_RPC_QUEUE = rbacRpcQueue;
        RBAC_RPC_KEY = rbacRpcKey;
        RBAC_RPC_AUTHOR_URL = rbacRpcAuthorUrl;

        VS_MEASURE_DATA_RPC_EXCHANGE = measureDataRpcExchange;
        VS_MEASURE_DATA_RPC_QUEUE_NAME = measureDataRpcQueue;
        VS_MEASURE_DATA_RPC_KEY = measureDataRpcKey;
        VS_MEASURE_DATA_WORK_QUEUE_NAME = measureDataWorkerQueue;
        VS_NOTIFY_WORK_QUEUE_NAME = notifyWorkerQueue ;
    }
}
