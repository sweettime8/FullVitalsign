package com.elcom.notify.bussiness;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.notify.constant.Constant;
import com.elcom.notify.controller.BaseController;
import com.elcom.notify.messaging.rabbitmq.RabbitMQClient;
import com.elcom.notify.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.notify.model.dto.DeviceInfoByPatientDTO;
import com.elcom.notify.model.dto.PatientByEmployeeDTO;
import com.elcom.notify.utils.StringUtil;
import com.elcom.notify.validation.HospitalValidation;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import com.elcom.notify.service.HospitalService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ListIterator;
import java.util.Optional;

/**
 *
 * @author anhdv
 */
@Controller
public class HospitalBusiness extends BaseController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalBusiness.class);
    
    @Autowired
    private HospitalService hospitalService;
    
    @Autowired
    private RabbitMQClient rabbitMQClient;
    
    /** Lấy danh sách bệnh nhân thuộc bác sỹ theo dõi
     * Service public để RestClient gọi qua Gateway, Ko check Auth, Ko check RBAC
     * @param urlParam (employeeId)
     * @param headerMap
     * @param methodType
     * @param requestPath
     * @return list */
    public ResponseMessage findPatientByEmployee(String urlParam, Map<String, String> headerMap, String methodType, String requestPath) {
        ResponseMessage response;
        if ( StringUtil.isNullOrEmpty(urlParam) )
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                       new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        else {
            /*AuthorizationResponseDTO auth = authenToken(headerMap);
            if( auth == null )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
            else {*/
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String employeeId = (String) params.get("employeeId");

                String validationMsg = new HospitalValidation().validateFindPatientByEmployee(employeeId);
                if( validationMsg != null )
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), validationMsg,
                               new MessageContent(HttpStatus.BAD_REQUEST.value(), validationMsg, null));
                else {
                    List<PatientByEmployeeDTO> patientLst = this.hospitalService.findPatientByEmployee(employeeId);
                    if ( patientLst==null || patientLst.isEmpty() )
                        response = new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                   new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    else {

                        String patientIdLst = "";
                        for( PatientByEmployeeDTO item : patientLst ) {
                            patientIdLst += item.getPatientId() + ",";
                        }

                        RequestMessage rpcReq = new RequestMessage("GET", Constant.SRV_VER + "/device/device-info-by-patient"
                                                                        , "patientIdLst="+patientIdLst, null, null, null);
                        String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.VS_DEVICE_RPC_EXCHANGE, 
                                        RabbitMQProperties.VS_DEVICE_RPC_QUEUE_NAME, RabbitMQProperties.VS_DEVICE_RPC_KEY, rpcReq.toJsonString());
                        if( !StringUtil.isNullOrEmpty(rpcRes) ) {
                            List<DeviceInfoByPatientDTO> deviceLst = null;
                            try {
                                deviceLst = new ObjectMapper().readValue(rpcRes, new TypeReference<List<DeviceInfoByPatientDTO>>(){});
                            } catch (Exception ex) {
                                LOGGER.error(StringUtil.printException(ex));
                            }
                            if( deviceLst!=null && !deviceLst.isEmpty() ) {
                                //List<PatientByEmployeeDTO> patientLstMore = null;
                                PatientByEmployeeDTO patient;
                                for (ListIterator<PatientByEmployeeDTO> iterator = patientLst.listIterator(); iterator.hasNext();) {
                                    patient = iterator.next();
                                //for( PatientByEmployeeDTO patient : patientLst ) {
                                    for( DeviceInfoByPatientDTO device : deviceLst ) {
                                        if( patient.getPatientId().equals(device.getPatientId()) ) {
                                            if( patient.getDisplayId()==null && patient.getGateId()==null ) { // Chưa có thì set mới
                                                try {
                                                    patient.setDisplayId(device.getDisplayId());
                                                    patient.setGateId(device.getGateId());
                                                    patient.setLstSensor(device.getSensorId());
                                                } catch (Exception ex) {
                                                    LOGGER.error(StringUtil.printException(ex));
                                                }
                                            }else if( !existsData(patientLst, device.getPatientId(), device.getDisplayId(), device.getGateId(), device.getSensorId()) ) {
                                                try {
                                                    iterator.add(new PatientByEmployeeDTO(patient.getPatientId(), patient.getPatientName(), patient.getPatientCode(), patient.getBirthDate(), patient.getGender(), device.getDisplayId(), device.getGateId(), device.getSensorId()));
                                                } catch (Exception ex) {
                                                    LOGGER.error(StringUtil.printException(ex));
                                                }
                                            }
                                            /*else if( existsData(patientLst, device.getDisplayId(), device.getGateId())!=null ) { // Đã tồn tại thì append vào sensorIdLst
                                                try {
                                                    PatientByEmployeeDTO targetItem = patientLst.get(patientLst.indexOf(containsDisplayId(patientLst, device.getDisplayId(), device.getGateId())));
                                                    targetItem.setLstSensor(patient.getLstSensor()+ "," + device.getSensorId());
                                                } catch (Exception ex) {
                                                    LOGGER.error(StringUtil.printException(ex));
                                                }
                                            }else { // Tạo thêm 1 bệnh nhân vì khác display+gate
                                                //if( patientLstMore==null )
                                                    //patientLstMore = new ArrayList<>();
                                                try {
                                                    iterator.add(new PatientByEmployeeDTO(patient.getPatientId(), patient.getPatientName(), patient.getPatientCode(), patient.getBirthDate(), patient.getGender(), device.getDisplayId(), device.getGateId(), device.getSensorId()));
                                                } catch (Exception ex) {
                                                    LOGGER.error(StringUtil.printException(ex));
                                                }
                                            }*/
                                        }
                                    }
                                }
                                /*try {
                                    if( patientLstMore!=null )
                                        patientLst.addAll(patientLstMore);
                                } catch (Exception ex) {
                                    LOGGER.error(StringUtil.printException(ex));
                                }*/
                            }
                        }
                        response = new ResponseMessage(new MessageContent(patientLst));
                    }
                }
            //}
        }
        return response;
    }
    
    public boolean existsData(List<PatientByEmployeeDTO> list, String patientId, String displayId, String gateId, String sensorId){
        try {
            Optional<PatientByEmployeeDTO> obj = list.stream().filter(o -> (o.getPatientId()!=null && o.getPatientId().equals(patientId))
                    && (o.getDisplayId()!=null && o.getDisplayId().equals(displayId))
                    && (o.getGateId()!=null && o.getGateId().equals(gateId))).findFirst();
            if( obj.isPresent() ) {
                PatientByEmployeeDTO result = obj.get();
                result.setLstSensor(result.getLstSensor()+ "," + sensorId);
                return true;
            }
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return false;
    }
}
