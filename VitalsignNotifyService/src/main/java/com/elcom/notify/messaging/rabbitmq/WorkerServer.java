package com.elcom.notify.messaging.rabbitmq;

import com.elcom.gateway.message.RequestMessage;
import com.elcom.notify.utils.StringUtil;
import com.elcom.notify.bussiness.HospitalBusiness;
import com.elcom.notify.constant.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author anhdv
 */
public class WorkerServer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerServer.class);
    
    @Autowired
    private HospitalBusiness hospitalBusiness;
    
    @RabbitListener(queues = "${vs.notify.worker.queue}${tech.env}")
    public void workerReceive(String json) throws InterruptedException, IOException {
        try {
            LOGGER.info(" [-->] Worker server received request for " + json);
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.setDateFormat(df);
            RequestMessage request = mapper.readValue(json, RequestMessage.class);
            
            //Process here
            if (request != null) {
                String requestPath = request.getRequestPath();
                //String urlParam = request.getUrlParam();
                //String pathParam = request.getPathParam();
                Map<String, Object> bodyParam = request.getBodyParam();
                //Map<String, String> headerParam = request.getHeaderParam();
                
                switch (request.getRequestMethod()) {
                    case "GET":
                        break;
                    case "POST":
                        if ( (Constant.SRV_VER + "/score-management/action/progress").equalsIgnoreCase(requestPath) )
                            //this.scoreManagementBusiness.actionProgress(bodyParam);
                            ;
                        else if ( (Constant.SRV_VER + "/score-management/init-user-score").equalsIgnoreCase(requestPath) )
                            //this.scoreManagementBusiness.initUserScore(bodyParam);
                            ;
                        break;
                    case "DELETE":
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
    }
}
