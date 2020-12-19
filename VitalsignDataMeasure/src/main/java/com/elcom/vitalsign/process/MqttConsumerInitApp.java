///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.elcom.vitalsign.process;
//
//import com.elcom.vitalsign.constant.Constant;
//import com.elcom.vitalsign.model.Display;
//import com.elcom.vitalsign.model.Gate;
//import com.elcom.vitalsign.model.Patient;
//import com.elcom.vitalsign.model.Sensor;
//import com.elcom.vitalsign.model.dto.DisplayWithSensor;
//import com.elcom.vitalsign.model.dto.GateLinkedWithSenSor;
//import com.elcom.vitalsign.model.dto.GateWithSensorDisplay;
//import com.elcom.vitalsign.mqtt.MqttPulisherRes;
//import com.elcom.vitalsign.service.DataService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.BlockingQueue;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
//
///**
// *
// * @author admin
// */
//public class MqttConsumerInitApp implements Runnable {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConsumerInitApp.class);
//
//    private final BlockingQueue sharedQueueDspTurnOn;
//    private final BlockingQueue sharedQueueGetPatient;
//    private final BlockingQueue sharedQueueDspUnLnkGate;
//    private final BlockingQueue sharedQueueDspLnkGate;
//    private final BlockingQueue sharedQueueGateTurnOn;
//    private final BlockingQueue sharedQueueDspSearchGate;
//    private final BlockingQueue sharedQueueDspGetGateSensor;
//    private final BlockingQueue sharedQueueDspAddSensor;
//    private final BlockingQueue sharedQueueDspConnSen;
//    private final BlockingQueue sharedQueueDspDisConnSen;
//
//    private final DataService dataService;
//
//    public MqttConsumerInitApp(BlockingQueue sharedQueueDspTurnOn, BlockingQueue sharedQueueGetPatient,
//            BlockingQueue sharedQueueDspUnLnkGate, BlockingQueue sharedQueueDspLnkGate, BlockingQueue sharedQueueGateTurnOn,
//            BlockingQueue sharedQueueDspSearchGate, BlockingQueue sharedQueueDspGetGateSensor, BlockingQueue sharedQueueDspAddSensor,
//            BlockingQueue sharedQueueDspConnSen, BlockingQueue sharedQueueDspDisConnSen,
//            ApplicationContext applicationContext) {
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
//    }
//
//    @Override
//    public void run() {
//        try {
//            List lstDspTurnOn;
//            List lstPatients;
//            List lstDspUnLnkGate;
//            List lstDspLnkGate;
//            List lstGateTurnOn;
//            List lstDspSearchGate;
//            List lstDspGetGateSensor;
//            List lstDspAddSensor;
//            List lstDspConnSen;
//            List lstDspDisConnSen;
//
//            while (!Thread.currentThread().isInterrupted()) {
//                if (sharedQueueDspTurnOn.size() > 0) {
//                    lstDspTurnOn = new ArrayList<>();
//                    sharedQueueDspTurnOn.drainTo(lstDspTurnOn);
//                    for (Object s : lstDspTurnOn) {
//                        LOGGER.info("Consummer - [DISPLAY/REQ/GATE/SENSOR] : " + s.toString());
//                        try {
//                            JSONObject jSONObject = new JSONObject(s.toString());
//                            //display_id của display bắn lên tương ứng serial_number trong database
//                            Display display = this.dataService.findDisplayBySerialNumber(jSONObject.getString("display_id"));
//                            String gateId = display.getGateId();
//
//                            //gate_id trong data display tương ứng serial_number gate trong database
//                            Gate gate = this.dataService.findGateBySerialNumber(gateId);
//                            if (gate == null) {
//                                LOGGER.info("Gate is not linked for Display");
//                                break;
//                            }
//
//                            List<Sensor> lstSensor = this.dataService.findAllSensorByGateId(gateId);
//
//                            GateWithSensorDisplay gateSensor = new GateWithSensorDisplay();
//                            gateSensor.setGateId(gateId);  //tra ve cho Display with gateId = Gate_serialNumber
//                            gateSensor.setStatus(gate.getStatus() == 1 ? "active" : "inactive");
//                            gateSensor.setSensorLst(lstSensor);
//
//                            String topic = Constant.SERVER_RES_GATE_SENSOR + "/" + jSONObject.getString("display_id");
//
//                            MqttPulisherRes mqtt = new MqttPulisherRes();
//                            mqtt.publisherDisplayTurnOn(topic, gateSensor);
//                        } catch (MqttException | JSONException e) {
//                            LOGGER.error(e.toString());
//                        }
//                    }
//                }
//                //---------------------------------------
//
//                if (sharedQueueGetPatient.size() > 0) {
//                    lstPatients = new ArrayList<>();
//                    sharedQueueGetPatient.drainTo(lstPatients);
//                    for (Object s : lstPatients) {
//                        try {
//                            LOGGER.info("Consummer - [GET/PATIENT/LIST] : " + s.toString());
//                            JSONObject jSONObject = new JSONObject(s.toString());
//
//                            //display_id của display bắn lên tương ứng serial_number trong database
//                            Display display = this.dataService.findDisplayBySerialNumber(jSONObject.getString("display_id"));
//                            String gateId = display.getGateId();
//
//                            List<Sensor> lstSensor = this.dataService.findAllSensorByGateId(gateId);
//
//                            List<Patient> lstPatient = new ArrayList<>();
//                            for (Sensor sen : lstSensor) {
//                                try {
//                                    Patient pa = this.dataService.findPatientById(sen.getPatientId());
//                                    lstPatient.add(pa);
//                                } catch (Exception e) {
//                                    LOGGER.error(e.toString());
//                                }
//                            }
//                            String topic = Constant.RES_PATIENT_LIST + "/" + jSONObject.getString("display_id");
//                            MqttPulisherRes mqtt = new MqttPulisherRes();
//                            mqtt.publisherListPatient(topic, lstPatient);
//
//                        } catch (Exception e) {
//                            LOGGER.error(e.toString());
//                        }
//                    }
//                }
//
//                //----------------------------
//                if (sharedQueueDspUnLnkGate.size() > 0) {
//                    lstDspUnLnkGate = new ArrayList<>();
//                    sharedQueueDspUnLnkGate.drainTo(lstDspUnLnkGate);
//                    for (Object s : lstDspUnLnkGate) {
//                        try {
//                            LOGGER.info("Case = [DISPLAY/UNLINK/GATE/REQ] : " + s.toString());
//                            JSONObject jSONObject = new JSONObject(s.toString());
//
//                            Map<String, String> map = new LinkedHashMap<String, String>();
//                            map.put("display_id", jSONObject.getString("display_id"));
//                            map.put("gate_id", jSONObject.getString("gate_id"));
//
//                            //display_id của display bắn lên tương ứng serial_number trong database
//                            Display display = this.dataService.findDisplayBySerialNumber(jSONObject.getString("display_id"));
//                            if (display != null) {
//                                if (display.getGateId().equals("0")) {
//                                    map.put("result", "OK");
//                                } else {
//                                    if (display.getGateId().equals(jSONObject.getString("gate_id"))) {
//                                        this.dataService.unLinkGate(display);
//                                        map.put("result", "OK");
//                                    } else {
//                                        map.put("result", "FAILED");
//                                    }
//                                }
//                            } else {
//                                map.put("result", "FAILED");
//                            }
//                            String topic = Constant.DISPLAY_UNLINK_GATE_RES + "/" + jSONObject.getString("display_id");
//
//                            MqttPulisherRes mqtt = new MqttPulisherRes();
//                            mqtt.publisherDisplayUnLinkGate(topic, map);
//
//                        } catch (JsonProcessingException | MqttException | JSONException e) {
//                            LOGGER.error(e.toString());
//                        }
//                    }
//                }
//
//                //------------------------------------------
//                if (sharedQueueDspLnkGate.size() > 0) {
//                    lstDspLnkGate = new ArrayList<>();
//                    sharedQueueDspLnkGate.drainTo(lstDspLnkGate);
//                    for (Object s : lstDspLnkGate) {
//                        try {
//                            LOGGER.info("Consummer - [DISPLAY/LINK/GATE/REQ] : " + s.toString());
//                            JSONObject jSONObject = new JSONObject(s.toString());
//
//                            Map<String, String> map = new LinkedHashMap<>();
//                            map.put("display_id", jSONObject.getString("display_id"));
//                            map.put("gate_id", jSONObject.getString("gate_id"));
//
//                            //display_id của display bắn lên tương ứng serial_number trong database
//                            Display display = this.dataService.findDisplayBySerialNumber(jSONObject.getString("display_id"));
//
//                            if (display != null) {
//                                //gate_id display gui len  = serialNumber gate trong DB
//                                if (jSONObject.getString("gate_id").equals(display.getGateId())) {
//                                    map.put("result", "LINKED");
//                                } else if (display.getGateId().equals("0")) {
//                                    display.setGateId(jSONObject.getString("gate_id"));
//                                    this.dataService.addLinkGate(display);
//                                    map.put("result", "OK");
//                                }else{
//                                    map.put("result", "FAILED");
//                                }
//                            } else {
//                                map.put("result", "FAILED");
//                            }
//
//                            String topic = Constant.DISPLAY_LINK_GATE_RES + "/" + jSONObject.getString("display_id");
//
//                            MqttPulisherRes mqtt = new MqttPulisherRes();
//                            mqtt.publisherDisplayAddLinkGate(topic, map);
//
//                        } catch (JsonProcessingException | MqttException | JSONException e) {
//                            LOGGER.error(e.toString());
//                        }
//
//                    }
//                }
//
//                //--------------------------------------------------
//                if (sharedQueueGateTurnOn.size() > 0) {
//                    lstGateTurnOn = new ArrayList<>();
//                    sharedQueueGateTurnOn.drainTo(lstGateTurnOn);
//                    for (Object s : lstGateTurnOn) {
//                        LOGGER.info("Consummer - [GATE/REQ/DISPLAY/SENSOR] : " + s.toString());
//                        try {
//                            JSONObject jSONObject = new JSONObject(s.toString());
//                            List<Sensor> lstSensor = this.dataService.findAllSensorByGateId(jSONObject.getString("gate_id"));
//
//                            Display display = this.dataService.findDisplayByGateId(jSONObject.getString("gate_id"));
//
//                            DisplayWithSensor displayWithSensor = new DisplayWithSensor();
//                            displayWithSensor.setDisplayId(display.getSerial_number()); // serial number tra ve cho display
//                            displayWithSensor.setSensorLst(lstSensor);
//
//                            String topic = Constant.SERVER_RES_SENSOR_LIST + "/" + jSONObject.getString("gate_id");
//
//                            MqttPulisherRes mqtt = new MqttPulisherRes();
//                            mqtt.publisherGateTurnOn(topic, displayWithSensor);
//                        } catch (Exception e) {
//                            LOGGER.error(e.toString());
//                        }
//                    }
//                }
//                //------------------------------------------------------
//                if (sharedQueueDspSearchGate.size() > 0) {
//                    lstDspSearchGate = new ArrayList();
//                    sharedQueueDspSearchGate.drainTo(lstDspSearchGate);
//                    for (Object s : lstDspSearchGate) {
//                        LOGGER.info("Consummer - [DISPLAY/SERVER/SEARCH/GATE/REQ] : " + s.toString());
//                        try {
//                            JSONObject jSONObject = new JSONObject(s.toString());
//                            String displaySerial = jSONObject.getString("display_id");
//                            String gateSerial = jSONObject.getString("gate_id");
//
//                            Gate gate = this.dataService.findGateBySerialNumber(gateSerial);
//                            if (gate == null) {
//                                LOGGER.info("GATE not found");
//                                break;
//                            } else {
//                                String topic = Constant.SERVER_DISPLAY_SEARCH_GATE_RES + "/" + jSONObject.getString("display_id");
//                                MqttPulisherRes mqtt = new MqttPulisherRes();
//                                mqtt.publisherToDisplaySearchGateRes(topic, gate);
//                            }
//
//                        } catch (Exception e) {
//                            LOGGER.error(e.toString());
//                        }
//                    }
//                }
//                //------------------------------------------------------
//                if (sharedQueueDspGetGateSensor.size() > 0) {
//                    lstDspGetGateSensor = new ArrayList<>();
//                    sharedQueueDspGetGateSensor.drainTo(lstDspGetGateSensor);
//                    for (Object s : lstDspGetGateSensor) {
//                        LOGGER.info("Consummer - [DISPLAY/SERVER/GET_GATE_SENSOR_LINKED_REQ] : " + s.toString());
//                        try {
//                            JSONObject jSONObject = new JSONObject(s.toString());
//                            String displaySerial = jSONObject.getString("display_id");
//
//                            Display display = this.dataService.findDisplayBySerialNumber(displaySerial);
//                            String gateSerial = display.getGateId();
//
//                            Gate gate = this.dataService.findGateBySerialNumber(gateSerial);
//                            if (gate == null) {
//                                LOGGER.info("Gate is not linked for Display");
//                                break;
//                            }
//                            List<Sensor> lstSensor = this.dataService.findAllSensorByGateId(gateSerial);
//
//                            GateLinkedWithSenSor gateLinkedWithSenSor = new GateLinkedWithSenSor();
//                            gateLinkedWithSenSor.setGateSerial(gateSerial);
//                            gateLinkedWithSenSor.setName(gate.getName());
//                            gateLinkedWithSenSor.setModel(gate.getModel());
//                            gateLinkedWithSenSor.setManufacture(gate.getManufacture());
//                            gateLinkedWithSenSor.setFirmwareVersion(gate.getFirmwareVersion());
//                            gateLinkedWithSenSor.setLastUpdatedAt(gate.getLastUpdatedAt());
//                            gateLinkedWithSenSor.setSensorLst(lstSensor);
//
//                            String topic = Constant.SERVER_DISPLAY_GET_GATE_SENSOR_LINKED_RES + "/" + displaySerial;
//                            MqttPulisherRes mqtt = new MqttPulisherRes();
//                            mqtt.publisherToDisplayGateSensorLinked(topic, gateLinkedWithSenSor);
//
//                        } catch (Exception e) {
//                            LOGGER.error(e.toString());
//                        }
//                    }
//                }
//
//                //------------------------------------------------------
//                if (sharedQueueDspAddSensor.size() > 0) {
//                    lstDspAddSensor = new ArrayList<>();
//                    sharedQueueDspAddSensor.drainTo(lstDspAddSensor);
//                    for (Object s : lstDspAddSensor) {
//                        LOGGER.info("Consummer - [GATE/SERVER/ADD_SENSOR_REQ] : " + s.toString());
//                        try {
//                            JSONObject jSONObject = new JSONObject(s.toString());
//
//                            String displaySerial = jSONObject.getString("display_id");
//                            String gateSerial = jSONObject.getString("gate_id");
//                            String sensorMac = jSONObject.getString("mac");
//                            String nameSensor = jSONObject.getString("name");
//                            int status = Integer.parseInt(jSONObject.get("status").toString());
//                            String message = jSONObject.getString("message");
//
//                            if (status == 1) {
//                                Sensor sensor = this.dataService.findSensorByMac(sensorMac);
//                                sensor.setGateId(gateSerial);
//                                sensor.setStatus(status);
//                                sensor.setName(nameSensor);
//                                this.dataService.saveSensor(sensor);
//                            }
//
//                            Map<String, Object> map = new LinkedHashMap<>();
//                            map.put("display_id", displaySerial);
//                            map.put("gate_id", gateSerial);
//                            map.put("mac", sensorMac);
//                            map.put("name", nameSensor);
//                            map.put("status", status);
//                            map.put("message", message);
//
//                            String topic = Constant.SERVER_DISPLAY_ADD_SENSOR_GATE_RES + "/" + jSONObject.getString("display_id");
//                            MqttPulisherRes mqtt = new MqttPulisherRes();
//                            mqtt.publisherToDisplayAddSensor(topic, map);
//
//                        } catch (Exception e) {
//                            LOGGER.error(e.toString());
//                        }
//                    }
//                }
//                //------------------------------------------------------
//                if (sharedQueueDspDisConnSen.size() > 0) {
//                    lstDspDisConnSen = new ArrayList<>();
//                    sharedQueueDspDisConnSen.drainTo(lstDspDisConnSen);
//                    for (Object s : lstDspDisConnSen) {
//                        LOGGER.info("Consummer - [RES/DISCONNECT/TO/SENSOR] : " + s.toString());
//                        try {
//                            JSONObject jSONObject = new JSONObject(s.toString());
//                            String sensorMac = jSONObject.getString("sensor_id");
//                            String gateId = jSONObject.getString("gate_id");
//                            int resultCode = Integer.parseInt(jSONObject.get("result_code").toString());
//                            Sensor sensor = this.dataService.findSensorByMac(sensorMac);//sensorMac = sensorID display send
//
//                            if (sensor != null) {
//                                if (sensor.getGateId().equals(gateId) && resultCode == 1) {
//                                    sensor.setStatus(0);
//                                    this.dataService.updateStatusSensor(sensor);
//                                }
//                            } else {
//                                LOGGER.info("Sensor not found");
//                            }
//                        } catch (Exception e) {
//                            LOGGER.error(e.toString());
//                        }
//                    }
//                }
//                //------------------------------------------------------------------
//                if (sharedQueueDspConnSen.size() > 0) {
//                    lstDspConnSen = new ArrayList<>();
//                    sharedQueueDspConnSen.drainTo(lstDspConnSen);
//                    for (Object s : lstDspConnSen) {
//                        LOGGER.info("Consummer - [RES/CONNECT/TO/SENSOR] : " + s.toString());
//                        try {
//                            JSONObject jSONObject = new JSONObject(s.toString());
//                            String sensorId = jSONObject.getString("sensor_id");
//                            String gateId = jSONObject.getString("gate_id");
//                            int resultCode = Integer.parseInt(jSONObject.get("result_code").toString());
//
//                            Sensor sensor = this.dataService.findSensorByMac(sensorId); //sensorMac = sensorID display send
//                            if (sensor != null) {
//                                if (sensor.getGateId().equals(gateId) && resultCode == 1) {
//                                    sensor.setStatus(1);
//                                    this.dataService.updateStatusSensor(sensor);
//                                }
//                            }
//                        } catch (Exception e) {
//                            LOGGER.error(e.toString());
//                        }
//                    }
//                }
//
//            }
//        } catch (Exception ex) {
//            LOGGER.error(ex.toString());
//        }
//    }
//}
