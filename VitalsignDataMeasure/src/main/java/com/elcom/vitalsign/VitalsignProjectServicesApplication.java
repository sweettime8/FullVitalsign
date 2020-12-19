package com.elcom.vitalsign;

import com.elcom.vitalsign.mqtt.MqttSubscriberInitForData;
import com.elcom.vitalsign.process.DaemonThreadFactory;
import com.elcom.vitalsign.process.MqttConsumerInitForData;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class VitalsignProjectServicesApplication {

    public static void main(String[] args) throws MqttException, URISyntaxException {
        System.out.println("[CPU] : " + Runtime.getRuntime().availableProcessors());
        ApplicationContext applicationContext = SpringApplication.run(VitalsignProjectServicesApplication.class, args);

        BlockingQueue sharedQueueData = new LinkedBlockingQueue();

        Runnable prodThreadData = new MqttSubscriberInitForData(sharedQueueData, applicationContext);
        Runnable consThreadData = new MqttConsumerInitForData(sharedQueueData, applicationContext);

        //executor 
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new DaemonThreadFactory());
        //data Spo2, NIBP, TEMP
        executorService.submit(prodThreadData);
        executorService.submit(consThreadData);

    }

}
