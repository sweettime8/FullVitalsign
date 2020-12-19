/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class PropertiesConfig {

    public static String MQTT_SUBSCRIBE_CLIENT_ID;
    public static String MQTT_SUBSCRIBE_TOPIC_NAME;
    public static String TECH_ENV;
    public static boolean APP_MASTER;

    public static String MQTT_BROKER_USERNAME;
    public static String MQTT_BROKER_PASSWORD;
    public static String MQTT_CA_FILE_PATH;
    public static String MQTT_CLIENTCRT_FILE_PATH;
    public static String MQTT_CLIENTKEY_FILE_PATH;
    public static String MQTT_NODE_NUMBER;

    @Autowired
    public PropertiesConfig(@Value("${mqtt.subscribe.client.id}") String mqttSubscribeClientId,
            @Value("${mqtt.subscribe.topic.name}") String mqttSubscribeTopicName,
            @Value("${tech.env}") String techEnv,
            @Value("${app.master}") boolean isAppMaster,
            @Value("${mqtt.username}") String username,
            @Value("${mqtt.password}") String password,
            @Value("${mqtt.ssl.ca}") String ca,
            @Value("${mqtt.ssl.clientcrt}") String clientcrt,
            @Value("${mqtt.ssl.clientkey}") String clientkey,
            @Value("${mqtt.node.number}") String nodenumber
            
    ) {
        PropertiesConfig.MQTT_SUBSCRIBE_CLIENT_ID = mqttSubscribeClientId;
        PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME = mqttSubscribeTopicName;
        PropertiesConfig.TECH_ENV = techEnv;
        PropertiesConfig.APP_MASTER = isAppMaster;

        PropertiesConfig.MQTT_BROKER_USERNAME = username;
        PropertiesConfig.MQTT_BROKER_PASSWORD = password;
        PropertiesConfig.MQTT_CA_FILE_PATH = ca;
        PropertiesConfig.MQTT_CLIENTCRT_FILE_PATH = clientcrt;
        PropertiesConfig.MQTT_CLIENTKEY_FILE_PATH = clientkey;
        PropertiesConfig.MQTT_NODE_NUMBER = nodenumber;

    }
}
