package com.elcom.vitalsign.messaging.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 *
 * @author Admin
 */
@Configuration
public class RpcConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConfig.class);
    
    @Value("${vs.measuredata.rpc.exchange}")
    private String rpcExchange;
    
    @Value("${vs.measuredata.rpc.queue}")
    private String rpcQueue;
    
    @Value("${vs.measuredata.rpc.key}")
    private String rpcKey;

    @Bean("vitalSignMeasureDataRpcQueue")
    @Primary
    public Queue initRpcQueue() {
        LOGGER.info("rpcQueueName: {}", rpcQueue);
        return new Queue(rpcQueue);
    }

    @Bean("vitalSignMeasureDataRpcExchange")
    public DirectExchange rpcExchange() {
        LOGGER.info("rpcExchangeName: {}", rpcExchange);
        return new DirectExchange(rpcExchange);
    }

    @Bean("vitalSignMeasureDataRpcBinding")
    public Binding bindingRpc(DirectExchange rpcExchange, Queue rpcQueue) {
        LOGGER.info("rpcRoutingKey: {}", rpcKey);
        return BindingBuilder.bind(rpcQueue).to(rpcExchange).with(rpcKey);
    }

    @Bean
    public RpcServer rpcServer() {
        return new RpcServer();
    }
}
