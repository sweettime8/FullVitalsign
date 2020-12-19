/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.process;

import com.elcom.vitalsign.config.PropertiesConfig;
import com.elcom.vitalsign.constant.Constant;
import com.elcom.vitalsign.model.measuredata.DataBp;
import com.elcom.vitalsign.model.measuredata.DataSpo2;
import com.elcom.vitalsign.model.measuredata.DataTemp;
import com.elcom.vitalsign.service.DataService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author admin
 */
public class MqttConsumerInitForData implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConsumerInitForData.class);

    private final BlockingQueue sharedQueueData;
    private final DataService dataService;

    public MqttConsumerInitForData(BlockingQueue sharedQueueData, ApplicationContext applicationContext) {
        this.sharedQueueData = sharedQueueData;
        this.dataService = applicationContext.getBean(DataService.class);
    }

    @Override
    public void run() {
        try {
            List lst;
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(Constant.REST_TIMES_QUEUE);
                if (sharedQueueData.size() > 0) {
                    lst = new ArrayList<>();
                    sharedQueueData.drainTo(lst);

                    if (PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME.contains(Constant.RES_TRANSMIT_DATA_NIBP)) {
                        LOGGER.info("Consummer - [RES_TRANSMIT_DATA_NIBP]");
                        try {
                            this.dataService.saveDataBp((List<DataBp>) lst);
                        } catch (Exception ex) {
                            LOGGER.error(ex.toString());
                        }
                    }

                    if (PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME.contains(Constant.RES_TRANSMIT_DATA_SPO2)) {
                        LOGGER.info("Consummer - [RES_TRANSMIT_DATA_SPO2]");
                        try {
                            this.dataService.saveDataSpo2((List<DataSpo2>) lst);
                        } catch (Exception ex) {
                            LOGGER.error(ex.toString());
                        }
                    }

                    if (PropertiesConfig.MQTT_SUBSCRIBE_TOPIC_NAME.contains(Constant.RES_TRANSMIT_DATA_TEMP)) {
                        LOGGER.info("Consummer - [RES_TRANSMIT_DATA_TEMP]");
                        try {
                            this.dataService.saveDataTemp((List<DataTemp>) lst);
                        } catch (Exception ex) {
                            LOGGER.error(ex.toString());
                        }
                    }

                }

            }

        } catch (InterruptedException ex) {
            LOGGER.error(ex.toString());
        }
    }
}
