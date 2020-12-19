package com.elcom.usersystem.messaging.rabbitmq;

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
    
    @Value("${vs.user-system.rpc.exchange}")
    private String rpcExchange;
    
    @Value("${vs.user-system.rpc.queue}")
    private String rpcQueue;
    
    @Value("${vs.user-system.rpc.key}")
    private String rpcKey;

    @Bean("vitalSignUserSystemRpcQueue")
    @Primary
    public Queue initRpcQueue() {
        LOGGER.info("rpcQueueName: {}", rpcQueue);
        return new Queue(rpcQueue);
    }

    @Bean("vitalSignUserSystemRpcExchange")
    public DirectExchange rpcExchange() {
        LOGGER.info("rpcExchangeName: {}", rpcExchange);
        return new DirectExchange(rpcExchange);
    }

    @Bean("vitalSignUserSystemRpcBinding")
    public Binding bindingRpc(DirectExchange rpcExchange, Queue rpcQueue) {
        LOGGER.info("rpcRoutingKey: {}", rpcKey);
        return BindingBuilder.bind(rpcQueue).to(rpcExchange).with(rpcKey);
    }

    @Bean
    public RpcServer rpcServer() {
        return new RpcServer();
    }
}
