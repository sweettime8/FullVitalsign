///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.elcom.vitalsign.mqtt;
//
//import com.elcom.vitalsign.constant.Constant;
//import com.elcom.vitalsign.service.DataService;
//import java.util.concurrent.BlockingQueue;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
//
///**
// *
// * @author Admin
// */
//public class MqttSubscriberInitApp implements MqttCallback, Runnable {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(MqttSubscriberInitApp.class);
//
//    private static final String MQTT_BROKER = Constant.MQTT_BROKER_PROTOCOL + "://" + Constant.MQTT_BROKER_HOST + ":" + Constant.MQTT_BROKER_PORT;
//
//    private final BlockingQueue sharedQueueDspTurnOn;
//    private final BlockingQueue sharedQueueGetPatient;
//    private final BlockingQueue sharedQueueDspUnLnkGate;
//    private final BlockingQueue sharedQueueDspLnkGate;
//    private final BlockingQueue sharedQueueGateTurnOn;
//
//    private final BlockingQueue sharedQueueDspSearchGate;
//    private final BlockingQueue sharedQueueDspGetGateSensor;
//    private final BlockingQueue sharedQueueDspAddSensor;
//    private final BlockingQueue sharedQueueDspConnSen;
//    private final BlockingQueue sharedQueueDspDisConnSen;
//
//    private final DataService dataService;
//
//    public MqttSubscriberInitApp(BlockingQueue sharedQueueDspTurnOn,
//            BlockingQueue sharedQueueGetPatient, BlockingQueue sharedQueueDspUnLnkGate, BlockingQueue sharedQueueDspLnkGate,
//            BlockingQueue sharedQueueGateTurnOn, BlockingQueue sharedQueueDspSearchGate,
//            BlockingQueue sharedQueueDspGetGateSensor, BlockingQueue sharedQueueDspAddSensor,
//            BlockingQueue sharedQueueDspConnSen, BlockingQueue sharedQueueDspDisConnSen,
//            ApplicationContext applicationContext) {
//
//        this.sharedQueueDspTurnOn = sharedQueueDspTurnOn;
//        this.sharedQueueGetPatient = sharedQueueGetPatient;
//        this.sharedQueueDspUnLnkGate = sharedQueueDspUnLnkGate;
//        this.sharedQueueDspLnkGate = sharedQueueDspLnkGate;
//        this.sharedQueueGateTurnOn = sharedQueueGateTurnOn;
//        this.sharedQueueDspSearchGate = sharedQueueDspSearchGate;
//        this.sharedQueueDspGetGateSensor = sharedQueueDspGetGateSensor;
//        this.sharedQueueDspAddSensor = sharedQueueDspAddSensor;
//        this.sharedQueueDspConnSen = sharedQueueDspConnSen;
//        this.sharedQueueDspDisConnSen = sharedQueueDspDisConnSen;
//
//        this.dataService = applicationContext.getBean(DataService.class);
//
//    }
//
//    @Override
//    public void connectionLost(Throwable cause) {
//        LOGGER.info("Connection lost because: " + cause);
//    }
//
//    @Override
//    public void messageArrived(String topic, MqttMessage message) throws MqttException {
//        LOGGER.info("[messageArrived topic]: " + topic);
//        if (message != null && message.getPayload() != null) {
//            LOGGER.info("Message received:\n\t" + new String(message.getPayload()));
//            if (topic.contains(Constant.DISPLAY_REQ_GATE_SENSOR)) { //display turn on
//                try {
//                    sharedQueueDspTurnOn.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//            if (topic.contains(Constant.GET_PATIENT_LIST)) { //display get patient List
//                try {
//                    sharedQueueGetPatient.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//            if (topic.contains(Constant.DISPLAY_UNLINK_GATE_REQ)) {
//                try {
//                    sharedQueueDspUnLnkGate.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//            if (topic.contains(Constant.DISPLAY_LINK_GATE_REQ)) {
//                try {
//                    sharedQueueDspLnkGate.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//            if (topic.contains(Constant.GATE_REQ_DISPLAY_SENSOR)) {
//                try {
//                    sharedQueueGateTurnOn.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//            if (topic.contains(Constant.DISPLAY_SERVER_SEARCH_GATE_REQ)) { // display search gate
//                try {
//                    sharedQueueDspSearchGate.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//
//            if (topic.contains(Constant.DISPLAY_SERVER_GET_GATE_SENSOR_LINKED_REQ)) { // display get gate sensor after add 
//                try {
//                    sharedQueueDspGetGateSensor.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//
//            if (topic.contains(Constant.GATE_SERVER_ADD_SENSOR_REQ)) { // add sensor
//                try {
//                    sharedQueueDspAddSensor.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//            if (topic.contains(Constant.RES_CONNECT_TO_SENSOR)) { // connect sensor
//                try {
//                    sharedQueueDspConnSen.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//            if (topic.contains(Constant.RES_DISCONNECT_TO_SENSOR)) {
//                try {
//                    sharedQueueDspDisConnSen.put(new String(message.getPayload()));
//                } catch (Exception ex) {
//                    LOGGER.error(ex.toString());
//                }
//            }
//        }
//    }
//
//    @Override
//    public void deliveryComplete(IMqttDeliveryToken token) {
//        try {
//            MqttMessage mqttMessage = token.getMessage();
//            String msg = new String(mqttMessage.getPayload());
//            LOGGER.info("deliveryComplete, msg = " + msg);
//        } catch (MqttException ex) {
//            LOGGER.error(ex.toString());
//        }
//    }
//
//    private void subscribersByGate() throws MqttException {
//        LOGGER.info("[subscribersByGate topic ]");
//        MqttConnectOptions conOpt = new MqttConnectOptions();
//        conOpt.setCleanSession(false);
//        conOpt.setAutomaticReconnect(true);
//        conOpt.setConnectionTimeout(10);
//        conOpt.setKeepAliveInterval(1800);
//        conOpt.setUserName(Constant.MQTT_BROKER_USERNAME);
//        conOpt.setPassword(Constant.MQTT_BROKER_PASSWORD.toCharArray());
//
//        MqttClient mqttClient;
//        String[] topicName = {
//            "GATE/REQ/DISPLAY/SENSOR/#",
//            "GATE/SERVER/ADD_SENSOR_REQ/#"
//        };
//        for (String topic : topicName) {
//            try {
//                mqttClient = new MqttClient(MQTT_BROKER, MqttClient.generateClientId(), new MemoryPersistence());
//                mqttClient.setCallback(this);
//                mqttClient.connect(conOpt);
//                mqttClient.subscribe(topic, Constant.MQTT_QOS);
//                LOGGER.info("[" + topic + "] subscribe topic [" + topic + "] SUCCESS!");
//            } catch (Exception ex) {
//                LOGGER.info("[" + topic + "] subscribe topic [" + topic + "] FAILED!, ex: " + ex.toString());
//            }
//        }
//
//    }
//
//    private void subscribersByDisplay() throws MqttException {
//        LOGGER.info("[subscribersByDisplay topic ]");
//        MqttConnectOptions conOpt = new MqttConnectOptions();
//        conOpt.setCleanSession(false);
//        conOpt.setAutomaticReconnect(true);
//        conOpt.setConnectionTimeout(10);
//        conOpt.setKeepAliveInterval(1800);
//        conOpt.setUserName(Constant.MQTT_BROKER_USERNAME);
//        conOpt.setPassword(Constant.MQTT_BROKER_PASSWORD.toCharArray());
//        
//        MqttClient mqttClient;
//        String[] topicName = {
//            "DISPLAY/REQ/GATE/SENSOR/#", "DISPLAY/UNLINK/GATE/REQ/#", "GET/PATIENT/LIST/#",
//            "DISPLAY/LINK/GATE/REQ/#", "RES/CONNECT/TO/SENSOR/#", "RES/DISCONNECT/TO/SENSOR/#",
//            "DISPLAY/SERVER/SEARCH_GATE_REQ/#", "DISPLAY/SERVER/GET_GATE_SENSOR_LINKED_REQ/#"
//        };
//
//        for (String topic : topicName) {
//            try {
//                mqttClient = new MqttClient(MQTT_BROKER, MqttClient.generateClientId(), new MemoryPersistence());
//                mqttClient.setCallback(this);
//                mqttClient.connect(conOpt);
//                mqttClient.subscribe(topic, Constant.MQTT_QOS);
//                LOGGER.info("[" + topic + "] subscribe topic [" + topic + "] SUCCESS!");
//            } catch (Exception ex) {
//                LOGGER.info("[" + topic + "] subscribe topic [" + topic + "] FAILED!, ex: " + ex.toString());
//            }
//        }
//    }
//
//    @Override
//    public void run() {
//        try {
//            this.subscribersByDisplay();
//            this.subscribersByGate();
//        } catch (MqttException ex) {
//            LOGGER.error(ex.toString());
//        }
//    }
//
//}
