package com.elcom.vitalsign.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elcom.vitalsign.message.RequestMessage;
import com.elcom.vitalsign.message.ResponseMessage;
import com.elcom.vitalsign.business.MeasureDataBusiness;
import com.elcom.vitalsign.constant.Constant;
import com.elcom.vitalsign.model.dto.BpDataByHealthProfile;
import com.elcom.vitalsign.model.dto.Spo2DataByHealthProfile;
import com.elcom.vitalsign.model.dto.TempDataByHealthProfile;
import com.elcom.vitalsign.utils.JSONConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Admin
 */
public class RpcServer {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
    
    @Autowired
    private MeasureDataBusiness measureBusiness;
    
    @RabbitListener(queues = "${vs.measuredata.rpc.queue}")
    public String rpcReceive(String json) {
        try {
            //LOGGER.info(" [-->] Server received request for " + json);
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.setDateFormat(df);
            
            long startTime = System.currentTimeMillis();
            //RequestMessage request = mapper.readValue(json, RequestMessage.class);
            RequestMessage request = mapper.readValue(json, new TypeReference<RequestMessage>(){});
            LOGGER.info("Elapsed readValue [{}] ms", getElapsedTime(System.currentTimeMillis() - startTime));
            
            ResponseMessage response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null);
            if (request != null) {
                String requestPath = request.getRequestPath();
                String urlParam = request.getUrlParam();
                String pathParam = request.getPathParam();
                Map<String, Object> bodyParam = request.getBodyParam();
                Map<String, String> headerParam = request.getHeaderParam();

                switch (request.getRequestMethod()) {
                    case "GET":
                        if ( (Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_BP_DATA_LATEST).equalsIgnoreCase(requestPath) ) {
                            if ( pathParam != null && pathParam.length() > 0 )
                                response = null;
                            else {
                                List<BpDataByHealthProfile> res = this.measureBusiness.findDataBpLatestByHealthProfileIdLst(urlParam);
                                LOGGER.info(" [<--] Server returned " + res);
                                return res!=null && !res.isEmpty() ? JSONConverter.toJSON(res) : "";
                            }
                        }
                        else if ( (Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_SPO2_DATA_LATEST).equalsIgnoreCase(requestPath) ) {
                            if ( pathParam != null && pathParam.length() > 0 )
                                response = null;
                            else {
                                List<Spo2DataByHealthProfile> res = this.measureBusiness.findDataSpo2LatestByHealthProfileIdLst(urlParam);
                                LOGGER.info(" [<--] Server returned " + res);
                                return res!=null && !res.isEmpty() ? JSONConverter.toJSON(res) : "";
                            }
                        }
                        else if ( (Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_TEMP_DATA_LATEST).equalsIgnoreCase(requestPath) ) {
                            if ( pathParam != null && pathParam.length() > 0 )
                                response = null;
                            else {
                                List<TempDataByHealthProfile> res = this.measureBusiness.findDataTempLatestByHealthProfileIdLst(urlParam);
                                LOGGER.info(" [<--] Server returned " + res);
                                return res!=null && !res.isEmpty() ? JSONConverter.toJSON(res) : "";
                            }
                        }
                        else if ( (Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_BP_DATA_BY_RANGE).equalsIgnoreCase(requestPath) ) {
                            if ( pathParam != null && pathParam.length() > 0 )
                                response = null;
                            else
                                response = this.measureBusiness.findDataBpWithRangeByHealthProfileId(urlParam, headerParam);
                        }
                        else if ( (Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_SPO2_DATA_BY_RANGE).equalsIgnoreCase(requestPath) ) {
                            if ( pathParam != null && pathParam.length() > 0 )
                                response = null;
                            else
                                response = this.measureBusiness.findDataSpo2WithRangeByHealthProfileId(urlParam, headerParam);
                        }
                        else if ( (Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_TEMP_DATA_BY_RANGE).equalsIgnoreCase(requestPath) ) {
                            if ( pathParam != null && pathParam.length() > 0 )
                                response = null;
                            else
                                response = this.measureBusiness.findDataTempWithRangeByHealthProfileId(urlParam, headerParam);
                        }
                        break;
                    case "POST":
                        if ( (Constant.SRV_VER + "/measure-data").equalsIgnoreCase(requestPath) ) 
                            response = this.measureBusiness.saveMeasureData(bodyParam, headerParam, "POST", requestPath);
                        else if ( (Constant.SRV_VER + "/score-management/level").equalsIgnoreCase(requestPath) ) 
                            response = null;
                        break;
                    case "PUT":
                        if ( (Constant.SRV_VER + "/score-management/point-type").equalsIgnoreCase(requestPath) ) 
                            //response = this.hospitalBusiness.savePointType(bodyParam, headerParam, "PUT", "UPDATE", requestPath);
                            response = null;
                        else if ( (Constant.SRV_VER + "/score-management/level").equalsIgnoreCase(requestPath) ) 
                            response = null;
                        break;
                    case "PATCH":
                        response = null;
                        break;
                    case "DELETE":
                        response = null;
                        break;
                    default:
                        break;
                }
            }
            LOGGER.info(" [<--] Server returned " + (response!=null ? response.toJsonString() : null));
            return response!=null ? response.toJsonString() : null;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
        return null;
    }
    
    private String getElapsedTime(long miliseconds) {
        //return (miliseconds / 1000.0) + "(s)";
        return miliseconds + " (ms)";
    }
}
