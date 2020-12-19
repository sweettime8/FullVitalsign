package com.elcom.usersystem.mqtt;

import com.elcom.usersystem.config.PropertiesConfig;
import com.elcom.usersystem.constant.Constant;
import com.elcom.usersystem.utils.SslUtil;
import java.io.IOException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class PublisherLoadBalancing implements Runnable {

    private static final String MQTT_URL = Constant.MQTT_BROKER_PROTOCOL
                                        + "://" + Constant.MQTT_BROKER_HOST
                                        + ":" + Constant.MQTT_BROKER_PORT;

    public static void main(String[] args) throws MqttException, IOException, InterruptedException {
        new Thread(new PublisherLoadBalancing()).start();
    }
    
    private static void sendSpo2Forever(MqttClient mqttClient, int stt) throws MqttException, IOException {
        MqttTopic topic = mqttClient.getTopic(Constant.MQTT_TOPIC);
        String data = stt+"";
        MqttMessage message = new MqttMessage(data.getBytes());
        message.setQos(Constant.MQTT_QOS);
        message.setRetained(false);
        topic.publish(message);
        System.out.println("Publish data to [$share/my-group/abc] success!");
    }

    @Override
    public void run() {
        try {
            MqttClient mqttClient = new MqttClient(MQTT_URL, MqttClient.generateClientId());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            connOpts.setAutomaticReconnect(true);
            connOpts.setConnectionTimeout(10);
            connOpts.setKeepAliveInterval(1800);
//            connOpts.setUserName(Constant.MQTT_USR);
//            connOpts.setPassword(Constant.MQTT_PW.toCharArray());
            connOpts.setSocketFactory(SslUtil.getSocketFactory("F:\\Work\\IoT\\Vitalsign\\GitLab\\Source\\vitalsign-platform\\v2\\VitalsignHospitalService\\config\\ssl-mqttkeystore\\ca.crt",
                    "F:\\Work\\IoT\\Vitalsign\\GitLab\\Source\\vitalsign-platform\\v2\\VitalsignHospitalService\\config\\ssl-mqttkeystore\\client.crt"
                , "F:\\Work\\IoT\\Vitalsign\\GitLab\\Source\\vitalsign-platform\\v2\\VitalsignHospitalService\\config\\ssl-mqttkeystore\\client.key", Constant.MQTT_PW));
            
            mqttClient.connect(connOpts);
            
            int stt = 1;
            while(true) {
                sendSpo2Forever(mqttClient, stt);
                stt++;
                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
