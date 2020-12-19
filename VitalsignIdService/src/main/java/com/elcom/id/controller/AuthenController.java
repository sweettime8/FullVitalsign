package com.elcom.id.controller;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.id.auth.CustomUserDetails;
import com.elcom.id.auth.jwt.JwtTokenProvider;
import com.elcom.id.constant.Constant;
import com.elcom.id.messaging.rabbitmq.RabbitMQClient;
import com.elcom.id.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.id.model.User;
import com.elcom.id.model.dto.AuthorizationResponseDTO;
import com.elcom.id.model.dto.UserFacebookDTO;
import com.elcom.id.model.dto.UserGoogleDTO;
import com.elcom.id.service.AuthService;
import com.elcom.id.service.UserService;
import com.elcom.id.thread.IdThreadManager;
import com.elcom.id.utils.JWTutils;
import com.elcom.id.utils.StringUtil;
import com.elcom.id.validation.UserValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Admin
 */
@Controller
public class AuthenController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenController.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RabbitMQClient rabbitMQClient;

    @Autowired
    private IdThreadManager idThreadManager;

    //Login
    public ResponseMessage userLogin(Map<String, Object> bodyParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            String email = (String) bodyParam.get("email");
            String userInfo = (String) bodyParam.get("userInfo");
            String password = (String) bodyParam.get("password");
            String loginIp = (String) bodyParam.get("loginIp");
            if (StringUtil.isNullOrEmpty(userInfo)) {
                userInfo = email;
            }

            String invalidData = new UserValidation().validateLogin(userInfo, password);
            if (invalidData != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData, 
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                // Check exist account with email or mobile
                User existUser = userService.findByEmailOrMobile(userInfo);
                if (existUser == null) {
                    invalidData = "Email hoặc Mobile không tồn tại";
                    return new ResponseMessage(HttpStatus.NOT_FOUND.value(), invalidData,
                                new MessageContent(HttpStatus.NOT_FOUND.value(), invalidData, null));
                } else {
                    // Xác thực thông tin người dùng Request lên, nếu không xảy ra exception tức là thông tin hợp lệ
                    Authentication authentication = null;
                    try {
                        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userInfo, password));
                    } catch (AuthenticationException ex) {
                        LOGGER.error(ex.toString());
                        invalidData = "Email/Mobile hoặc mật khẩu không đúng";
                        return new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), invalidData,
                                new MessageContent(HttpStatus.UNAUTHORIZED.value(), invalidData, null));
                    }
                    // Set thông tin authentication vào Security Context
                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                    if (userDetails.getUser().getStatus() == User.STATUS_LOCK) {
                        response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, new MessageContent(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, null));
                    } else {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        //Set last login
                        userService.updateLastLogin(userDetails.getUser().getUuid(), loginIp);
                        // Trả về jwt cho người dùng.
                        String accessJwt = tokenProvider.generateToken(userDetails);
                        String refreshJwt = JWTutils.createToken(userDetails.getUser().getUuid());
                        //LoginResponse loginResponse = new LoginResponse(accessJwt, refreshJwt);
                        AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                        response = new ResponseMessage(new MessageContent(responseDTO));
                    }
                }
            }
        }
        return response;
    }

    //Login Google
    public ResponseMessage socialLoginViaGoogle(Map<String, Object> bodyParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            String accessToken = (String) bodyParam.get("accessToken");
            if (StringUtil.isNullOrEmpty(accessToken)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, "Thiếu accessToken"));
            } else {
                try {
                    //Call Google API
                    //String googleAPI = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;
                    String googleAPI = "https://oauth2.googleapis.com/tokeninfo?id_token=" + accessToken;
                    RestTemplate restTemplate = new RestTemplate();
                    // Gửi yêu cầu với phương thức GET và Headers mặc định.
                    String result = null;
                    try {
                        result = restTemplate.getForObject(googleAPI, String.class);
                    } catch (RestClientException ex) {
                        LOGGER.error("failed on set socialLoginViaGoogle", ex);
                    }
                    LOGGER.info("result: " + result);
                    //For test
                    //result = "{\"sub\":\"1231323234\",\"name\":\"Đặng Văn Nghĩa\",\"email\":\"nghiadv@hotmail.com\"}";
                    if (result != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        UserGoogleDTO dto = mapper.readValue(result, UserGoogleDTO.class);
                        //LOGGER.info("dto: " + dto.toJsonString());
                        //1. Lấy thông tin user với google id
                        //2. Nếu không có check email với email google trả về
                        //3. Nếu không có cả 2 thì tạo user mới

                        //1.
                        User user = null;
                        if (!StringUtil.isNullOrEmpty(dto.getSub())) {
                            try {
                                user = userService.findBySocial(Constant.USER_SIGNUP_GOOGLE, dto.getSub());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (user != null) {
                            if (user.getStatus() == User.STATUS_LOCK) {
                                response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, new MessageContent(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, null));
                            } else {
                                //Set last login
                                userService.updateLastLogin(user.getUuid(), null);
                                // Trả về jwt cho người dùng.
                                CustomUserDetails userDetails = new CustomUserDetails(user);
                                String accessJwt = tokenProvider.generateToken(userDetails);
                                String refreshJwt = JWTutils.createToken(user.getUuid());
                                AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                                response = new ResponseMessage(new MessageContent(responseDTO));
                            }
                        } else {
                            //2.
                            if (!StringUtil.isNullOrEmpty(dto.getEmail())) {
                                user = userService.findByEmail(dto.getEmail());
                            }
                            if (user != null) {
                                if (user.getStatus() == User.STATUS_LOCK) {
                                    response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED,
                                            new MessageContent(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, null));
                                } else {
                                    //Set last login
                                    userService.updateLastLogin(user.getUuid(), null);
                                    //Update gg id cho email
                                    if (userService.connectSocial(user, Constant.USER_SIGNUP_GOOGLE, dto.getSub())) {
                                        user.setGgId(dto.getSub());
                                    }
                                    // Trả về jwt cho người dùng.
                                    CustomUserDetails userDetails = new CustomUserDetails(user);
                                    String accessJwt = tokenProvider.generateToken(userDetails);
                                    String refreshJwt = JWTutils.createToken(user.getUuid());
                                    AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                                    response = new ResponseMessage(new MessageContent(responseDTO));
                                }
                            } else {
                                //3.
                                user = new User();
                                user.setUuid(UUID.randomUUID().toString());
                                user.setUserName(dto.getSub());
                                user.setEmail(dto.getEmail());
                                user.setFullName(dto.getName());
                                if (!StringUtil.isNullOrEmpty(dto.getPicture())) {
                                    user.setAvatar(dto.getPicture());
                                }
                                user.setPassword(null);
                                user.setSignupType(Constant.USER_SIGNUP_GOOGLE);
                                user.setGgId(dto.getSub());
                                user.setIsDelete(0);
                                user.setStatus(1);
                                user.setEmailVerify(0);
                                user.setMobileVerify(0);
                                user.setSetPassword(0);

                                String invalidData = new UserValidation().validateInsertSocial(user);
                                if (invalidData != null) {
                                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                                } else {
                                    try {
                                        userService.save(user);

                                        // Trả về jwt cho người dùng.
                                        CustomUserDetails userDetails = new CustomUserDetails(user);
                                        String accessJwt = tokenProvider.generateToken(userDetails);
                                        String refreshJwt = JWTutils.createToken(user.getUuid());
                                        AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                                        response = new ResponseMessage(new MessageContent(responseDTO));
                                    } catch (Exception ex) {
                                        response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constant.RESPONSE_UNKNOW_ERR,
                                                new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constant.RESPONSE_UNKNOW_ERR, null));
                                        LOGGER.error(ex.getMessage());
                                    }

                                    if (response != null && response.getStatus() == HttpStatus.OK.value()) {
                                        //Send 2 RBAC => Create default ROLE_USER
                                        String userUuid = user.getUuid();
                                        idThreadManager.execute(() -> {
                                            Map<String, Object> rbacRequestBodyParam = new HashMap<>();
                                            rbacRequestBodyParam.put("uuidUser", userUuid);
                                            RequestMessage request = new RequestMessage("POST", RabbitMQProperties.RBAC_RPC_DEFAULT_ROLE_URL,
                                                    null, null, rbacRequestBodyParam, null);
                                            LOGGER.info("[Account Reg via Google] CREATE DEFAULT ROLE (send 2 RBAC service) with param : " + request.toJsonString());
                                            String rpcResult = rabbitMQClient.callRpcService(RabbitMQProperties.RBAC_RPC_EXCHANGE,
                                                    RabbitMQProperties.RBAC_RPC_QUEUE, RabbitMQProperties.RBAC_RPC_KEY,
                                                    request.toJsonString());
                                            if (!StringUtil.isNullOrEmpty(rpcResult)) {
                                                try {
                                                    ResponseMessage resultResponse = mapper.readValue(rpcResult, ResponseMessage.class);
                                                    if (resultResponse != null && (resultResponse.getStatus() == HttpStatus.OK.value()
                                                            || resultResponse.getStatus() == HttpStatus.CREATED.value())
                                                            && resultResponse.getData() != null) {
                                                        LOGGER.info("CREATE DEFAULT ROLE for {} ok ", userUuid);
                                                    } else {
                                                        LOGGER.info("ERROR >>> create DEFAULT ROLE for {} ", userUuid);
                                                    }
                                                } catch (JsonProcessingException ex) {
                                                    LOGGER.error("Error to parse json FROM CREATE DEFAULT ROLE (send 2 RBAC service) >>> " + ex.toString());
                                                    ex.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    } else {
                        response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(),
                                new MessageContent(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(),
                                        "Không lấy được thông tin với accessToken: " + accessToken));
                    }
                } catch (JsonProcessingException ex) {
                    response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(),
                            new MessageContent(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(),
                                    "Không lấy được thông tin với accessToken: " + accessToken));
                    LOGGER.error("failed on set socialLoginViaFacebook", ex);
                }
            }
        }
        return response;
    }

    //Login Facebok
    public ResponseMessage socialLoginViaFacebook(Map<String, Object> bodyParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            String accessToken = (String) bodyParam.get("accessToken");
            if (StringUtil.isNullOrEmpty(accessToken)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, "Thiếu accessToken"));
            } else {
                try {
                    //Call Facebook API
                    String facebookAPI = "https://graph.facebook.com/v3.1/me?fields=id,name,email&access_token=" + accessToken;
                    RestTemplate restTemplate = new RestTemplate();
                    // Gửi yêu cầu với phương thức GET và Headers mặc định.
                    String result = null;
                    try {
                        result = restTemplate.getForObject(facebookAPI, String.class);
                    } catch (RestClientException ex) {
                        LOGGER.error("failed on set socialLoginViaFacebook", ex);
                    }
                    LOGGER.info("result: " + result);
                    //For test
                    //result = "{\"id\":\"1231323234\",\"name\":\"Đặng Văn Nghĩa\",\"email\":\"nghiadv@hotmail.com\"}";
                    if (result != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        UserFacebookDTO dto = mapper.readValue(result, UserFacebookDTO.class);
                        //LOGGER.info("dto: " + dto.toJsonString());
                        //1. Lấy thông tin user với facebook id
                        //2. Nếu không có check email với email facebook trả về
                        //3. Nếu không có cả 2 thì tạo user mới

                        //1.
                        User user = null;
                        try {
                            user = userService.findBySocial(Constant.USER_SIGNUP_FACEBOOK, dto.getId());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (user != null) {
                            if (user.getStatus() == User.STATUS_LOCK) {
                                response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED,
                                        new MessageContent(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, null));
                            } else {
                                //Set last login
                                userService.updateLastLogin(user.getUuid(), null);
                                // Trả về jwt cho người dùng.
                                CustomUserDetails userDetails = new CustomUserDetails(user);
                                String accessJwt = tokenProvider.generateToken(userDetails);
                                String refreshJwt = JWTutils.createToken(user.getUuid());
                                AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                                response = new ResponseMessage(new MessageContent(responseDTO));
                            }
                        } else {
                            //2.
                            if (!StringUtil.isNullOrEmpty(dto.getEmail())) {
                                user = userService.findByEmail(dto.getEmail());
                            }
                            if (user != null) {
                                if (user.getStatus() == User.STATUS_LOCK) {
                                    response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED,
                                            new MessageContent(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, null));
                                } else {
                                    //Set last login
                                    userService.updateLastLogin(user.getUuid(), null);
                                    //Update fb id cho email
                                    if (userService.connectSocial(user, Constant.USER_SIGNUP_FACEBOOK, dto.getId())) {
                                        user.setFbId(dto.getId());
                                    }
                                    // Trả về jwt cho người dùng.
                                    CustomUserDetails userDetails = new CustomUserDetails(user);
                                    String accessJwt = tokenProvider.generateToken(userDetails);
                                    String refreshJwt = JWTutils.createToken(user.getUuid());
                                    AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                                    response = new ResponseMessage(new MessageContent(responseDTO));
                                }
                            } else {
                                //3.
                                user = new User();
                                user.setUuid(UUID.randomUUID().toString());
                                user.setUserName(dto.getId());
                                user.setEmail(dto.getEmail());
                                user.setFullName(dto.getName());
                                user.setFacebook(dto.getName());
                                user.setAvatar("https://graph.facebook.com/" + dto.getId() + "/picture?type=large");
                                user.setPassword(null);
                                user.setSignupType(Constant.USER_SIGNUP_FACEBOOK);
                                user.setFbId(dto.getId());
                                user.setIsDelete(0);
                                user.setStatus(1);
                                user.setEmailVerify(0);
                                user.setMobileVerify(0);
                                user.setSetPassword(0);

                                String invalidData = new UserValidation().validateInsertSocial(user);
                                if (invalidData != null) {
                                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData, new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                                } else {
                                    try {
                                        userService.save(user);

                                        // Trả về jwt cho người dùng.
                                        CustomUserDetails userDetails = new CustomUserDetails(user);
                                        String accessJwt = tokenProvider.generateToken(userDetails);
                                        String refreshJwt = JWTutils.createToken(user.getUuid());
                                        AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                                        response = new ResponseMessage(new MessageContent(responseDTO));
                                    } catch (Exception ex) {
                                        response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constant.RESPONSE_UNKNOW_ERR,
                                                new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constant.RESPONSE_UNKNOW_ERR, null));
                                        LOGGER.error(ex.getMessage());
                                    }

                                    if (response != null && response.getStatus() == HttpStatus.OK.value()) {
                                        String userUuid = user.getUuid();
                                        //Send 2 RBAC => Create default ROLE_USER
                                        idThreadManager.execute(() -> {
                                            Map<String, Object> rbacRequestBodyParam = new HashMap<>();
                                            rbacRequestBodyParam.put("uuidUser", userUuid);
                                            RequestMessage request = new RequestMessage("POST", RabbitMQProperties.RBAC_RPC_DEFAULT_ROLE_URL,
                                                    null, null, rbacRequestBodyParam, null);
                                            LOGGER.info("[Account Reg via Facebook] CREATE DEFAULT ROLE (send 2 RBAC service) with param : " + request.toJsonString());
                                            String rpcResult = rabbitMQClient.callRpcService(RabbitMQProperties.RBAC_RPC_EXCHANGE,
                                                    RabbitMQProperties.RBAC_RPC_QUEUE, RabbitMQProperties.RBAC_RPC_KEY,
                                                    request.toJsonString());
                                            if (!StringUtil.isNullOrEmpty(rpcResult)) {
                                                try {
                                                    ResponseMessage resultResponse = mapper.readValue(rpcResult, ResponseMessage.class);
                                                    if (resultResponse != null && (resultResponse.getStatus() == HttpStatus.OK.value()
                                                            || resultResponse.getStatus() == HttpStatus.CREATED.value())
                                                            && resultResponse.getData() != null) {
                                                        LOGGER.info("CREATE DEFAULT ROLE for {} ok ", userUuid);
                                                    } else {
                                                        LOGGER.info("ERROR >>> create DEFAULT ROLE for {} ", userUuid);
                                                    }
                                                } catch (JsonProcessingException ex) {
                                                    LOGGER.error("Error to parse json FROM CREATE DEFAULT ROLE (send 2 RBAC service) >>> " + ex.toString());
                                                    ex.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    } else {
                        response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(),
                                new MessageContent(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(),
                                        "Không lấy được thông tin với accessToken: " + accessToken));
                    }
                } catch (JsonProcessingException ex) {
                    response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(),
                            new MessageContent(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(),
                                    "Không lấy được thông tin với accessToken: " + accessToken));
                    LOGGER.error("failed on set socialLoginViaFacebook", ex);
                }
            }
        }
        return response;
    }

    //Login Apple
    public ResponseMessage socialLoginViaApple(Map<String, Object> bodyParam) {
        ResponseMessage response;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            String userId = (String) bodyParam.get("userId");
            String email = (String) bodyParam.get("email");
            String validationMsg = new UserValidation().validateLoginApple(userId, email);
            if (validationMsg != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), validationMsg, new MessageContent(HttpStatus.BAD_REQUEST.value(), validationMsg, null));
            } else {
                try {
                    User user = userService.findAppleAccount(userId, email);
                    if (user != null) { // Tài khoản này đã từng đăng ký, trả JWT AccessToken
                        if (User.STATUS_LOCK.equals(user.getStatus())) {
                            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, new MessageContent(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, null));
                        } else {
                            CustomUserDetails userDetails = new CustomUserDetails(user); // Trả jwt.
                            String accessJwt = tokenProvider.generateToken(userDetails);
                            String refreshJwt = JWTutils.createToken(user.getUuid());
                            AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                            response = new ResponseMessage(new MessageContent(responseDTO));

                            userService.updateLastLogin(user.getUuid(), null);
                        }
                    } else { // Xử lý đăng ký mới tài khoản
                        user = new User();
                        user.setUuid(UUID.randomUUID().toString());
                        user.setUserName(userId);
                        user.setAppleId(userId);
                        user.setEmail(email);
                        user.setFullName((String) bodyParam.get("fullName"));
                        user.setSignupType(Constant.USER_SIGNUP_APPLE);
                        user.setIsDelete(0);
                        user.setStatus(1);
                        user.setEmailVerify(0);
                        user.setMobileVerify(0);
                        user.setSetPassword(0);

                        String invalidData = new UserValidation().validateInsertSocial(user);
                        if (invalidData != null) {
                            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData, new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                        } else {
                            try {
                                userService.save(user);

                                CustomUserDetails userDetails = new CustomUserDetails(user); // Trả jwt
                                String accessJwt = tokenProvider.generateToken(userDetails);
                                String refreshJwt = JWTutils.createToken(user.getUuid());
                                AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                                response = new ResponseMessage(new MessageContent(responseDTO));
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constant.RESPONSE_UNKNOW_ERR,
                                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constant.RESPONSE_UNKNOW_ERR, null));
                                LOGGER.error(ex.getMessage());
                            }

                            if (response.getStatus() == HttpStatus.OK.value()) {
                                String userUuid = user.getUuid();
                                //Send 2 RBAC => Create default ROLE_USER
                                idThreadManager.execute(() -> {
                                    Map<String, Object> rbacRequestBodyParam = new HashMap<>();
                                    rbacRequestBodyParam.put("uuidUser", userUuid);
                                    RequestMessage request = new RequestMessage("POST", RabbitMQProperties.RBAC_RPC_DEFAULT_ROLE_URL,
                                            null, null, rbacRequestBodyParam, null);
                                    LOGGER.info("[Account Reg via Apple] CREATE DEFAULT ROLE (send 2 RBAC service) with param : " + request.toJsonString());
                                    String rpcResult = rabbitMQClient.callRpcService(RabbitMQProperties.RBAC_RPC_EXCHANGE,
                                            RabbitMQProperties.RBAC_RPC_QUEUE, RabbitMQProperties.RBAC_RPC_KEY, request.toJsonString());
                                    if (!StringUtil.isNullOrEmpty(rpcResult)) {
                                        try {
                                            ResponseMessage resultResponse = new ObjectMapper().readValue(rpcResult, ResponseMessage.class);
                                            if (resultResponse != null && (resultResponse.getStatus() == HttpStatus.OK.value()
                                                    || resultResponse.getStatus() == HttpStatus.CREATED.value())
                                                    && resultResponse.getData() != null) {
                                                LOGGER.info("CREATE DEFAULT ROLE for {} ok ", userUuid);
                                            } else {
                                                LOGGER.info("ERROR >>> create DEFAULT ROLE for {} ", userUuid);
                                            }
                                        } catch (Exception ex) {
                                            LOGGER.error("Error to parse json FROM CREATE DEFAULT ROLE (send 2 RBAC service) >>> " + ex.getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception ex) {
                    response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(),
                            new MessageContent(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.toString(), HttpStatus.BAD_REQUEST.toString()));
                    LOGGER.error(ex.getMessage());
                }
            }
        }
        return response;
    }

    //Authentication
    public ResponseMessage authorized(Map<String, String> headerParam) {
        ResponseMessage response = null;
        if (headerParam == null || headerParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null));
        } else {
            String bearerToken = headerParam.get("authorization");
            // Kiểm tra xem header Authorization có chứa thông tin jwt không
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                try {
                    String jwt = bearerToken.substring(7);
                    LOGGER.info("jwt => " + jwt);
                    String uuid = tokenProvider.getUuidFromJWT(jwt);
                    UserDetails userDetails = authService.loadUserByUuid(uuid);
                    if (userDetails != null) {
                        User user = ((CustomUserDetails) userDetails).getUser();
                        if (Objects.equals(user.getStatus(), User.STATUS_LOCK)) {
                            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED,
                                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), Constant.VALIDATION_ACCOUNT_LOCKED, null));
                        } else {
                            UsernamePasswordAuthenticationToken authentication
                                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO((CustomUserDetails) authentication.getPrincipal(), null, null);
                            response = new ResponseMessage(new MessageContent(responseDTO));
                        }
                    } else {
                        response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                                new MessageContent(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null));
                    }
                } catch (Exception ex) {
                    LOGGER.error("failed on set user authentication", ex);
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null));
                }
            } else {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null));
            }
        }
        return response;
    }
}
