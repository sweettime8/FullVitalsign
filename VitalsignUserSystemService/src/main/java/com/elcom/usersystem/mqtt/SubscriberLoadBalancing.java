package com.elcom.usersystem.mqtt;

import com.elcom.usersystem.config.PropertiesConfig;
import com.elcom.usersystem.constant.Constant;
import com.elcom.usersystem.utils.SslUtil;
import com.elcom.usersystem.utils.StringUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sample application that demonstrates how to use the Paho MQTT v3.1 Client
 * blocking API.
 */
public class SubscriberLoadBalancing implements MqttCallback {

    private MqttClient client;

    public SubscriberLoadBalancing(String uri) throws MqttException, URISyntaxException, InterruptedException, Exception {
        this(new URI(uri));
    }

    public SubscriberLoadBalancing(URI uri) throws MqttException, InterruptedException, Exception {
        String host = String.format(Constant.MQTT_BROKER_PROTOCOL + "://%s:%d", uri.getHost(), uri.getPort());

        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(false);
        conOpt.setAutomaticReconnect(true);
        conOpt.setConnectionTimeout(10);
        conOpt.setKeepAliveInterval(1800);
//        conOpt.setUserName(Constant.MQTT_USR);
//        conOpt.setPassword(Constant.MQTT_PW.toCharArray());
        conOpt.setSocketFactory(SslUtil.getSocketFactory("F:\\Work\\IoT\\Vitalsign\\GitLab\\Source\\vitalsign-platform\\v2\\VitalsignHospitalService\\config\\ssl-mqttkeystore\\ca.crt",
                    "F:\\Work\\IoT\\Vitalsign\\GitLab\\Source\\vitalsign-platform\\v2\\VitalsignHospitalService\\config\\ssl-mqttkeystore\\client.crt"
                , "F:\\Work\\IoT\\Vitalsign\\GitLab\\Source\\vitalsign-platform\\v2\\VitalsignHospitalService\\config\\ssl-mqttkeystore\\client.key", Constant.MQTT_PW));
        
        this.client = new MqttClient(host, "SUB-02", new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect(conOpt);
        
        String topicName = "$share/" + Constant.MQTT_GROUP_TOPIC + "/" + Constant.MQTT_TOPIC;
        try {
            this.client.subscribe(topicName, Constant.MQTT_QOS);
        } catch (Exception ex) {
            System.out.println("SUBSCRIBE TOPIC ["+topicName+"] FAILED, ex: " + StringUtil.printException(ex));
        }
        System.out.println("SUBSCRIBE TOPIC ["+topicName+"] SUCCESS!");
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("SHUT DOWN APP!!!");
                try {
                    client.unsubscribe(topicName);
                } catch (Exception ex) {
                    System.out.println("UNSUBSCRIBE TOPIC ["+topicName+"] FAILED, ex: " + StringUtil.printException(ex));
                }
                System.out.println("UNSUBSCRIBE TOPIC ["+topicName+"] SUCCESS!");
            }
        });
        Thread.sleep(90000);
        System.exit(0);
    }

    /*public void sendMessage(String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(QOS);
        this.client.publish(TOPIC, message); // Blocking publish
    }*/
    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost because: " + cause);
        System.exit(1);
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            System.out.println("deliveryComplete: " +  new String(token.getMessage().getPayload()));
        } catch (Exception ex) {
            Logger.getLogger(SubscriberLoadBalancing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        System.out.println(
                String.format("topic:[%s] - data:[%s] - msgId:[%s]",
                        topic,
                        new String(message.getPayload()),
                        message.getId())
        );
    }
    
    public static void main(String[] args) throws MqttException, URISyntaxException, InterruptedException, Exception {
        SubscriberLoadBalancing sub = new SubscriberLoadBalancing(Constant.MQTT_BROKER_PROTOCOL
                                                                + "://" + Constant.MQTT_BROKER_HOST
                                                                + ":" + Constant.MQTT_BROKER_PORT);
        //sub.sendMessage("Hello1");
    }
}
