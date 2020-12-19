package com.elcom.notify.messaging.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author anhdv
 */
@Configuration
public class WorkerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerConfig.class);
    
    @Value("${vs.notify.worker.queue}${tech.env}")
    private String workerQueue;

    @Bean("vitalSignNotifyWorkerQueue")
    public Queue initWorkerQueue() {
        LOGGER.info("workerQueueName: {}", workerQueue);
        return new Queue(workerQueue);
    }

    @Bean
    public WorkerServer workerServer() {
        return new WorkerServer();
    }
}
