package com.elcom.notify.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elcom.notify.utils.StringUtil;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.notify.bussiness.HospitalBusiness;
import com.elcom.notify.constant.Constant;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Admin
 */
public class RpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
    
    @Autowired
    private HospitalBusiness hospitalBusiness;
    
    @RabbitListener(queues = "${vs.notify.rpc.queue}${tech.env}")
    public String rpcReceive(String json) {
        try {
            LOGGER.info(" [-->] Server received request for " + json);
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.setDateFormat(df);
            RequestMessage request = mapper.readValue(json, RequestMessage.class);
            ResponseMessage response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null);
            if (request != null) {
                String requestPath = request.getRequestPath();
                String urlParam = request.getUrlParam();
                String pathParam = request.getPathParam();
                Map<String, Object> bodyParam = request.getBodyParam();
                Map<String, String> headerParam = request.getHeaderParam();

                switch (request.getRequestMethod()) {
                    case "GET":
                        if ( (Constant.SRV_VER + "/hospital/patient-by-employee").equalsIgnoreCase(requestPath) ) {
                            if ( pathParam != null && pathParam.length() > 0 )
                                response = null;
                            else
                                response = this.hospitalBusiness.findPatientByEmployee(urlParam, headerParam, "GET", requestPath);
                        }
                        else if ( (Constant.SRV_VER + "/score-management/score-info").equalsIgnoreCase(requestPath) ) {
                            if ( pathParam != null && pathParam.length() > 0 )
                                response = null;
                            else
                                response = null;
                        }
                        break;
                    case "POST":
                        if ( (Constant.SRV_VER + "/score-management/point-type").equalsIgnoreCase(requestPath) ) 
                            //response = this.hospitalBusiness.savePointType(bodyParam, headerParam, "POST", "INSERT", requestPath);
                            response = null;
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
            LOGGER.error(StringUtil.printException(ex));
        }
        return null;
    }
}
