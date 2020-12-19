/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.usersystem.bussiness;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.usersystem.constant.Constant;
import com.elcom.usersystem.controller.BaseController;
import com.elcom.usersystem.messaging.rabbitmq.RabbitMQClient;
import com.elcom.usersystem.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.usersystem.model.Doctor;
import com.elcom.usersystem.model.DoctorHomeAdminMaps;
import com.elcom.usersystem.model.HomeAdmin;
import com.elcom.usersystem.model.dto.AuthorizationResponseDTO;
import com.elcom.usersystem.model.dto.HomeAdminDTO;
import com.elcom.usersystem.model.dto.HomeAdminDeviceCountDTO;
import com.elcom.usersystem.model.dto.HomeAdminViewDTO;
import com.elcom.usersystem.service.UserSystemService;
import com.elcom.usersystem.utils.StringUtil;
import com.elcom.usersystem.validation.UserSystemValidation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

/**
 *
 * @author ducnh
 */
@Controller
public class BuSystemBusiness extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuSystemBusiness.class);

    @Autowired
    private UserSystemService userSystemService;

    @Autowired
    private RabbitMQClient rabbitMQClient;

    public ResponseMessage removeDoctorOfCustomer(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {

            AuthorizationResponseDTO dto = authenToken(headerParam);
            if (dto == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn chưa đăng nhập"));
            } else {
                //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String customerId = (String) params.get("customerId");
                String doctorId = (String) params.get("doctorId");

                DoctorHomeAdminMaps doctorHomeAdminMaps = userSystemService.findDoctorPairHomeAdmin(customerId, doctorId);
                if (doctorHomeAdminMaps == null) {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                } else {

                    userSystemService.removeDoctorPairCustomer(doctorHomeAdminMaps.getId());

                    response = new ResponseMessage(new MessageContent(doctorHomeAdminMaps));
                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return response;
    }

    public ResponseMessage addDoctorForCustomer(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            Constant.VALIDATION_INVALID_PARAM_VALUE));
        } else {
            try {
                //authen
                AuthorizationResponseDTO dto = authenToken(headerParam);
                if (dto == null) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    "Bạn chưa đăng nhập"));
                } else {
                    //RBAC
                    // if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                    //    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    //               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));

                    String doctorId = (String) bodyParam.get("doctorId");
                    String customerId = (String) bodyParam.get("customerId");

                    DoctorHomeAdminMaps doctorHomeAdminMaps = new DoctorHomeAdminMaps();
                    doctorHomeAdminMaps.setDoctorId(doctorId);
                    doctorHomeAdminMaps.setHomeAdminId(customerId);

                    String invalidData = new UserSystemValidation().validateAddDoctorForCustomer(doctorHomeAdminMaps);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                    } else {
                        try {
                            userSystemService.addDoctorForCustomer(doctorHomeAdminMaps);

                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), new MessageContent(doctorHomeAdminMaps));
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

    public ResponseMessage deleteCustomer(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {

            AuthorizationResponseDTO dto = authenToken(headerParam);
            if (dto == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn chưa đăng nhập"));
            } else {
                //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String id = (String) params.get("id");
                HomeAdmin ha = userSystemService.findCustomerById(id);
                if (ha == null) {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                } else {
                    String accountId = ha.getAccountId();

                    Map<String, String> headerMap = new HashMap<>();
                    headerMap.put("authorization", headerParam.get("authorization"));

                    RequestMessage rpcReq = new RequestMessage("DELETE", Constant.SRV_VER + "/user", null, accountId, null, headerMap);
                    String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                            RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rpcReq.toJsonString());

                    if (!StringUtil.isNullOrEmpty(rpcRes)) {
                        userSystemService.deleteCustomer(id);
                        userSystemService.deleteHealthProfileOfCustomer(id);
                    }
                    response = new ResponseMessage(new MessageContent(ha));
                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return response;
    }

    public ResponseMessage editCustomer(Map<String, Object> bodyParam, Map<String, String> headerParam) throws JsonProcessingException {
        ResponseMessage response = null;
        try {

            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                Constant.VALIDATION_INVALID_PARAM_VALUE));
            } else {
                //authen
                AuthorizationResponseDTO dto = authenToken(headerParam);
                if (dto == null) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    "Bạn chưa đăng nhập"));
                } else {

                    //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                    //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                    String id = (String) bodyParam.get("id");
                    String fullName = (String) bodyParam.get("fullName");
                    String birthDay = (String) bodyParam.get("birthDay");
                    int gender = (Integer) bodyParam.get("gender");
                    String phoneNumber = (String) bodyParam.get("phoneNumber");
                    String email = (String) bodyParam.get("email");
                    String additional = (String) bodyParam.get("additional");

                    HomeAdmin ha = this.userSystemService.findCustomerById(id);
                    ha.setFullName(fullName);
                    ha.setPhoneNumber(phoneNumber);
                    ha.setEmail(email);
                    ha.setAdditional(additional);

                    String invalidData = new UserSystemValidation().validateUpdateCustomer(ha);

                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                    } else {
                        try {

                            //call rpc to Id service update avartar
                            Map<String, Object> bodySend = new HashMap<>();
                            bodySend.put("uuid", ha.getAccountId());
                            bodySend.put("email", ha.getEmail());
                            bodySend.put("fullName", fullName);
                            bodySend.put("gender", gender);
                            bodySend.put("mobile", phoneNumber);
                            bodySend.put("birthDay", birthDay);

                            Map<String, String> headerMap = new HashMap<>();
                            headerMap.put("authorization", headerParam.get("authorization"));

                            RequestMessage rpcReq = new RequestMessage("PUT", Constant.SRV_VER + "/user/update-customer", null, null, bodySend, headerMap);
                            String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                                    RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rpcReq.toJsonString());

                            if (!StringUtil.isNullOrEmpty(rpcRes)) {
                                ObjectMapper mapper = new ObjectMapper();
                                ResponseMessage res = null;
                                try {
                                    res = mapper.readValue(rpcRes, ResponseMessage.class);
                                    if (res != null) {
                                        JsonNode jsonNode = mapper.readTree(rpcRes);
                                        AuthorizationResponseDTO authorizationResponseDTO = mapper.treeToValue(jsonNode.get("data").get("data"),
                                                AuthorizationResponseDTO.class);

                                        userSystemService.updateCustomer(ha);
                                    }
                                } catch (JsonProcessingException ex) {
                                    LOGGER.info("Lỗi parse json khi gọi user service verify: " + ex.getCause().toString());
                                    return null;
                                }

                            }
                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), new MessageContent(ha));
                        } catch (Exception ex) {
                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                    new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.toString()));
                        }

                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }

        return response;
    }

    public ResponseMessage findCustomerById(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {

            AuthorizationResponseDTO dto = authenToken(headerParam);
            if (dto == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn chưa đăng nhập"));
            } else {
                //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String id = (String) params.get("id");
                String msgValidation = new UserSystemValidation().validateFindCustomerById(id);
                if (msgValidation != null) {
                    return new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.toString(),
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), msgValidation, null));
                }

                HomeAdmin homeAdmin = this.userSystemService.findCustomerById(id);

                if (homeAdmin == null) {
                    return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                            new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                }

                HomeAdminViewDTO havdto = new HomeAdminViewDTO();
                havdto.setId(id);
                havdto.setAccountId(homeAdmin.getAccountId());
                havdto.setBusinessUnitId(homeAdmin.getBusinessUnitId());
                havdto.setFullName(homeAdmin.getFullName());
                havdto.setPhoneNumber(homeAdmin.getPhoneNumber());
                havdto.setAdditional(homeAdmin.getAdditional());
                havdto.setEmail(homeAdmin.getEmail());

                //call rpc to Id service get data
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("authorization", headerParam.get("authorization"));

                RequestMessage rpcReq = new RequestMessage("GET", Constant.SRV_VER + "/user", null, homeAdmin.getAccountId(), null, headerMap);
                String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                        RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rpcReq.toJsonString());

                if (!StringUtil.isNullOrEmpty(rpcRes)) {
                    ObjectMapper mapper = new ObjectMapper();
                    ResponseMessage res = null;
                    try {
                        res = mapper.readValue(rpcRes, ResponseMessage.class);
                        if (res != null) {
                            JsonNode jsonNode = mapper.readTree(rpcRes);
                            AuthorizationResponseDTO authorizationResponseDTO = mapper.treeToValue(jsonNode.get("data").get("data"),
                                    AuthorizationResponseDTO.class);

                            havdto.setGender(authorizationResponseDTO.getGender());
                            havdto.setBirthDay(authorizationResponseDTO.getBirthDay());
                        }
                    } catch (JsonProcessingException ex) {
                        LOGGER.info("Lỗi parse json khi gọi user service verify: " + ex.getCause().toString());
                        return null;
                    }

                }

                return new ResponseMessage(new MessageContent(havdto));
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return response;
    }

    public ResponseMessage createCustomer(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            Constant.VALIDATION_INVALID_PARAM_VALUE));
        } else {
            try {
                //authen
                AuthorizationResponseDTO dto = authenToken(headerParam);
                if (dto == null) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    "Bạn chưa đăng nhập"));
                } else {
                    //RBAC
                    // if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                    //    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    //               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));

                    String buId = (String) bodyParam.get("businessUnitId");
                    String fullName = (String) bodyParam.get("fullName");
                    String birthDay = (String) bodyParam.get("birthDay");
                    int gender = (Integer) bodyParam.get("gender");
                    String phoneNumber = (String) bodyParam.get("phoneNumber");
                    String email = (String) bodyParam.get("email");
                    String password = StringUtil.generateRandomString(8);
                    String additional = (String) bodyParam.get("additional");

                    HomeAdmin ha = new HomeAdmin();
                    ha.setBusinessUnitId(buId);
                    ha.setFullName(fullName);
                    ha.setPhoneNumber(phoneNumber);
                    ha.setEmail(email);
                    ha.setAdditional(additional);

                    String invalidData = new UserSystemValidation().validateAddCustomer(ha);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                    } else {
                        try {

                            Map<String, Object> bodySend = new HashMap<>();
                            bodySend.put("userName", email);
                            bodySend.put("email", email);
                            bodySend.put("mobile", phoneNumber);
                            bodySend.put("fullName", fullName);
                            bodySend.put("password", password);
                            bodySend.put("gender", gender);
                            bodySend.put("signupType", null);
                            bodySend.put("address", null);
                            bodySend.put("fbId", null);
                            bodySend.put("ggId", null);

                            RequestMessage rpcReq = new RequestMessage("POST", Constant.SRV_VER + "/user", null, null, bodySend, null);
                            String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                                    RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rpcReq.toJsonString());

                            if (!StringUtil.isNullOrEmpty(rpcRes)) {
                                ObjectMapper mapper = new ObjectMapper();
                                ResponseMessage res = null;
                                try {
                                    res = mapper.readValue(rpcRes, ResponseMessage.class);
                                    if (res != null && res.getStatus() == HttpStatus.CREATED.value()) {
                                        JsonNode jsonNode = mapper.readTree(rpcRes);
                                        AuthorizationResponseDTO authorizationResponseDTO = mapper.treeToValue(jsonNode.get("data").get("data"),
                                                AuthorizationResponseDTO.class);

                                        ha.setAccountId(authorizationResponseDTO.getUuid());
                                        userSystemService.addCustomer(ha);

                                        String emailTo = ha.getEmail();
                                        String title = "Register success";
                                        String content = "password : " + password;
                                        String sign = "Elcom2020@123456";

                                        Map<String, Object> bodySendMail = new HashMap<>();
                                        bodySendMail.put("emailTo", emailTo);
                                        bodySendMail.put("title", title);
                                        bodySendMail.put("content", content);
                                        bodySendMail.put("sign", sign);
                                        RequestMessage rpcReqMail = new RequestMessage("POST", Constant.SRV_VER + "/user/sendEmail", null, null, bodySendMail, null);
                                        String rpcReqMailRes = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                                                RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rpcReqMail.toJsonString());
                                        LOGGER.info("rpcReqMailRes : " + rpcReqMailRes);

                                    } else {
                                        return res;
                                    }
                                } catch (JsonProcessingException ex) {
                                    LOGGER.info("Lỗi parse json khi gọi user service verify: " + ex.getCause().toString());
                                    return null;
                                }
                            }

                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), new MessageContent(ha));
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

    public ResponseMessage createDoctor(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            Constant.VALIDATION_INVALID_PARAM_VALUE));
        } else {
            try {
                //authen
                AuthorizationResponseDTO dto = authenToken(headerParam);
                if (dto == null) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    "Bạn chưa đăng nhập"));
                } else {

                    // if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                    //    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    //               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                    String buId = (String) bodyParam.get("businessUnitId");
                    String email = (String) bodyParam.get("email");
                    String fullName = (String) bodyParam.get("fullName");
                    String specialist = (String) bodyParam.get("specialist");
                    String avatar = (String) bodyParam.get("avatar");
                    String password = StringUtil.generateRandomString(8);
                    String additional = (String) bodyParam.get("additional");

                    Doctor doctor = new Doctor();
                    doctor.setBusinessUnitId(buId);
                    doctor.setFullName(fullName);
                    doctor.setEmail(email);
                    doctor.setSpecialist(specialist);
                    doctor.setAdditional(additional);
                    doctor.setAvatar(avatar);

                    String invalidData = new UserSystemValidation().validateAddDoctor(doctor);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                    } else {
                        try {

                            Map<String, Object> bodySend = new HashMap<>();
                            bodySend.put("userName", email);
                            bodySend.put("email", email);
                            bodySend.put("mobile", null);
                            bodySend.put("fullName", fullName);
                            bodySend.put("password", password);
                            bodySend.put("signupType", null);
                            bodySend.put("address", null);
                            bodySend.put("fbId", null);
                            bodySend.put("ggId", null);
                            bodySend.put("avatar", avatar);

                            RequestMessage rpcReq = new RequestMessage("POST", Constant.SRV_VER + "/user", null, null, bodySend, null);
                            String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                                    RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rpcReq.toJsonString());

                            if (!StringUtil.isNullOrEmpty(rpcRes)) {
                                ObjectMapper mapper = new ObjectMapper();
                                ResponseMessage res = null;
                                try {
                                    res = mapper.readValue(rpcRes, ResponseMessage.class);
                                    if (res != null && res.getStatus() == HttpStatus.CREATED.value()) {
                                        JsonNode jsonNode = mapper.readTree(rpcRes);
                                        AuthorizationResponseDTO authorizationResponseDTO = mapper.treeToValue(jsonNode.get("data").get("data"),
                                                AuthorizationResponseDTO.class);

                                        doctor.setAccountId(authorizationResponseDTO.getUuid());
                                        userSystemService.addDoctorForBu(doctor);

                                        String emailTo = doctor.getEmail();
                                        String title = "Register success";
                                        String content = "password : " + password;
                                        String sign = "Elcom2020@123456";

                                        Map<String, Object> bodySendMail = new HashMap<>();
                                        bodySendMail.put("emailTo", emailTo);
                                        bodySendMail.put("title", title);
                                        bodySendMail.put("content", content);
                                        bodySendMail.put("sign", sign);
                                        RequestMessage rpcReqMail = new RequestMessage("POST", Constant.SRV_VER + "/user/sendEmail", null, null, bodySendMail, null);
                                        String rpcReqMailRes = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                                                RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rpcReqMail.toJsonString());
                                        LOGGER.info("rpcReqMailRes : " + rpcReqMailRes);

                                    } else {
                                        return res;
                                    }
                                } catch (JsonProcessingException ex) {
                                    LOGGER.info("Lỗi parse json khi gọi user service verify: " + ex.getCause().toString());
                                    return null;
                                }
                            }

                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), new MessageContent(doctor));
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

    public ResponseMessage editDoctor(Map<String, Object> bodyParam, Map<String, String> headerParam) throws JsonProcessingException {
        ResponseMessage response = null;
        try {

            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                Constant.VALIDATION_INVALID_PARAM_VALUE));
            } else {
                //authen
                AuthorizationResponseDTO dto = authenToken(headerParam);
                if (dto == null) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    "Bạn chưa đăng nhập"));
                } else {

                    //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                    //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                    String id = (String) bodyParam.get("id");
                    String fullName = (String) bodyParam.get("fullName");
                    String specialist = (String) bodyParam.get("specialist");
                    String avatar = (String) bodyParam.get("avatar");
                    String additional = (String) bodyParam.get("additional");

                    Doctor doctor = this.userSystemService.findDoctorByDoctorId(id);
                    doctor.setFullName(fullName);
                    doctor.setSpecialist(specialist);
                    doctor.setAdditional(additional);
                    doctor.setAvatar(avatar);

                    String invalidData = new UserSystemValidation().validateUpdateDoctor(doctor);

                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                    } else {
                        try {

                            //call rpc to Id service update avartar
                            Map<String, Object> bodySend = new HashMap<>();
                            bodySend.put("uuid", doctor.getAccountId());
                            bodySend.put("avatar", avatar);
                            bodySend.put("fullName", fullName);

                            Map<String, String> headerMap = new HashMap<>();
                            headerMap.put("authorization", headerParam.get("authorization"));

                            RequestMessage rpcReq = new RequestMessage("PUT", Constant.SRV_VER + "/user/update-doctor", null, null, bodySend, headerMap);
                            String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                                    RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rpcReq.toJsonString());

                            if (!StringUtil.isNullOrEmpty(rpcRes)) {
                                ObjectMapper mapper = new ObjectMapper();
                                ResponseMessage res = null;
                                try {
                                    res = mapper.readValue(rpcRes, ResponseMessage.class);
                                    if (res != null) {
                                        JsonNode jsonNode = mapper.readTree(rpcRes);
                                        AuthorizationResponseDTO authorizationResponseDTO = mapper.treeToValue(jsonNode.get("data").get("data"),
                                                AuthorizationResponseDTO.class);

                                        userSystemService.updateDoctor(doctor);
                                    }
                                } catch (JsonProcessingException ex) {
                                    LOGGER.info("Lỗi parse json khi gọi user service verify: " + ex.getCause().toString());
                                    return null;
                                }

                            }
                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), new MessageContent(doctor));
                        } catch (Exception ex) {
                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                    new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.toString()));
                        }

                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }

        return response;
    }

    public ResponseMessage deleteDoctor(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {

            AuthorizationResponseDTO dto = authenToken(headerParam);
            if (dto == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn chưa đăng nhập"));
            } else {
                //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String id = (String) params.get("doctorId");
                Doctor doctor = userSystemService.findDoctorByDoctorId(id);
                if (doctor == null) {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                } else {
                    String accountId = doctor.getAccountId();

                    Map<String, String> headerMap = new HashMap<>();
                    headerMap.put("authorization", headerParam.get("authorization"));

                    RequestMessage rpcReq = new RequestMessage("DELETE", Constant.SRV_VER + "/user", null, accountId, null, headerMap);
                    String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                            RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rpcReq.toJsonString());

                    userSystemService.deleteDoctor(id);
                    response = new ResponseMessage(new MessageContent(doctor));
                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return response;
    }

    public ResponseMessage findDoctorByDoctorId(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {

            AuthorizationResponseDTO dto = authenToken(headerParam);
            if (dto == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn chưa đăng nhập"));
            } else {
                //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String doctorId = (String) params.get("doctorId");
                String msgValidation = new UserSystemValidation().validateFindDoctorByDoctorId(doctorId);
                if (msgValidation != null) {
                    return new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.toString(),
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), msgValidation, null));
                }

                Doctor doctor = this.userSystemService.findDoctorByDoctorId(doctorId);

                if (doctor == null) {
                    return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                            new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                }

                return new ResponseMessage(new MessageContent(doctor));
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return response;
    }

    public ResponseMessage findDoctors(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {
            //authen
            AuthorizationResponseDTO dto = authenToken(headerParam);
            if (dto == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn chưa đăng nhập"));
            } else {

                //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String buId = (String) params.get("businessUnitId");
                String specialist = (String) params.get("specialist");
                String fullName = (String) params.get("fullName");

                String msgValidation = new UserSystemValidation().validateFindDoctors(specialist, fullName);
                if (msgValidation != null) {
                    return new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.toString(),
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), msgValidation, null));
                }

                List<Doctor> doctorlst = this.userSystemService.findDoctors(buId, specialist, fullName);

                if (doctorlst == null || doctorlst.isEmpty()) {
                    return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                            new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                }

                return new ResponseMessage(new MessageContent(doctorlst));
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return response;
    }

    public ResponseMessage findDoctorsByName(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {
            //authen
            AuthorizationResponseDTO dto = authenToken(headerParam);
            if (dto == null) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn chưa đăng nhập"));
            } else {

                //            if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String buId = (String) params.get("businessUnitId");
                String fullName = (String) params.get("fullName");

                List<Doctor> doctorlst = this.userSystemService.findDoctorsbyName(buId, fullName);

                if (doctorlst == null || doctorlst.isEmpty()) {
                    return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                            new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                }

                return new ResponseMessage(new MessageContent(doctorlst));
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return response;
    }

    public ResponseMessage getCustomerByBuId(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {
            if (StringUtil.isNullOrEmpty(urlParam)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                AuthorizationResponseDTO dto = authenToken(headerParam);
                if (dto == null) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    "Bạn chưa đăng nhập"));
                } else {
                    Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                    String buid = (String) params.get("buId");
                    List<HomeAdminDTO> lstHomeAdminDTO = this.userSystemService.findCustomerByBuId(buid);

                    if (lstHomeAdminDTO == null) {
                        return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    }

                    //call rpc to device service get countDevice For Customer
                    Map<String, String> headerMap = new HashMap<>();
                    headerMap.put("authorization", headerParam.get("authorization"));

                    String homeAdminIdLst = "";
                    for (HomeAdminDTO homeAdminDTO : lstHomeAdminDTO) {
                        homeAdminIdLst += homeAdminDTO.getId() + ",";
                    }
                    RequestMessage rpcReq = new RequestMessage("GET", Constant.SRV_VER + "/device/count-gate-by-ha-id",
                            "homeAdminIdLst=" + homeAdminIdLst, null, null, headerMap);
                    String rpcRes = rabbitMQClient.callRpcService(RabbitMQProperties.VS_DEVICE_RPC_EXCHANGE,
                            RabbitMQProperties.VS_DEVICE_RPC_QUEUE_NAME, RabbitMQProperties.VS_DEVICE_RPC_KEY, rpcReq.toJsonString());

                    if (!StringUtil.isNullOrEmpty(rpcRes)) {
                        List<HomeAdminDeviceCountDTO> lstHaDeviceCountDTO = null;
                        try {
                            lstHaDeviceCountDTO = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                                    .readValue(rpcRes, new TypeReference<List<HomeAdminDeviceCountDTO>>() {
                                    });
                        } catch (Exception ex) {
                            LOGGER.error(StringUtil.printException(ex));
                        }

                        if (lstHaDeviceCountDTO != null && !lstHaDeviceCountDTO.isEmpty()) {
                            for (HomeAdminDeviceCountDTO homeAdminDeviceCountDTO : lstHaDeviceCountDTO) {
                                for (HomeAdminDTO homeAdminDTO : lstHomeAdminDTO) {
                                    if (homeAdminDTO.getId().equals(homeAdminDeviceCountDTO.getHomeAdminId())) {
                                        homeAdminDTO.setCountDevice(homeAdminDeviceCountDTO.getCountDevice());
                                    }
                                }
                            }
                        }

                    }

                    return new ResponseMessage(new MessageContent(lstHomeAdminDTO));
                }
            }

        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }

        return response;
    }

    public ResponseMessage getDetailCustomer(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {

            if (StringUtil.isNullOrEmpty(urlParam)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                AuthorizationResponseDTO dto = authenToken(headerParam);
                if (dto == null) {
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    "Bạn chưa đăng nhập"));
                } else {
                    Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                    String id = (String) params.get("id");
                    List<HomeAdminDTO> lstHomeAdminDTO = this.userSystemService.findCustomerByBuId(id);

                    if (lstHomeAdminDTO == null) {
                        return new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    }

                    return new ResponseMessage(new MessageContent(lstHomeAdminDTO));
                }
            }

        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }

        return response;
    }
}
