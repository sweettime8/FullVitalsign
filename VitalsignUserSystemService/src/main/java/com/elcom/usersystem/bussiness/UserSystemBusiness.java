package com.elcom.usersystem.bussiness;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.usersystem.constant.Constant;
import com.elcom.usersystem.controller.BaseController;
import com.elcom.usersystem.messaging.rabbitmq.RabbitMQClient;
import com.elcom.usersystem.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.usersystem.model.Doctor;
import com.elcom.usersystem.model.HealthProfile;
import com.elcom.usersystem.model.HomeAdmin;
import com.elcom.usersystem.model.dto.AuthorizationResponseDTO;
import com.elcom.usersystem.model.dto.BpDataByHealthProfile;
import com.elcom.usersystem.model.dto.DetailsHealthProfile;
import com.elcom.usersystem.model.dto.HealthProfilesByDoctor;
import com.elcom.usersystem.model.dto.Spo2DataByHealthProfile;
import com.elcom.usersystem.model.dto.TempDataByHealthProfile;
import com.elcom.usersystem.utils.StringUtil;
import com.elcom.usersystem.validation.UserSystemValidation;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import com.elcom.usersystem.service.UserSystemService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.sql.Timestamp;

/**
 *
 * @author anhdv
 */
@Controller
public class UserSystemBusiness extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSystemBusiness.class);

    @Autowired
    private UserSystemService userSystemService;

    @Autowired
    private RabbitMQClient rabbitMQClient;

    /**
     * Lấy danh sách hồ sơ sức khỏe thuộc HomeAdmin quản lý Mobile gọi qua
     * Gateway, Check Auth, Ko check RBAC
     *
     * @param urlParam (employeeId)
     * @param headerParam
     * @return list
     */
    public ResponseMessage findListProfileByHomeAdmin(String urlParam, Map<String, String> headerParam) {
        try {
            boolean validDeviceToken = authenDeviceToken(headerParam);
            if (!validDeviceToken) {
                return new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            } //else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
            //                    return new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
            //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath))); 
            else {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String homeAdminId = (String) params.get("home-admin-id");
                String msgValidation = new UserSystemValidation().validateFindListProfileByHomeAdmin(homeAdminId);
                if (msgValidation != null) {
                    return new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.toString(),
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), msgValidation, null));
                }

                Optional<HomeAdmin> homeAdminResult = this.userSystemService.findHomeAdminById(homeAdminId);
                if (!homeAdminResult.isPresent()) {
                    return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                            new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                }

                HomeAdmin homeAdmin = homeAdminResult.get();

                List<HealthProfile> healthProfileLst = this.userSystemService.findHealthProfileByHomeAdminId(homeAdminId);

                if (healthProfileLst != null && !healthProfileLst.isEmpty()) {
                    homeAdmin.setLstHealthProfile(healthProfileLst);
                }

                return new ResponseMessage(new MessageContent(homeAdmin));
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return null;
    }

    /**
     * Lấy danh sách bác sỹ đang theo dõi HomeAdmin Mobile gọi qua Gateway,
     * Check Auth, Ko check RBAC
     *
     * @param urlParam (employeeId)
     * @param headerParam
     * @return list
     */
    public ResponseMessage findDoctorsByHomeAdminId(String urlParam, Map<String, String> headerParam) {
        try {
            boolean validDeviceToken = authenDeviceToken(headerParam);
            if (!validDeviceToken) {
                return new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            } //else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
            //                    return new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
            //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath))); 
            else {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String homeAdminId = (String) params.get("home-admin-id");
                String msgValidation = new UserSystemValidation().validateFindListProfileByHomeAdmin(homeAdminId);
                if (msgValidation != null) {
                    return new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.toString(),
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), msgValidation, null));
                }

                List<Doctor> doctorlst = this.userSystemService.findDoctorsByHomeAdminId(homeAdminId);

                if (doctorlst == null || doctorlst.isEmpty()) {
                    return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                            new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                }

                return new ResponseMessage(new MessageContent(doctorlst));
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return null;
    }

    /**
     * Lấy danh sách bệnh nhân thuộc bác sỹ đang đăng nhập theo dõi
     * Service để RestClient(WebDoctor) gọi qua Gateway, Check Auth, Ko check RBAC
     *
     * @param urlParam (doctorId, healthProfileFullName)
     * @param headerMap
     * @param methodType
     * @param requestPath
     * @return list
     */
    public ResponseMessage findHealthProfilesByDoctor(String urlParam, Map<String, String> headerMap, String methodType, String requestPath) {
        ResponseMessage response;
        /*if (StringUtil.isNullOrEmpty(urlParam)) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {*/
            AuthorizationResponseDTO auth = authenToken(headerMap);
            if (auth == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            } /*else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));*/ else {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                //String doctorAccountId = (String) params.get("doctor-account-id");
                String doctorAccountId = auth.getUuid(); // Id của bảng USER(ID Service), dựa vào đây để truy vấn vào bảng DOCTOR
                String healthProfileFullName = (String) params.get("health-profile-full-name");

                String validationMsg = new UserSystemValidation().validateFindHealthProfilesByDoctor(doctorAccountId);
                if (validationMsg != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), validationMsg,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), validationMsg, null));
                } else {
                    List<HealthProfilesByDoctor> healthProfileLst = this.userSystemService.findHealthProfilesByDoctor(doctorAccountId, healthProfileFullName);
                    if (healthProfileLst == null || healthProfileLst.isEmpty()) {
                        response = new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    } else {
                        String healthProfileIdLst = "";
                        for (HealthProfilesByDoctor healthProfilesByDoctor : healthProfileLst) {
                            healthProfileIdLst += healthProfilesByDoctor.getHealthProfileId() + ",";
                        }

                        mapBpDataLatestWithHealthProfileLst(healthProfileIdLst, healthProfileLst);
                        mapSpo2DataLatestWithHealthProfileLst(healthProfileIdLst, healthProfileLst);
                        mapTempDataLatestWithHealthProfileLst(healthProfileIdLst, healthProfileLst);

                        response = new ResponseMessage(new MessageContent(healthProfileLst));
                    }
                }
            }
        //}
        return response;
    }
    
    /**
     * Lấy chi tiết bệnh nhân thuộc bác sỹ đang đăng nhập theo dõi
     * Service để RestClient(WebDoctor) gọi qua Gateway, Check Auth, Ko check RBAC
     *
     * @param urlParam (doctorId, healthProfileFullName)
     * @param headerMap
     * @param methodType
     * @param requestPath
     * @return list
     */
    public ResponseMessage findDetailsHealthProfile(String urlParam, Map<String, String> headerMap, String methodType, String requestPath) {
        ResponseMessage response;
        if (StringUtil.isNullOrEmpty(urlParam)) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            AuthorizationResponseDTO auth = authenToken(headerMap);
            if (auth == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            } /*else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));*/ else {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String healthProfileId = (String) params.get("health-profile-id");

                String validationMsg = new UserSystemValidation().validateFindDetailsHealthProfile(healthProfileId);
                if (validationMsg != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), validationMsg,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), validationMsg, null));
                } else {
                    DetailsHealthProfile detailsHealthProfile = this.userSystemService.findDetailsHealthProfile(healthProfileId);
                    if( detailsHealthProfile == null )
                        response = new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                    new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    else {
                        String healthProfileIdLst = detailsHealthProfile.getHealthProfileId();

                        mapBpDataLatestWithHealthProfileDetail(healthProfileIdLst, detailsHealthProfile);
                        mapSpo2DataLatestWithHealthProfileDetail(healthProfileIdLst, detailsHealthProfile);
                        mapTempDataLatestWithHealthProfileDetail(healthProfileIdLst, detailsHealthProfile);

                        response = new ResponseMessage(new MessageContent(detailsHealthProfile));
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage createProfile(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            Constant.VALIDATION_INVALID_PARAM_VALUE));
        } else {
            try {
                boolean validDeviceToken = authenDeviceToken(headerParam);
                if (!validDeviceToken) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
                } //                else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                else {

                    //String id = (String) bodyParam.get("id");
                    String homeAdminId = (String) bodyParam.get("homeAdminId");
                    String fullName = (String) bodyParam.get("fullName");
                    String birthDay = (String) bodyParam.get("birthDay");
                    Integer gender = (Integer) bodyParam.get("gender");
                    float height = Float.parseFloat(bodyParam.get("height").toString());
                    float weight = Float.parseFloat(bodyParam.get("weight").toString());

                    HealthProfile healthProfile = new HealthProfile();
                    //healthProfile.setId(id);
                    healthProfile.setHomeAdminId(homeAdminId);
                    healthProfile.setFullName(fullName);
                    healthProfile.setBirthDay(birthDay);
                    healthProfile.setGender(gender);
                    healthProfile.setHeight(height);
                    healthProfile.setWeight(weight);
                    healthProfile.setActive(1);
                    healthProfile.setIsDelete(0);

                    String invalidData = new UserSystemValidation().validateInsertProfile(healthProfile);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                    } else {
                        try {
                            userSystemService.saveHealthProfile(healthProfile);
                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), new MessageContent(healthProfile));
                        } catch (Exception ex) {
                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                    new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.toString()));
                        }

                    }
                }
            } catch (Exception e) {
                LOGGER.error(StringUtil.printException(e));
            }
        }

        return response;
    }

    public ResponseMessage editProfile(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            Constant.VALIDATION_INVALID_PARAM_VALUE));
        } else {
            try {
                boolean validDeviceToken = authenDeviceToken(headerParam);
                if (!validDeviceToken) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
                } //                else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                else {

                    String id = (String) bodyParam.get("id");
                    String homeAdminId = (String) bodyParam.get("homeAdminId");
                    String fullName = (String) bodyParam.get("fullName");
                    String birthDay = (String) bodyParam.get("birthDay");
                    Integer gender = (Integer) bodyParam.get("gender");
                    float height = Float.parseFloat(bodyParam.get("height").toString());
                    float weight = Float.parseFloat(bodyParam.get("weight").toString());

                    HealthProfile healthProfile = new HealthProfile();
                    healthProfile.setId(id);
                    healthProfile.setHomeAdminId(homeAdminId);
                    healthProfile.setFullName(fullName);
                    healthProfile.setBirthDay(birthDay);
                    healthProfile.setGender(gender);
                    healthProfile.setHeight(height);
                    healthProfile.setWeight(weight);

                    String invalidData = new UserSystemValidation().validateInsertProfile(healthProfile);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                    } else {
                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        healthProfile.setLastUpdatedAt(now);

                        try {
                            userSystemService.updateHealthProfile(healthProfile);
                            response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(healthProfile));
                        } catch (Exception ex) {
                            response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(),
                                    new MessageContent(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(), ex.toString()));
                        }

                    }
                }
            } catch (Exception e) {
                LOGGER.error(StringUtil.printException(e));
            }
        }

        return response;
    }

    public ResponseMessage deleteProfile(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        try {
            boolean validDeviceToken = authenDeviceToken(headerParam);
            if (!validDeviceToken) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            } //                else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
            //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
            //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
            else {

                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String id = (String) params.get("id");
                HealthProfile healthProfile = userSystemService.findHealthProfileById(id);

                if (healthProfile == null) {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                } else {
                    userSystemService.deleteHealthProfile(healthProfile);
                    response = new ResponseMessage(new MessageContent(id));
                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }

        return response;
    }

    private void mapBpDataLatestWithHealthProfileLst(String healthProfileIdLst, List<HealthProfilesByDoctor> healthProfileLst) {
        RequestMessage rpcReqBp = new RequestMessage("GET", Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_BP_DATA_LATEST,
                 "healthProfileIdLst=" + healthProfileIdLst, null, null, null);
        String rpcResBp = rabbitMQClient.callRpcService(RabbitMQProperties.VS_MEASURE_DATA_RPC_EXCHANGE,
                RabbitMQProperties.VS_MEASURE_DATA_RPC_QUEUE_NAME, RabbitMQProperties.VS_MEASURE_DATA_RPC_KEY, rpcReqBp.toJsonString());
        if (!StringUtil.isNullOrEmpty(rpcResBp)) {
            List<BpDataByHealthProfile> bpLatestLst = null;
            try {
//                bpLatestLst = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
//                        .readValue(rpcResBp, new TypeReference<List<BpDataLatestByHealthProfile>>() {
//                        });
                  bpLatestLst = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                        .readValue(rpcResBp, new TypeReference<List<BpDataByHealthProfile>>() {
                        });
            } catch (Exception ex) {
                LOGGER.error(StringUtil.printException(ex));
            }
            if (bpLatestLst != null && !bpLatestLst.isEmpty()) {
                for (HealthProfilesByDoctor healthProfile : healthProfileLst) {
                    for (BpDataByHealthProfile bpDataLatest : bpLatestLst) {
                        if (healthProfile.getHealthProfileId().equals(bpDataLatest.getHealthProfileId())) {
                            healthProfile.setBpDataLatest(new BpDataByHealthProfile(
                                    bpDataLatest.getSys(), bpDataLatest.getDia(), bpDataLatest.getMap(),
                                     bpDataLatest.getPr(), bpDataLatest.getTs(), bpDataLatest.getLastUpdatedAt()
                            ));
                        }
                    }
                }
            }
        }
    }

    private void mapSpo2DataLatestWithHealthProfileLst(String healthProfileIdLst, List<HealthProfilesByDoctor> healthProfileLst) {
        RequestMessage rpcReq = new RequestMessage("GET", Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_SPO2_DATA_LATEST,
                 "healthProfileIdLst=" + healthProfileIdLst, null, null, null);
        String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.VS_MEASURE_DATA_RPC_EXCHANGE,
                RabbitMQProperties.VS_MEASURE_DATA_RPC_QUEUE_NAME, RabbitMQProperties.VS_MEASURE_DATA_RPC_KEY, rpcReq.toJsonString());
        if (!StringUtil.isNullOrEmpty(rpcRes)) {
            List<Spo2DataByHealthProfile> spo2LatestLst = null;
            try {
                spo2LatestLst = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                        .readValue(rpcRes, new TypeReference<List<Spo2DataByHealthProfile>>() {
                        });
            } catch (Exception ex) {
                LOGGER.error(StringUtil.printException(ex));
            }
            if (spo2LatestLst != null && !spo2LatestLst.isEmpty()) {
                for (HealthProfilesByDoctor healthProfile : healthProfileLst) {
                    for (Spo2DataByHealthProfile spo2DataLatest : spo2LatestLst) {
                        if (healthProfile.getHealthProfileId().equals(spo2DataLatest.getHealthProfileId())) {
                            healthProfile.setSpo2DataLatest(new Spo2DataByHealthProfile(
                                    spo2DataLatest.getSpo2(), spo2DataLatest.getPi(), spo2DataLatest.getPr(), spo2DataLatest.getStep(),
                                     spo2DataLatest.getTs(), spo2DataLatest.getLastUpdatedAt()
                            ));
                        }
                    }
                }
            }
        }
    }

    private void mapTempDataLatestWithHealthProfileLst(String healthProfileIdLst, List<HealthProfilesByDoctor> healthProfileLst) {
        RequestMessage rpcReq = new RequestMessage("GET", Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_TEMP_DATA_LATEST,
                 "healthProfileIdLst=" + healthProfileIdLst, null, null, null);
        String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.VS_MEASURE_DATA_RPC_EXCHANGE,
                RabbitMQProperties.VS_MEASURE_DATA_RPC_QUEUE_NAME, RabbitMQProperties.VS_MEASURE_DATA_RPC_KEY, rpcReq.toJsonString());
        if (!StringUtil.isNullOrEmpty(rpcRes)) {
            List<TempDataByHealthProfile> tempLatestLst = null;
            try {
                tempLatestLst = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                        .readValue(rpcRes, new TypeReference<List<TempDataByHealthProfile>>() {
                        });
            } catch (Exception ex) {
                LOGGER.error(StringUtil.printException(ex));
            }
            if (tempLatestLst != null && !tempLatestLst.isEmpty()) {
                for (HealthProfilesByDoctor healthProfile : healthProfileLst) {
                    for (TempDataByHealthProfile tempDataLatest : tempLatestLst) {
                        if (healthProfile.getHealthProfileId().equals(tempDataLatest.getHealthProfileId())) {
                            healthProfile.setTempDataLatest(new TempDataByHealthProfile(
                                    tempDataLatest.getTemp(), tempDataLatest.getTs(), tempDataLatest.getLastUpdatedAt()
                            ));
                        }
                    }
                }
            }
        }
    }
    
    private void mapBpDataLatestWithHealthProfileDetail(String healthProfileIdLst, DetailsHealthProfile healthProfileDetail) {
        RequestMessage rpcReqBp = new RequestMessage("GET", Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_BP_DATA_LATEST,
                 "healthProfileIdLst=" + healthProfileIdLst, null, null, null);
        String rpcResBp = rabbitMQClient.callRpcService(RabbitMQProperties.VS_MEASURE_DATA_RPC_EXCHANGE,
                RabbitMQProperties.VS_MEASURE_DATA_RPC_QUEUE_NAME, RabbitMQProperties.VS_MEASURE_DATA_RPC_KEY, rpcReqBp.toJsonString());
        if (!StringUtil.isNullOrEmpty(rpcResBp)) {
            List<BpDataByHealthProfile> bpLatestLst = null;
            try {
                bpLatestLst = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                        .readValue(rpcResBp, new TypeReference<List<BpDataByHealthProfile>>() {
                        });
            } catch (Exception ex) {
                LOGGER.error(StringUtil.printException(ex));
            }
            if (bpLatestLst != null && !bpLatestLst.isEmpty()) {
                for (BpDataByHealthProfile bpDataLatest : bpLatestLst) {
                    if (healthProfileDetail.getHealthProfileId().equals(bpDataLatest.getHealthProfileId())) {
                        healthProfileDetail.setBpDataLatest(new BpDataByHealthProfile(
                                bpDataLatest.getSys(), bpDataLatest.getDia(), bpDataLatest.getMap(),
                                 bpDataLatest.getPr(), bpDataLatest.getTs(), bpDataLatest.getLastUpdatedAt()
                        ));
                    }
                }
            }
        }
    }

    private void mapSpo2DataLatestWithHealthProfileDetail(String healthProfileIdLst, DetailsHealthProfile healthProfileDetail) {
        RequestMessage rpcReq = new RequestMessage("GET", Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_SPO2_DATA_LATEST,
                 "healthProfileIdLst=" + healthProfileIdLst, null, null, null);
        String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.VS_MEASURE_DATA_RPC_EXCHANGE,
                RabbitMQProperties.VS_MEASURE_DATA_RPC_QUEUE_NAME, RabbitMQProperties.VS_MEASURE_DATA_RPC_KEY, rpcReq.toJsonString());
        if (!StringUtil.isNullOrEmpty(rpcRes)) {
            List<Spo2DataByHealthProfile> spo2LatestLst = null;
            try {
                spo2LatestLst = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                        .readValue(rpcRes, new TypeReference<List<Spo2DataByHealthProfile>>() {
                        });
            } catch (Exception ex) {
                LOGGER.error(StringUtil.printException(ex));
            }
            if (spo2LatestLst != null && !spo2LatestLst.isEmpty()) {
                for (Spo2DataByHealthProfile spo2DataLatest : spo2LatestLst) {
                    if (healthProfileDetail.getHealthProfileId().equals(spo2DataLatest.getHealthProfileId())) {
                        healthProfileDetail.setSpo2DataLatest(new Spo2DataByHealthProfile(
                                spo2DataLatest.getSpo2(), spo2DataLatest.getPi(), spo2DataLatest.getPr(), spo2DataLatest.getStep(),
                                 spo2DataLatest.getTs(), spo2DataLatest.getLastUpdatedAt()
                        ));
                    }
                }
            }
        }
    }

    private void mapTempDataLatestWithHealthProfileDetail(String healthProfileIdLst, DetailsHealthProfile healthProfileDetail) {
        RequestMessage rpcReq = new RequestMessage("GET", Constant.SRV_VER + Constant.API_MEASURE_DATA_FIND_TEMP_DATA_LATEST,
                 "healthProfileIdLst=" + healthProfileIdLst, null, null, null);
        String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.VS_MEASURE_DATA_RPC_EXCHANGE,
                RabbitMQProperties.VS_MEASURE_DATA_RPC_QUEUE_NAME, RabbitMQProperties.VS_MEASURE_DATA_RPC_KEY, rpcReq.toJsonString());
        if (!StringUtil.isNullOrEmpty(rpcRes)) {
            List<TempDataByHealthProfile> tempLatestLst = null;
            try {
                tempLatestLst = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                        .readValue(rpcRes, new TypeReference<List<TempDataByHealthProfile>>() {
                        });
            } catch (Exception ex) {
                LOGGER.error(StringUtil.printException(ex));
            }
            if (tempLatestLst != null && !tempLatestLst.isEmpty()) {
                for (TempDataByHealthProfile tempDataLatest : tempLatestLst) {
                    if (healthProfileDetail.getHealthProfileId().equals(tempDataLatest.getHealthProfileId())) {
                        healthProfileDetail.setTempDataLatest(new TempDataByHealthProfile(
                                tempDataLatest.getTemp(), tempDataLatest.getTs(), tempDataLatest.getLastUpdatedAt()
                        ));
                    }
                }
            }
        }
    }
}
