package com.elcom.usersystem.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elcom.usersystem.utils.StringUtil;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.usersystem.bussiness.BuSystemBusiness;
import com.elcom.usersystem.bussiness.UserSystemBusiness;
import com.elcom.usersystem.constant.Constant;
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
    private UserSystemBusiness userSystemBusiness;
    
    @Autowired
    private BuSystemBusiness buSystemBusiness;
    

    @RabbitListener(queues = "${vs.user-system.rpc.queue}")
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
                        if ((Constant.SRV_VER + "/user-system/find-profiles-by-home-admin").equalsIgnoreCase(requestPath)) //get listUserProfile by UserId
                            response = this.userSystemBusiness.findListProfileByHomeAdmin(urlParam, headerParam);
                        
                        else if((Constant.SRV_VER + "/user-system/find-profiles-by-doctor").equalsIgnoreCase(requestPath))  //get listUserProfile by UserId
                            response = this.userSystemBusiness.findHealthProfilesByDoctor(urlParam, headerParam, "GET", requestPath);
                        
                        else if((Constant.SRV_VER + "/user-system/find-details-profile").equalsIgnoreCase(requestPath))  //get listUserProfile by UserId
                            response = this.userSystemBusiness.findDetailsHealthProfile(urlParam, headerParam, "GET", requestPath);
                        
                        else if((Constant.SRV_VER + "/user-system/find-doctors-by-home-admin").equalsIgnoreCase(requestPath))  //get listUserProfile by UserId
                            response = this.userSystemBusiness.findDoctorsByHomeAdminId(urlParam, headerParam);
                        
                        else if((Constant.SRV_VER + "/user-system/find-doctor-by-doctorId").equalsIgnoreCase(requestPath))  //get list Doctor
                            response = this.buSystemBusiness.findDoctorByDoctorId(urlParam, headerParam);                        
                        else if((Constant.SRV_VER + "/user-system/find-doctors").equalsIgnoreCase(requestPath))  //get list Doctor
                            response = this.buSystemBusiness.findDoctors(urlParam, headerParam);
                        else if((Constant.SRV_VER + "/user-system/find-doctors-by-name").equalsIgnoreCase(requestPath))  //get list Doctor
                            response = this.buSystemBusiness.findDoctorsByName(urlParam, headerParam);
                        else if((Constant.SRV_VER + "/user-system/get-customer-by-bu").equalsIgnoreCase(requestPath))
                            response = this.buSystemBusiness.getCustomerByBuId(urlParam, headerParam);
                        else if((Constant.SRV_VER + "/user-system/find-customer-by-id").equalsIgnoreCase(requestPath))
                            response = this.buSystemBusiness.findCustomerById(urlParam, headerParam);
                        else if((Constant.SRV_VER + "/user-system/get-detail-customer").equalsIgnoreCase(requestPath))
                            response = this.buSystemBusiness.getDetailCustomer(urlParam, headerParam);
                        
                        break;
                    case "POST":
                        if ((Constant.SRV_VER + "/score-management/point-type").equalsIgnoreCase(requestPath)) //response = this.hospitalBusiness.savePointType(bodyParam, headerParam, "POST", "INSERT", requestPath);
                        {
                            response = null;
                        } else if ((Constant.SRV_VER + "/score-management/level").equalsIgnoreCase(requestPath)) {
                            response = null;
                        } else if ((Constant.SRV_VER + "/user-system/create-profile").equalsIgnoreCase(requestPath)) {
                            response = userSystemBusiness.createProfile(bodyParam, headerParam);
                        }else if ((Constant.SRV_VER + "/user-system/create-doctor").equalsIgnoreCase(requestPath)) {
                            response = buSystemBusiness.createDoctor(bodyParam, headerParam);
                        }else if ((Constant.SRV_VER + "/user-system/create-customer").equalsIgnoreCase(requestPath)) {
                            response = buSystemBusiness.createCustomer(bodyParam, headerParam);
                        }else if ((Constant.SRV_VER + "/user-system/add-doctor-for-customer").equalsIgnoreCase(requestPath)) {
                            response = buSystemBusiness.addDoctorForCustomer(bodyParam, headerParam);
                        }                       
                        break;
                    case "PUT":
                        if ((Constant.SRV_VER + "/score-management/point-type").equalsIgnoreCase(requestPath)) //response = this.hospitalBusiness.savePointType(bodyParam, headerParam, "PUT", "UPDATE", requestPath);
                        {
                            response = null;
                        } else if ((Constant.SRV_VER + "/score-management/level").equalsIgnoreCase(requestPath)) {
                            response = null;
                        } else if ((Constant.SRV_VER + "/user-system/edit-profile").equalsIgnoreCase(requestPath)) {
                            response = userSystemBusiness.editProfile(bodyParam, headerParam);
                        }
                         else if ((Constant.SRV_VER + "/user-system/edit-doctor").equalsIgnoreCase(requestPath)) {
                            response = buSystemBusiness.editDoctor(bodyParam, headerParam);
                        }else if ((Constant.SRV_VER + "/user-system/edit-customer").equalsIgnoreCase(requestPath)) {
                            response = buSystemBusiness.editCustomer(bodyParam, headerParam);
                        }  
                        break;
                    case "PATCH":
                        response = null;
                        break;
                    case "DELETE":
                        if ((Constant.SRV_VER + "/user-system/delete-profile").equalsIgnoreCase(requestPath)) {
                            response = userSystemBusiness.deleteProfile(urlParam, headerParam);
                        }else if ((Constant.SRV_VER + "/user-system/delete-doctor").equalsIgnoreCase(requestPath)) {
                            response = buSystemBusiness.deleteDoctor(urlParam, headerParam);
                        }else if ((Constant.SRV_VER + "/user-system/delete-customer").equalsIgnoreCase(requestPath)) {
                            response = buSystemBusiness.deleteCustomer(urlParam, headerParam);
                        }else if ((Constant.SRV_VER + "/user-system/remove-doctor-of-customer").equalsIgnoreCase(requestPath)) {
                            response = buSystemBusiness.removeDoctorOfCustomer(urlParam, headerParam);
                        }else {
                            response = null;
                        }
                        break;
                    default:
                        break;
                }
            }
            LOGGER.info(" [<--] Server returned " + (response != null ? response.toJsonString() : null));
            return response != null ? response.toJsonString() : null;
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return null;
    }
}
