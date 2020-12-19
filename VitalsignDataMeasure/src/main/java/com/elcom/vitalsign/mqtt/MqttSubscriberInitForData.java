package com.elcom.vitalsign.mqtt;

import com.elcom.vitalsign.config.PropertiesConfig;
import com.elcom.vitalsign.constant.Constant;
import com.elcom.vitalsign.message.RequestMessage;
import com.elcom.vitalsign.messaging.rabbitmq.RabbitMQClient;
import com.elcom.vitalsign.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.vitalsign.model.measuredata.DataBp;
import com.elcom.vitalsign.model.measuredata.DataSpo2;
import com.elcom.vitalsign.model.measuredata.DataTemp;
import com.elcom.vitalsign.service.DataService;
import com.elcom.vitalsign.utils.JSONConverter;
import com.elcom.vitalsign.utils.SslUtil;
import com.elcom.vitalsign.utils.StringUtil;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 *
 * @author admin
 */
public class MqttSubscriberInitForData implements MqttCallback, Runnable {

    public static List<String> subscriberLst = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttSubscriberInitForData.class);

    private static final String MQTT_BROKER = Constant.MQTT_BROKER_PROTOCOL + "://" + Constant.MQTT_BROKER_HOST + ":" + Constant.MQTT_BROKER_PORT;

    private final BlockingQueue sharedQueue;

    private final DataService dataService;

    private RabbitMQClient rabbitMQClient;

    public MqttSubscriberInitForData(BlockingQueue sharedQueueData, ApplicationContext applicationContext) {
        this.sharedQueue = sharedQueueData;

        this.dataService = applicationContext.getBean(DataService.class);
        this.rabbitMQClient = applicationContext.getBean(RabbitMQClient.class);

    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LOGGER.info("[messageArrived topic]: " + topic);
        if (message != null && message.getPayload() != null) {
            LOGGER.info("Message received:\n" + new String(message.getPayload()));
            if (PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME.contains(Constant.RES_TRANSMIT_DATA_TEMP)) {
                try {
                    JSONObject jSONObject1 = new JSONObject(new String(message.getPayload()));
                    DataTemp dataTemp = new DataTemp();
                    dataTemp.setGateId(jSONObject1.getString("gate_id"));
                    dataTemp.setUserProfileId(jSONObject1.getString("user_profile_id"));
                    dataTemp.setMeasureId(jSONObject1.getString("measure_id"));
                    dataTemp.setTs(Integer.parseInt(jSONObject1.get("ts").toString()));
                    dataTemp.setTemp(Float.parseFloat(jSONObject1.get("temp").toString()));
                    sharedQueue.put(dataTemp);

                    Map<String, String> headerMap = null;
                    RequestMessage requestMessage = new RequestMessage("POST", Constant.SRV_VER + "/notify/check-notify", null, null, new Gson().fromJson(jSONObject1.toString(), HashMap.class), null);
                    this.rabbitMQClient.callWorkerService(RabbitMQProperties.VS_NOTIFY_WORK_QUEUE_NAME, JSONConverter.toJSON(requestMessage));

                } catch (Exception ex) {
                    LOGGER.error(ex.toString());
                }
            } else if (PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME.contains(Constant.RES_TRANSMIT_DATA_SPO2)) {
                try {
                    JSONObject jSONObject2 = new JSONObject(new String(message.getPayload()));
                    DataSpo2 dataSpo2 = new DataSpo2();
                    dataSpo2.setGateId(jSONObject2.getString("gate_id"));
                    dataSpo2.setMeasureId(jSONObject2.getString("measure_id"));
                    dataSpo2.setTs(Integer.parseInt(jSONObject2.get("ts").toString()));
                    dataSpo2.setSpo2(Integer.parseInt(jSONObject2.get("spo2").toString()));
                    dataSpo2.setPi(Integer.parseInt(jSONObject2.get("pi").toString()));
                    dataSpo2.setPr(Double.parseDouble(jSONObject2.get("pr").toString()));
                    dataSpo2.setStep(Integer.parseInt(jSONObject2.get("step").toString()));

                    sharedQueue.put(dataSpo2);

                    Map<String, String> headerMap = null;
                    RequestMessage requestMessage = new RequestMessage("POST", Constant.SRV_VER + "/notify/check-notify", null, null, new Gson().fromJson(jSONObject2.toString(), HashMap.class), null);
                    this.rabbitMQClient.callWorkerService(RabbitMQProperties.VS_NOTIFY_WORK_QUEUE_NAME, JSONConverter.toJSON(requestMessage));

                } catch (Exception ex) {
                    LOGGER.error(ex.toString());
                }
            } else if (PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME.contains(Constant.RES_TRANSMIT_DATA_NIBP)) {
                try {
                    DataBp dataBp = new DataBp();
                    JSONObject jSONObject3 = new JSONObject(new String(message.getPayload()));
                    dataBp.setGateId(jSONObject3.getString("gate_id"));
                    dataBp.setMeasureId(jSONObject3.getString("measure_id"));
                    dataBp.setTs(Integer.parseInt(jSONObject3.get("ts").toString()));
                    dataBp.setDia(Long.parseLong(jSONObject3.get("dia").toString()));
                    dataBp.setSys(Long.parseLong(jSONObject3.get("sys").toString()));
                    dataBp.setMap(Long.parseLong(jSONObject3.get("map").toString()));
                    dataBp.setPr(Integer.parseInt(jSONObject3.get("pr").toString()));
                    sharedQueue.put(dataBp);

                    Map<String, String> headerMap = null;
                    RequestMessage requestMessage = new RequestMessage("POST", Constant.SRV_VER + "/notify/check-notify", null, null, new Gson().fromJson(jSONObject3.toString(), HashMap.class), null);
                    this.rabbitMQClient.callWorkerService(RabbitMQProperties.VS_NOTIFY_WORK_QUEUE_NAME, JSONConverter.toJSON(requestMessage));

                } catch (Exception ex) {
                    LOGGER.error(ex.toString());
                }
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token
    ) {
        try {
            MqttMessage mqttMessage = token.getMessage();
            String msg = new String(mqttMessage.getPayload());
            LOGGER.info("deliveryComplete, msg = " + msg);
        } catch (MqttException ex) {
            LOGGER.error(ex.toString());
        }
    }

    private void subscribersData() throws MqttException, Exception {
        LOGGER.info("[ subscribersDATA topic ]");
        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(false);
        conOpt.setAutomaticReconnect(true);
        conOpt.setConnectionTimeout(10);
        conOpt.setKeepAliveInterval(1800);
        conOpt.setUserName(PropertiesConfig.MQTT_BROKER_USERNAME);
        conOpt.setPassword(PropertiesConfig.MQTT_BROKER_PASSWORD.toCharArray());

        conOpt.setSocketFactory(SslUtil.getSocketFactory(PropertiesConfig.MQTT_CA_FILE_PATH,
                PropertiesConfig.MQTT_CLIENTCRT_FILE_PATH, PropertiesConfig.MQTT_CLIENTKEY_FILE_PATH,
                PropertiesConfig.MQTT_BROKER_PASSWORD));
//        
        MqttClient mqttClient;

        String subId = PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME + PropertiesConfig.MQTT_NODE_NUMBER;
        String topicName = PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME + "/#";
        try {
            mqttClient = new MqttClient(MQTT_BROKER, subId, new MemoryPersistence());
            mqttClient.setCallback(this);
            mqttClient.connect(conOpt);

            mqttClient.subscribe(topicName, Constant.MQTT_QOS);
            subscriberLst.add(topicName);

            LOGGER.info("[" + subId + "] subscribe topic [" + topicName + "] SUCCESS!");
        } catch (Exception ex) {
            LOGGER.info("[" + subId + "] subscribe topic [" + topicName + "] FAILED!, ex: " + ex.toString());
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    MqttConnectOptions conOpt = new MqttConnectOptions();
                    conOpt.setCleanSession(false);
                    conOpt.setAutomaticReconnect(true);
                    conOpt.setConnectionTimeout(10);
                    conOpt.setKeepAliveInterval(1800);
                    conOpt.setUserName(PropertiesConfig.MQTT_BROKER_USERNAME);
                    conOpt.setPassword(PropertiesConfig.MQTT_BROKER_PASSWORD.toCharArray());
                    conOpt.setSocketFactory(SslUtil.getSocketFactory(PropertiesConfig.MQTT_CA_FILE_PATH,
                            PropertiesConfig.MQTT_CLIENTCRT_FILE_PATH, PropertiesConfig.MQTT_CLIENTKEY_FILE_PATH,
                            PropertiesConfig.MQTT_BROKER_PASSWORD));

                    MqttClient mqttClient;
                    String subId = PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME + PropertiesConfig.MQTT_NODE_NUMBER;
                    String topicName = PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME + "/#";
                    LOGGER.info("SHUT DOWN APP!!!");
                    try {
                        mqttClient = new MqttClient(MQTT_BROKER, subId, new MemoryPersistence());
                        mqttClient.connect(conOpt);

                        mqttClient.unsubscribe(topicName);
                        LOGGER.info("UNSUBSCRIBE [" + subId + "]  topic [" + topicName + "] SUCCESS! ");
                    } catch (Exception ex) {
                        LOGGER.info(ex.toString());

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LOGGER.error(StringUtil.printException(ex));
                }
            }
        });

    }

    @Override
    public void run() {
        try {
            this.subscribersData();
        } catch (MqttException ex) {
            LOGGER.error(ex.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(StringUtil.printException(ex));
        }
    }

}
