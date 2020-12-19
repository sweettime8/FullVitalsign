package com.elcom.id.controller;

import com.elcom.gateway.message.MessageContent;
import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.id.auth.CustomUserDetails;
import com.elcom.id.auth.jwt.JwtTokenProvider;
import com.elcom.id.config.ApplicationConfig;
import com.elcom.id.constant.Constant;
import com.elcom.id.mail.MailContentDTO;
import com.elcom.id.messaging.rabbitmq.RabbitMQClient;
import com.elcom.id.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.id.model.User;
import com.elcom.id.model.PatientProfile;
import com.elcom.id.model.dto.AuthorizationResponseDTO;
import com.elcom.id.model.dto.DoctorPatientMapDTO;
import com.elcom.id.model.dto.ListUserProfileDTO;
import com.elcom.id.model.dto.OtpExpiredDTO;
import com.elcom.id.model.dto.UserAvatarDTO;
import com.elcom.id.model.dto.UserDetailDTO;
import com.elcom.id.model.dto.UserOtpDTO;
import com.elcom.id.model.dto.UserPagingDTO;
import com.elcom.id.model.dto.UserProfileDTO;
import com.elcom.id.service.AuthService;
import com.elcom.id.service.UserService;
import com.elcom.id.thread.IdThreadManager;
import com.elcom.id.utils.StringUtil;
import com.elcom.id.utils.encrypt.AES;
import com.elcom.id.validation.UserValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

/**
 *
 * @author anhdv
 */
@Controller
public class UserController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RabbitMQClient rabbitMQClient;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IdThreadManager idThreadManager;

    public ResponseMessage getAllUser(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            //Check RBAC quyền xem danh sach user
            boolean hasRight = authorizeRBAC("GET", dto.getUuid(), "/v1.0/user/cms");
            if (hasRight) {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                int currentPage = Integer.parseInt(params.get("currentPage"));
                int rowsPerPage = Integer.parseInt(params.get("rowsPerPage"));
                String sort = params.get("sort");
                String keyword = params.get("keyword");
                Integer status = null;
                String strStatus = params.get("status");
                if (StringUtil.isNumeric(strStatus)) {
                    status = Integer.parseInt(strStatus);
                }

                if (currentPage == 0 || rowsPerPage == 0) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                    Constant.VALIDATION_INVALID_PARAM_VALUE));
                } else {
                    if (!StringUtil.isNullOrEmpty(sort) && !"uuid".equalsIgnoreCase(sort)
                            && !"email".equalsIgnoreCase(sort) && !"mobile".equalsIgnoreCase(sort)
                            && !"fullName".equalsIgnoreCase(sort) && !"createdAt".equalsIgnoreCase(sort)) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                        "Không có kiểu sort theo " + sort));
                    } else {
                        UserPagingDTO userDTO = userService.findAll(keyword, status, currentPage, rowsPerPage, sort);
                        if (userDTO == null || userDTO.getDataRows() == null || userDTO.getDataRows().isEmpty()) {
                            response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                    new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                            Constant.VALIDATION_DATA_NOT_FOUND));
                        } else {
                            response = new ResponseMessage(new MessageContent(userDTO.getDataRows(), userDTO.getTotalRows()));
                        }
                    }
                }
            } else {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn không có quyền quản lý danh sách user"));
            }
        }
        return response;
    }

    public ResponseMessage getDetailUser(String sId, Map<String, String> headerParam) {
        ResponseMessage response;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            if (!StringUtil.isNumberic(sId) && !StringUtil.isUUID(sId)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
            } else {
                User user = userService.findByUuid(sId);
                if (user == null) {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                } else {
                    UserDetailDTO detailDTO = new UserDetailDTO(user);
                    response = new ResponseMessage(new MessageContent(detailDTO));
                }
            }
        }
        return response;
    }

    public ResponseMessage createUser(Map<String, Object> bodyParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            String userName = (String) bodyParam.get("userName");
            String email = (String) bodyParam.get("email");
            String mobile = (String) bodyParam.get("mobile");
            String fullName = (String) bodyParam.get("fullName");
            String password = (String) bodyParam.get("password");
            Integer signupType = (Integer) bodyParam.get("signupType");
            String address = (String) bodyParam.get("address");
            String fbId = (String) bodyParam.get("fbId");
            String ggId = (String) bodyParam.get("ggId");
            String avatar = (String) bodyParam.get("avatar");
            String createDefaultRoleUser = (String) bodyParam.get("defaultRole");
            int gender = (Integer) bodyParam.get("gender");
            if (signupType == null) {
                signupType = Constant.USER_SIGNUP_NORMAL;
            }

            User user = new User();
            user.setUuid(UUID.randomUUID().toString());
            user.setUserName(userName);
            user.setEmail(email);
            user.setMobile(mobile);
            user.setFullName(fullName);
            user.setPassword(password);
            user.setSignupType(signupType);
            user.setFacebook(fbId);
            user.setAddress(address);
            user.setGgId(ggId);
            user.setFbId(fbId);
            user.setIsDelete(0);
            user.setStatus(1);
            user.setEmailVerify(0);
            user.setMobileVerify(0);
            user.setSetPassword(1);
            user.setAvatar(avatar);
            user.setGender(gender);

            String invalidData = new UserValidation().validateInsertUser(user);
            if (invalidData != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData, new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                User existUser = null;
                //Check email exist
                if (signupType == Constant.USER_SIGNUP_NORMAL && StringUtil.validateEmail(user.getEmail())) {
                    existUser = userService.findByEmail(user.getEmail());
                }
                if (existUser != null) {
                    invalidData = "Đã tồn tại user trên hệ thống ứng với email " + user.getEmail();
                    response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                            new MessageContent(HttpStatus.CONFLICT.value(), invalidData,
                                    invalidData));
                } else {
                    //Check mobile exist
                    if (signupType == Constant.USER_SIGNUP_NORMAL && StringUtil.checkMobilePhoneNumberNew(user.getMobile())) {
                        existUser = userService.findByMobile(user.getMobile());
                    }
                    if (existUser != null) {
                        invalidData = "Đã tồn tại user trên hệ thống ứng với mobile " + user.getMobile();
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                new MessageContent(HttpStatus.CONFLICT.value(), invalidData,
                                        invalidData));
                    } else {
                        //Check user_name exist
                        if (signupType == Constant.USER_SIGNUP_NORMAL && !StringUtil.isNullOrEmpty(user.getUserName())) {
                            existUser = userService.findByUserName(user.getUserName());
                        }
                        if (existUser != null) {
                            invalidData = "Đã tồn tại user trên hệ thống ứng với user_name " + user.getUserName();
                            response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                    new MessageContent(HttpStatus.CONFLICT.value(), invalidData,
                                            invalidData));
                        } else {
                            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
                            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                            try {
                                userService.save(user);
                                response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), new MessageContent(user));
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.toString()));
                            }
                            if (response != null && response.getStatus() == HttpStatus.CREATED.value() && !"no".equalsIgnoreCase(createDefaultRoleUser)) {

                                //Send 2 RBAC => Create default ROLE_USER
                                idThreadManager.execute(() -> {
                                    Map<String, Object> rbacRequestBodyParam = new HashMap<>();
                                    rbacRequestBodyParam.put("uuidUser", user.getUuid());
                                    RequestMessage request = new RequestMessage("POST", RabbitMQProperties.RBAC_RPC_DEFAULT_ROLE_URL,
                                            null, null, rbacRequestBodyParam, null);
                                    LOGGER.info("CREATE DEFAULT ROLE (send 2 RBAC service) with param : " + request.toJsonString());
                                    String result = rabbitMQClient.callRpcService(RabbitMQProperties.RBAC_RPC_EXCHANGE,
                                            RabbitMQProperties.RBAC_RPC_QUEUE, RabbitMQProperties.RBAC_RPC_KEY,
                                            request.toJsonString());
                                    if (!StringUtil.isNullOrEmpty(result)) {
                                        try {
                                            ObjectMapper mapper = new ObjectMapper();
                                            ResponseMessage resultResponse = mapper.readValue(result, ResponseMessage.class);
                                            if (resultResponse != null && (resultResponse.getStatus() == HttpStatus.OK.value()
                                                    || resultResponse.getStatus() == HttpStatus.CREATED.value())
                                                    && resultResponse.getData() != null) {
                                                LOGGER.info("CREATE DEFAULT ROLE for {} ok ", user.getUuid());
                                            } else {
                                                LOGGER.info("ERROR >>> create DEFAULT ROLE for {} ", user.getUuid());
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
            }
        }
        return response;
    }

    public ResponseMessage deleteUser(String sId) {
        ResponseMessage response = null;

        if (!StringUtil.isNumberic(sId) && !StringUtil.isUUID(sId)) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        } else {
            User user = userService.findByUuid(sId);
            if (user == null) {
                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
            } else {
                userService.remove(user);
                response = new ResponseMessage(new MessageContent(sId));
            }
        }
        return response;
    }

    public ResponseMessage updateUser(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                            "Bạn chưa đăng nhập"));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                Constant.VALIDATION_INVALID_PARAM_VALUE));
            } else {
                String uuid = dto.getUuid();//(String) bodyParam.get("uuid");
                String mobile = (String) bodyParam.get("mobile");
                String fullName = (String) bodyParam.get("fullName");
                String skype = (String) bodyParam.get("skype");
                String facebook = (String) bodyParam.get("facebook");
                String avatar = (String) bodyParam.get("avatar");
                String address = (String) bodyParam.get("address");
                String birthDay = (String) bodyParam.get("birthDay");
                Integer gender = (Integer) bodyParam.get("gender");

                User user = new User();
                user.setUuid(uuid);
                user.setMobile(mobile);
                user.setFullName(fullName);
                user.setSkype(skype);
                user.setFacebook(facebook);
                user.setAvatar(avatar);
                user.setAddress(address);
                user.setBirthDay(birthDay);
                user.setGender(gender);

                String invalidData = new UserValidation().validateUpdateUser(user);
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                } else {
                    if (!user.getUuid().equals(dto.getUuid())) {
                        response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn không được phép sửa thông tin của uuid " + user.getUuid(),
                                new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn không được phép sửa thông tin của uuid " + user.getUuid(),
                                        "Bạn không được phép sửa thông tin của uuid " + user.getUuid()));
                    } else {
                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        user.setLastUpdate(now);
                        //Check profile update
                        if (!StringUtil.isNullOrEmpty(fullName) && !StringUtil.isNullOrEmpty(birthDay)
                                && gender != null && !StringUtil.isNullOrEmpty(address)) {
                            user.setProfileUpdate(now);
                        }
                        //Check avatar update
                        if (!StringUtil.isNullOrEmpty(avatar)) {
                            user.setAvatarUpdate(now);
                        }
                        try {
                            userService.update(user);
                            response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(user));
                        } catch (Exception ex) {
                            response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(),
                                    new MessageContent(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(), ex.toString()));
                        }
                        if (response.getStatus() == HttpStatus.OK.value()) {
                            //Avatar update => Score update avatar
                        }
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage updateDoctor(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                            "Bạn chưa đăng nhập"));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                Constant.VALIDATION_INVALID_PARAM_VALUE));
            } else {
                String uuid = (String) bodyParam.get("uuid");
                String mobile = (String) bodyParam.get("mobile");
                String fullName = (String) bodyParam.get("fullName");
                String skype = (String) bodyParam.get("skype");
                String facebook = (String) bodyParam.get("facebook");
                String avatar = (String) bodyParam.get("avatar");
                String address = (String) bodyParam.get("address");
                String birthDay = (String) bodyParam.get("birthDay");
                Integer gender = (Integer) bodyParam.get("gender");

                User user = new User();
                user.setUuid(uuid);
                user.setMobile(mobile);
                user.setFullName(fullName);
                user.setSkype(skype);
                user.setFacebook(facebook);
                user.setAvatar(avatar);
                user.setAddress(address);
                user.setBirthDay(birthDay);
                user.setGender(gender);

                String invalidData = new UserValidation().validateUpdateUser(user);
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                } else {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    user.setLastUpdate(now);
                    //Check profile update
                    if (!StringUtil.isNullOrEmpty(fullName) && !StringUtil.isNullOrEmpty(birthDay)
                            && gender != null && !StringUtil.isNullOrEmpty(address)) {
                        user.setProfileUpdate(now);
                    }
                    //Check avatar update
                    if (!StringUtil.isNullOrEmpty(avatar)) {
                        user.setAvatarUpdate(now);
                    }
                    try {
                        userService.update(user);
                        response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(user));
                    } catch (Exception ex) {
                        response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(),
                                new MessageContent(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(), ex.toString()));
                    }
                    if (response.getStatus() == HttpStatus.OK.value()) {
                        //Avatar update => Score update avatar
                    }

                }
            }
        }
        return response;
    }

    public ResponseMessage updateCustomer(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                            "Bạn chưa đăng nhập"));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                Constant.VALIDATION_INVALID_PARAM_VALUE));
            } else {
                String uuid = (String) bodyParam.get("uuid");
                String mobile = (String) bodyParam.get("mobile");
                String fullName = (String) bodyParam.get("fullName");
                String birthDay = (String) bodyParam.get("birthDay");
                Integer gender = (Integer) bodyParam.get("gender");
                String email = (String) bodyParam.get("email");

                User user = new User();
                user.setUuid(uuid);
                user.setMobile(mobile);
                user.setFullName(fullName);
                user.setBirthDay(birthDay);
                user.setGender(gender);
                user.setEmail(email);

                String invalidData = new UserValidation().validateUpdateUser(user);
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                } else {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    user.setLastUpdate(now);
                    //Check profile update
                    if (!StringUtil.isNullOrEmpty(fullName) && !StringUtil.isNullOrEmpty(birthDay)
                            && gender != null && !StringUtil.isNullOrEmpty(email) && !StringUtil.isNullOrEmpty(mobile)) {
                        user.setProfileUpdate(now);
                    }

                    try {
                        userService.update(user);
                        response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(user));
                    } catch (Exception ex) {
                        response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(),
                                new MessageContent(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(), ex.toString()));
                    }
                    if (response.getStatus() == HttpStatus.OK.value()) {
                        //Avatar update => Score update avatar
                    }

                }
            }
        }
        return response;
    }

    private AuthorizationResponseDTO getAuthorFromToken(Map<String, String> headerParam) {
        if (headerParam == null || (!headerParam.containsKey("authorization")
                && !headerParam.containsKey("Authorization"))) {
            return null;
        }
        String bearerToken = headerParam.get("authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            try {
                String jwt = bearerToken.substring(7);
                String uuid = tokenProvider.getUuidFromJWT(jwt);
                UserDetails userDetails = authService.loadUserByUuid(uuid);
                if (userDetails != null) {
                    User user = ((CustomUserDetails) userDetails).getUser();
                    if (user.getStatus() == User.STATUS_LOCK) {
                        return null;
                    } else {
                        UsernamePasswordAuthenticationToken authentication
                                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO((CustomUserDetails) authentication.getPrincipal(), null, null);
                        return responseDTO;
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("failed on set user authentication", ex);
                return null;
            }
        }
        return null;
    }

    public ResponseMessage findByUuid(Map<String, String> headerParam, Map<String, Object> bodyParam) {
        ResponseMessage response = null;

        //AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        //if (dto == null) {
        //    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
        //            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
        //                    "Bạn chưa đăng nhập"));
        //} else {
        List<String> uuidList = (List<String>) bodyParam.get("uuids");
        if (uuidList == null || uuidList.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            "uuids không được bỏ trống hoặc không đúng định dạng array"));
        } else {
            try {
                List<User> userList = userService.findByUuidIn(uuidList);
                if (userList == null || userList.isEmpty()) {
                    response = new ResponseMessage(HttpStatus.NO_CONTENT.value(), "Không tìm thấy user ứng với list uuid",
                            new MessageContent(HttpStatus.NO_CONTENT.value(), "Không tìm thấy user ứng với list uuid",
                                    "Không tìm thấy user ứng với list uuid"));
                } else {
                    response = new ResponseMessage(new MessageContent(userList));
                }
            } catch (Exception ex) {
                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi không cập nhật " + ex.toString(),
                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi không cập nhật " + ex.toString(), ex.toString()));
                ex.printStackTrace();
            }
        }
        //}
        return response;
    }

    public ResponseMessage updateEmail(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                            "Bạn chưa đăng nhập"));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                Constant.VALIDATION_INVALID_PARAM_VALUE));
            } else {
                String password = (String) bodyParam.get("password");
                String newEmail = (String) bodyParam.get("newEmail");

                String invalidData = new UserValidation().validateChangeEmail(dto, password, newEmail);
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                } else {
                    User user = userService.findByUuid(dto.getUuid());
                    boolean checkPassword = false;
                    //Nếu đăng ký qua gg hoặc fb và chưa thiết lập mật khẩu => K check password
                    if ((dto.getSignupType() == Constant.USER_SIGNUP_FACEBOOK
                            || dto.getSignupType() == Constant.USER_SIGNUP_GOOGLE) && dto.getSetPassword() == 0) {
                        checkPassword = true;
                    } else {
                        //Kiểm tra mật khẩu
                        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                        String dbPassword = user.getPassword();
                        checkPassword = passwordEncoder.matches(password, dbPassword);
                    }
                    if (checkPassword) {
                        User existUser = userService.findByEmail(newEmail);
                        //Check email exist
                        if (existUser != null && (StringUtil.isNullOrEmpty(user.getEmail()) || !user.getEmail().equals(newEmail))) {
                            response = new ResponseMessage(HttpStatus.CONFLICT.value(), "Đã tồn tại user trên hệ thống ứng với email " + newEmail,
                                    new MessageContent(HttpStatus.CONFLICT.value(), "Đã tồn tại user trên hệ thống ứng với email " + newEmail,
                                            "Đã tồn tại user trên hệ thống ứng với email " + newEmail));
                        } else {
                            user.setEmail(newEmail);
                            user.setEmailVerify(0);
                            try {
                                userService.changeEmail(user);
                                response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(user));
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(),
                                        new MessageContent(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(), ex.toString()));
                            }
                        }
                    } else {
                        // Report error 
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Mật khẩu không đúng",
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), "Mật khẩu không đúng",
                                        "Mật khẩu không đúng"));
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage updateMobile(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                            null));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                null));
            } else {
                String password = (String) bodyParam.get("password");
                String newMobile = (String) bodyParam.get("newMobile");

                String invalidData = new UserValidation().validateChangeMobile(dto, password, newMobile);
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    User user = userService.findByUuid(dto.getUuid());
                    boolean checkPassword = false;
                    //Nếu đăng ký qua gg hoặc fb và chưa thiết lập mật khẩu => K check password
                    if ((dto.getSignupType() == Constant.USER_SIGNUP_FACEBOOK
                            || dto.getSignupType() == Constant.USER_SIGNUP_GOOGLE) && dto.getSetPassword() == 0) {
                        checkPassword = true;
                    } else {
                        //Kiểm tra mật khẩu
                        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                        String dbPassword = user.getPassword();
                        checkPassword = passwordEncoder.matches(password, dbPassword);
                    }
                    if (checkPassword) {
                        if (newMobile.startsWith("+84")) {
                            newMobile = newMobile.replace("+84", "0");
                        }
                        User existUser = userService.findByMobile(newMobile);
                        //Check mobile exist
                        if (existUser != null && (StringUtil.isNullOrEmpty(user.getMobile()) || !user.getMobile().equals(newMobile))) {
                            response = new ResponseMessage(HttpStatus.CONFLICT.value(), "Đã tồn tại user trên hệ thống ứng với mobile " + newMobile,
                                    new MessageContent(HttpStatus.CONFLICT.value(), "Đã tồn tại user trên hệ thống ứng với mobile " + newMobile,
                                            null));
                        } else {
                            //Neu mobile moi thi set lai mobile verify
                            if (!newMobile.equals(user.getMobile())) {
                                user.setMobileVerify(0);
                            }
                            user.setMobile(newMobile);
                            try {
                                userService.changeMobile(user);
                                //LOGGER.info("======================================================");
                                //LOGGER.info(user.toJsonString());
                                //LOGGER.info("======================================================");
                                response = new ResponseMessage(new MessageContent(user));
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(),
                                        new MessageContent(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(), null));
                            }
                        }
                    } else {
                        // Report error 
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Mật khẩu không đúng",
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), "Mật khẩu không đúng",
                                        null));
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage updatePassword(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                Constant.VALIDATION_INVALID_PARAM_VALUE));
            } else {
                String curentPassword = (String) bodyParam.get("curentPassword");
                String newPassword = (String) bodyParam.get("newPassword");
                String rePassword = (String) bodyParam.get("rePassword");

                String invalidData = new UserValidation().validateUpdatePassword(dto, curentPassword, newPassword, rePassword);
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    User user = userService.findByUuid(dto.getUuid());
                    boolean checkPassword = false;
                    //Nếu đăng ký qua gg hoặc fb và chưa thiết lập mật khẩu => K check password
                    if ((dto.getSignupType() == Constant.USER_SIGNUP_FACEBOOK
                            || dto.getSignupType() == Constant.USER_SIGNUP_GOOGLE) && dto.getSetPassword() == 0) {
                        checkPassword = true;
                    } else {
                        //Check currentPassword
                        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                        String dbPassword = user.getPassword();
                        checkPassword = passwordEncoder.matches(curentPassword, dbPassword);
                    }
                    if (checkPassword) {
                        // Encode new password and store it
                        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
                        user.setSetPassword(1);
                        try {
                            if (userService.changePassword(user)) {
                                // Xác thực thông tin người dùng Request lên, nếu không xảy ra exception tức là thông tin hợp lệ
                                Authentication authentication = null;
                                try {
                                    authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), newPassword));
                                } catch (AuthenticationException ex) {
                                    LOGGER.error("Error to set new authentication >>> " + ex.toString());
                                }
                                // Set thông tin authentication mới vào Security Context
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(bodyParam));
                            } else {
                                response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(),
                                        new MessageContent(HttpStatus.NOT_MODIFIED.value(), HttpStatus.NOT_MODIFIED.getReasonPhrase(), null));
                            }
                        } catch (Exception ex) {
                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                    new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.toString()));
                        }
                    } else {
                        // Report error 
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Mật khẩu hiện tại không đúng",
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), "Mật khẩu hiện tại không đúng",
                                        "Mật khẩu hiện tại không đúng"));
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage checkUserExist(String urlParam) {
        ResponseMessage response = null;
        if (StringUtil.isNullOrEmpty(urlParam)) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String type = params.get("type");
            String email = params.get("email");
            String mobile = params.get("mobile");

            if ("email".equalsIgnoreCase(type)) {
                if (!StringUtil.validateEmail(email)) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                    "email không được trống hoặc không đúng định dạng"));
                } else {
                    User user = userService.findByEmail(email);
                    if (user == null) {
                        response = new ResponseMessage(new MessageContent("Không tìm thấy user ứng với email " + email));
                    } else {
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                new MessageContent(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                        "Email đã được đăng ký bởi tài khoản khác"));
                    }
                }
            } else if ("mobile".equalsIgnoreCase(type)) {
                if (!StringUtil.checkMobilePhoneNumberNew(mobile)) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                    "mobile không được trống hoặc không đúng định dạng"));
                } else {
                    User user = userService.findByMobile(mobile);
                    if (user == null) {
                        response = new ResponseMessage(new MessageContent("Không tìm thấy user ứng với mobile " + mobile));
                    } else {
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                new MessageContent(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
                                        "Số điện thoại này đã được xác thực bởi một tài khoản khác. Mỗi số điện thoại chỉ có thể xác thực cho một tài khoản"));
                    }
                }
            } else {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                "type chỉ nhận giá trị bằng email hoặc mobile"));
            }
        }
        return response;
    }

    public ResponseMessage forgotPassword(String urlParam) {
        ResponseMessage response = null;
        if (StringUtil.isNullOrEmpty(urlParam)) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            Constant.VALIDATION_INVALID_PARAM_VALUE));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String email = params.get("email");

            if (!StringUtil.validateEmail(email)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "email không được trống hoặc không đúng định dạng",
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), "email không được trống hoặc không đúng định dạng",
                                null));
            } else {
                User user = userService.findByEmail(email);
                if (user == null) {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy user ứng với email " + email,
                            new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy user ứng với email " + email,
                                    null));
                } else {
                    //Code mới trả về link
                    String genCheckInfo = user.getUuid() + "&" + (System.currentTimeMillis()
                            + ApplicationConfig.FORGOTPASS_EXPIRED_TIME * 60 * 1000);
                    String token = AES.encryptAESbase(genCheckInfo, Constant.AES_KEY);
                    String link = ApplicationConfig.FRONTEND_FORGOTPASS_URL + "?token=" + token;
                    MailContentDTO item = new MailContentDTO();
                    item.setType("one");
                    item.setFromName("CoLearn.vn");
                    item.setEmailTitle(String.format(Constant.MAIL_FORGOT_PW_TITLE, user.getEmail()));
                    item.setEmailContent(String.format(Constant.MAIL_FORGOT_PW_CONTENT_LINK,
                            user.getFullName(), link, link, ApplicationConfig.FORGOTPASS_EXPIRED_TIME));
                    item.setEmailTo(email);
                    if (idThreadManager.sendEmail(item)) {
                        response = new ResponseMessage(new MessageContent("Gửi email quên mật khẩu thành công"));
                    } else {
                        response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                        null));
                    }

                    //Code cũ đang chơi kiểu reset pass => Xong gửi pass về email (trước 04/11/2020)
                    //Reset password and send email
                    //String resetPassword = StringUtil.generateRandomString(8);
                    // Encode new password and store it
                    /*user.setPassword(new BCryptPasswordEncoder().encode(resetPassword));
                    try {
                        if (userService.changePassword(user)) {
                            // Xác thực thông tin người dùng Request lên, nếu không xảy ra exception tức là thông tin hợp lệ
                            Authentication authentication = null;
                            try {
                                authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), resetPassword));
                            } catch (AuthenticationException ex) {
                                LOGGER.error("Error to set new authentication >>> " + ex.toString());
                            }
                            // Set thông tin authentication mới vào Security Context
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            MailContentDTO item = new MailContentDTO();
                            item.setType("one");
                            item.setFromName("CoLearn Website");
                            item.setEmailTitle(Constant.MAIL_FORGOT_PW_TITLE);
                            item.setEmailContent(String.format(Constant.MAIL_FORGOT_PW_CONTENT, user.getFullName(), resetPassword));
                            item.setEmailTo(email);
                            if (idThreadManager.sendEmail(item)) {
                                response = new ResponseMessage(new MessageContent("Gửi email quên mật khẩu thành công"));
                            } else {
                                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                                null));
                            }
                        } else {
                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                    new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
                        }
                    } catch (Exception ex) {
                        response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.toString(), null));
                    }*/
                }
            }
        }
        return response;
    }

    public ResponseMessage updateStatus(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                            "Bạn chưa đăng nhập"));
        } else {
            //Check RBAC quyền cap nhat user status
            boolean hasRight = authorizeRBAC("PUT", dto.getUuid(), "/v1.0/user/status");
            if (hasRight) {
                if (bodyParam == null || bodyParam.isEmpty()) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                    Constant.VALIDATION_INVALID_PARAM_VALUE));
                } else {
                    String uuid = (String) bodyParam.get("uuid");
                    Integer status = (Integer) bodyParam.get("status");

                    User user = new User();
                    user.setUuid(uuid);
                    user.setStatus(status);

                    String invalidData = new UserValidation().validateUpdateStatus(user);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                    } else {
                        User existUser = userService.findByUuid(uuid);
                        if (existUser == null) {
                            response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy user ứng với uuid " + uuid,
                                    new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy user ứng với uuid " + uuid,
                                            "Không tìm thấy user ứng với uuid " + uuid));
                        } else {
                            user.setLastUpdate(new Timestamp(System.currentTimeMillis()));
                            try {
                                userService.changeStatus(user);
                                response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(bodyParam));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(),
                                        new MessageContent(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(), ex.toString()));
                            }
                        }
                    }
                }
            } else {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                                "Bạn không có quyền cập nhật trạng thái user"));
            }
        }
        return response;
    }

    public ResponseMessage findAvatarByUuid(Map<String, Object> bodyParam) {
        ResponseMessage response = null;
        List<String> uuidList = (List<String>) bodyParam.get("uuids");
        if (uuidList == null || uuidList.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            "uuids không được bỏ trống hoặc không đúng định dạng array"));
        } else {
            try {
                List<User> userList = userService.findByUuidIn(uuidList);
                if (userList == null || userList.isEmpty()) {
                    LOGGER.info("userList nulllllllll");
                    response = new ResponseMessage(HttpStatus.NO_CONTENT.value(), "Không tìm thấy user avatar ứng với list uuid",
                            new MessageContent(HttpStatus.NO_CONTENT.value(), "Không tìm thấy user avatar ứng với list uuid",
                                    "Không tìm thấy user avatar ứng với list uuid"));
                } else {
                    LOGGER.info("userList size: " + userList.size());
                    List<UserAvatarDTO> userAvatarList = UserAvatarDTO.getAvatarList(userList);
                    response = new ResponseMessage(new MessageContent(userAvatarList));
                }
            } catch (Exception ex) {
                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi không cập nhật " + ex.toString(),
                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi không cập nhật " + ex.toString(), ex.toString()));
                ex.printStackTrace();
            }
        }
        return response;
    }

    public ResponseMessage updateSocialMobile(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                Constant.VALIDATION_INVALID_PARAM_VALUE));
            } else {
                String mobile = (String) bodyParam.get("mobile");

                String invalidData = new UserValidation().validateUpdateSocialMobile(dto, mobile);
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    User user = userService.findByUuid(dto.getUuid());
                    if (mobile.startsWith("+84")) {
                        mobile = mobile.replace("+84", "0");
                    }
                    User existUser = userService.findByMobile(mobile);
                    //Check mobile exist
                    if (existUser != null && (StringUtil.isNullOrEmpty(user.getMobile()) || !user.getMobile().equals(mobile))) {
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), "Đã tồn tại user trên hệ thống ứng với mobile " + mobile,
                                new MessageContent(HttpStatus.CONFLICT.value(), "Đã tồn tại user trên hệ thống ứng với mobile " + mobile,
                                        null));
                    } else {
                        //Check Redis contains OTP of user
                        OtpExpiredDTO otpExpired = getOtpExpiredTime(user.getUuid());
                        //Nếu chưa gửi lần nào hoặc đã hết 15ph lại cho đổi hoặc trong 15ph đó chưa gửi quá 3 lần
                        if (otpExpired == null || otpExpired.getOtpExpiredTime() < System.currentTimeMillis()
                                || otpExpired.getCountSms() < Constant.MAX_SMS_PER_EXPIRED) {
                            user.setOtpMobile(mobile);
                            user.setMobileVerify(0);
                            try {
                                userService.changeOtpMobile(user);
                                if (otpExpired == null || otpExpired.getOtpExpiredTime() < System.currentTimeMillis()) {
                                    user.setOtpTime(new Timestamp(System.currentTimeMillis() + Constant.OTP_TIME_EXIPRED));
                                }
                                //Convert only here for UserOtpDTO
                                UserOtpDTO userOtp = new UserOtpDTO(user);
                                //
                                response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(userOtp));
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), "Lỗi cập nhật mobile khi đăng nhập Facebook/Google >>> " + ex.toString(),
                                        new MessageContent(HttpStatus.NOT_MODIFIED.value(), "Lỗi cập nhật mobile khi đăng nhập Facebook/Google >>> " + ex.toString(), null));
                            }
                            if (response.getStatus() == HttpStatus.OK.value()) {
                                //Tao OTP & set OTP time
                                String otp = RandomStringUtils.randomNumeric(6);
                                if (userService.createOTP(user, otp)) {
                                    String userMobile = mobile;
                                    idThreadManager.execute(() -> {
                                        //Send SMS via Monty mobile
                                        boolean status = sendSms(userMobile, otp);
                                        LOGGER.info("Send OTP {} to mobile {} => {}", otp, userMobile, status);
                                        if (status) {
                                            OtpExpiredDTO tmpOtpExpired = otpExpired;
                                            //Push 2 redis otpExpired
                                            if (tmpOtpExpired == null || tmpOtpExpired.getOtpExpiredTime() < System.currentTimeMillis()) {
                                                tmpOtpExpired = new OtpExpiredDTO();
                                                tmpOtpExpired.setUuid(user.getUuid());
                                                tmpOtpExpired.setOtpExpiredTime(user.getOtpTime().getTime());
                                                tmpOtpExpired.setCountSms(1);
                                            } else {
                                                tmpOtpExpired.setCountSms(tmpOtpExpired.getCountSms() + 1);
                                            }
                                            pushOtpExpiredTime(user.getUuid(), tmpOtpExpired);
                                        }
                                    });
                                }
                            }
                        } else {
                            String reason = "Chỉ được cập nhật số điện thoại tối đa " + Constant.MAX_SMS_PER_EXPIRED + " lần trong vòng "
                                    + (Constant.OTP_TIME_EXIPRED / (60 * 1000) + " phút ");
                            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), reason, new MessageContent(HttpStatus.FORBIDDEN.value(), reason, null));
                        }
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage verifyMobileViaOtp(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập",
                            null));
        } else {
            if (bodyParam == null || bodyParam.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                null));
            } else {
                String otp = (String) bodyParam.get("otp");

                String invalidData = null;
                if (!StringUtil.isDigit(otp)) {
                    invalidData = "OTP là chuỗi 6 số";
                }
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    User user = userService.findByUuid(dto.getUuid());
                    if (user != null && otp.equals(user.getOtp())) {
                        //Kiểm tra thời gian hết hạn OTP so với hiện tại
                        if (user.getOtpTime() != null && System.currentTimeMillis() <= user.getOtpTime().getTime()) {
                            try {
                                user.setMobile(user.getOtpMobile());
                                user.setMobileVerify(1);
                                user.setLastUpdate(new Timestamp(System.currentTimeMillis()));
                                userService.changeMobile(user);
                                response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(user));
                            } catch (Exception ex) {
                                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi verify mobile qua OTP >>> " + ex.toString(),
                                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi verify mobile qua OTP >>> " + ex.toString(), null));
                            }
                        } else {
                            response = new ResponseMessage(HttpStatus.NOT_ACCEPTABLE.value(), "OTP hết hạn",
                                    new MessageContent(HttpStatus.NOT_ACCEPTABLE.value(), "OTP hết hạn",
                                            null));
                        }
                    } else {
                        response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "User không tồn tại hoặc OTP không chính xác",
                                new MessageContent(HttpStatus.NOT_FOUND.value(), "User không tồn tại hoặc OTP không chính xác",
                                        null));
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage resendOTP(Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Bạn chưa đăng nhập"));
        } else {
            User user = userService.findByUuid(dto.getUuid());
            if (user != null && user.getMobileVerify() == 0) {
                //Check Redis contains OTP of user
                OtpExpiredDTO otpExpired = getOtpExpiredTime(user.getUuid());
                //Nếu chưa gửi lần nào hoặc đã hết 15ph lại cho đổi hoặc trong 15ph đó chưa gửi quá 3 lần
                if (otpExpired == null || otpExpired.getOtpExpiredTime() < System.currentTimeMillis()
                        || otpExpired.getCountSms() < Constant.MAX_SMS_PER_EXPIRED) {
                    //Tao OTP & set OTP time
                    String otp = RandomStringUtils.randomNumeric(6);
                    if (otpExpired == null || otpExpired.getOtpExpiredTime() < System.currentTimeMillis()) {
                        user.setOtpTime(new Timestamp(System.currentTimeMillis() + Constant.OTP_TIME_EXIPRED));
                    }
                    if (userService.createOTP(user, otp)) {
                        //Convert only here for UserOtpDTO
                        UserOtpDTO userOtp = new UserOtpDTO(user);
                        //
                        response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(),
                                new MessageContent(userOtp));
                        idThreadManager.execute(() -> {
                            String otpMobile = user.getOtpMobile();
                            //Send SMS via Monty mobile
                            boolean status = sendSms(otpMobile, otp);
                            LOGGER.info("Send OTP {} to mobile {} => {}", otp, otpMobile, status);
                            if (status) {
                                OtpExpiredDTO tmpOtpExpired = otpExpired;
                                //Push 2 redis otpExpired
                                if (tmpOtpExpired == null || tmpOtpExpired.getOtpExpiredTime() < System.currentTimeMillis()) {
                                    tmpOtpExpired = new OtpExpiredDTO();
                                    tmpOtpExpired.setUuid(user.getUuid());
                                    tmpOtpExpired.setOtpExpiredTime(user.getOtpTime().getTime());
                                    tmpOtpExpired.setCountSms(1);
                                } else {
                                    tmpOtpExpired.setCountSms(tmpOtpExpired.getCountSms() + 1);
                                }
                                pushOtpExpiredTime(user.getUuid(), tmpOtpExpired);
                            }
                        });
                    } else {
                        response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi gửi lại OTP",
                                new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi gửi lại OTP", null));
                    }
                } else {
                    String reason = "Chỉ được gửi OTP verify tối đa " + Constant.MAX_SMS_PER_EXPIRED + " lần trong vòng "
                            + (Constant.OTP_TIME_EXIPRED / (60 * 1000) + " phút ");
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), reason, new MessageContent(HttpStatus.FORBIDDEN.value(), reason, null));
                }
            } else if (user != null && user.getMobileVerify() == 1) {
                response = new ResponseMessage(HttpStatus.CONFLICT.value(), "User mobile đã được xác minh rồi",
                        new MessageContent(null));
            } else {
                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy thông tin user",
                        new MessageContent(null));
            }
        }
        return response;
    }

    public ResponseMessage updateUserInternal(Map<String, Object> bodyParam, Map<String, String> headerParam, String pathParam) {
        ResponseMessage response = null;

        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            null));
        } else {
            String uuid = pathParam;
            String mobile = (String) bodyParam.get("mobile");
            String fullName = (String) bodyParam.get("fullName");
            String skype = (String) bodyParam.get("skype");
            String facebook = (String) bodyParam.get("facebook");
            String avatar = (String) bodyParam.get("avatar");
            String address = (String) bodyParam.get("address");
            String birthDay = (String) bodyParam.get("birthDay");
            Integer gender = (Integer) bodyParam.get("gender");

            User user = new User();
            user.setUuid(uuid);
            user.setMobile(mobile);
            user.setFullName(fullName);
            user.setSkype(skype);
            user.setFacebook(facebook);
            user.setAvatar(avatar);
            user.setAddress(address);
            user.setBirthDay(birthDay);
            user.setGender(gender);

            String invalidData = new UserValidation().validateUpdateUser(user);
            if (invalidData != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                user.setLastUpdate(now);
                //Check profile update
                if (!StringUtil.isNullOrEmpty(fullName) && !StringUtil.isNullOrEmpty(birthDay)
                        && gender != null && !StringUtil.isNullOrEmpty(address)) {
                    user.setProfileUpdate(now);
                }
                //Check avatar update
                if (!StringUtil.isNullOrEmpty(avatar)) {
                    user.setAvatarUpdate(now);
                }
                try {
                    userService.update(user);
                    response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(user));
                } catch (Exception ex) {
                    response = new ResponseMessage(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(),
                            new MessageContent(HttpStatus.NOT_MODIFIED.value(), "Lỗi không cập nhật " + ex.toString(), null));
                }
            }
        }
        return response;
    }

    public ResponseMessage checkToken(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            null));
        } else {
            String token = (String) bodyParam.get("token");
            if (StringUtil.isNullOrEmpty(token)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                                null));
            } else {
                String decryptResult = AES.decryptAESbase(token, Constant.AES_KEY);
                if (StringUtil.isNullOrEmpty(decryptResult)) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Token không đúng",
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), "Token không đúng",
                                    null));
                } else {
                    String[] decryptInfo = decryptResult.split("&");
                    if (decryptInfo == null || decryptInfo.length < 2) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Token không đúng",
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), "Token không đúng",
                                        null));
                    } else {
                        String userUuid = decryptInfo[0];
                        User user = userService.findByUuid(userUuid);

                        if (user == null || (user.getIsDelete() != null && (user.getStatus() == -1 || user.getIsDelete() == 1))) {
                            response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                    new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                        } else {
                            long now = System.currentTimeMillis();
                            long expiredTime = Long.parseLong(decryptInfo[1]);
                            if (expiredTime < now) {
                                response = new ResponseMessage(HttpStatus.REQUEST_TIMEOUT.value(), "Link quên mật khẩu hết hạn",
                                        new MessageContent(HttpStatus.REQUEST_TIMEOUT.value(), "Link quên mật khẩu hết hạn", null));
                            } else {
                                response = new ResponseMessage(new MessageContent(bodyParam));
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage updateForgotPassword(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            null));
        } else {
            String token = (String) bodyParam.get("token");
            String newPassword = (String) bodyParam.get("newPassword");
            String rePassword = (String) bodyParam.get("rePassword");

            String invalidData = new UserValidation().validateUpdateForgotPassword(token, newPassword, rePassword);
            if (invalidData != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                String decryptResult = AES.decryptAESbase(token, Constant.AES_KEY);
                if (StringUtil.isNullOrEmpty(decryptResult)) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Token không đúng",
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), "Token không đúng", null));
                } else {
                    String[] decryptInfo = decryptResult.split("&");
                    if (decryptInfo == null || decryptInfo.length < 2) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Token không đúng",
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), "Token không đúng", null));
                    } else {
                        String userUuid = decryptInfo[0];
                        User user = userService.findByUuid(userUuid);

                        if (user == null || (user.getIsDelete() != null && (user.getStatus() == -1 || user.getIsDelete() == 1))) {
                            response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                    new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                        } else {
                            long now = System.currentTimeMillis();
                            //15ph + 1ph cho ngoi nhin la 16ph
                            long expiredTime = Long.parseLong(decryptInfo[1]) + 60 * 1000;
                            if (expiredTime < now) {
                                response = new ResponseMessage(HttpStatus.REQUEST_TIMEOUT.value(), "Link quên mật khẩu hết hạn",
                                        new MessageContent(HttpStatus.REQUEST_TIMEOUT.value(), "Link quên mật khẩu hết hạn", null));
                            } else {
                                //Update password
                                user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
                                user.setSetPassword(1);
                                try {
                                    if (userService.changePassword(user)) {
                                        // Xác thực thông tin người dùng Request lên, nếu không xảy ra exception tức là thông tin hợp lệ
                                        Authentication authentication = null;
                                        try {
                                            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), newPassword));
                                        } catch (AuthenticationException ex) {
                                            LOGGER.error("Error to set new authentication >>> " + ex.toString());
                                        }
                                        // Set thông tin authentication mới vào Security Context
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        response = new ResponseMessage(new MessageContent(bodyParam));
                                    } else {
                                        response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                                new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
                                    }
                                } catch (Exception ex) {
                                    response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                            new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.toString(), null));
                                }
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage sendEmail(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                            null));
        } else {
            String emailTo = (String) bodyParam.get("emailTo");
            String title = (String) bodyParam.get("title");
            String content = (String) bodyParam.get("content");
            String sign = (String) bodyParam.get("sign");
            LOGGER.info("emailTo: {}, title: {}, content: {}, sign: {}", emailTo, title, content, sign);
            String invalidData = new UserValidation().validateSendEmail(emailTo, title, content, sign);
            if (invalidData != null) {
                LOGGER.info("invalidData: {}", invalidData);
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                MailContentDTO item = new MailContentDTO();
                item.setType("one");
                item.setFromName("VitalSign.com.vn");
                item.setEmailTitle(title);
                item.setEmailContent(content);
                item.setEmailTo(emailTo);
                if (idThreadManager.sendEmail(item)) {
                    LOGGER.info("Send email ok");
                    response = new ResponseMessage(new MessageContent("Gửi email thành công"));
                } else {
                    LOGGER.info("Send email INTERNAL_SERVER_ERROR");
                    response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                    null));
                }
            }
        }
        return response;
    }

    public ResponseMessage getListUserProfile(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {
            boolean validDeviceToken = authenDeviceToken(headerParam);
            if (!validDeviceToken) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            } //else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
            //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
            //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath))); 
            else {

                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String userId = (String) params.get("uuid");

                UserProfileDTO userProfileDTO = new UserProfileDTO();

                User user = userService.findByUuid(userId);
                List<ListUserProfileDTO> lstUserProfile = userService.findListUserProfile(userId);

                if (user != null && lstUserProfile != null) {
                    userProfileDTO.setUuid(userId);
                    userProfileDTO.setUsername(user.getUserName());
                    userProfileDTO.setFullname(user.getFullName());
                    userProfileDTO.setLstUserProfile(lstUserProfile);

                    response = new ResponseMessage(new MessageContent(userProfileDTO));
                } else {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }

        return response;
    }

    public ResponseMessage getListDoctorsByPatient(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        try {
            boolean validDeviceToken = authenDeviceToken(headerParam);
            if (!validDeviceToken) {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            } //else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
            //                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
            //                               new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath))); 
            else {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String patientId = (String) params.get("uuid");

                List<DoctorPatientMapDTO> lstDoctorPatientMapDTO = userService.findDoctorsByPatient(patientId);

                if (lstDoctorPatientMapDTO != null) {
                    response = new ResponseMessage(new MessageContent(lstDoctorPatientMapDTO));
                } else {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return response;
    }

    public ResponseMessage createProfile(Map<String, Object> bodyParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
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
                    String userId = (String) bodyParam.get("userId");
                    String fullName = (String) bodyParam.get("fullName");
                    String birthDay = (String) bodyParam.get("birthDay");
                    Integer gender = (Integer) bodyParam.get("gender");
                    String height = (String) bodyParam.get("height");
                    String weight = (String) bodyParam.get("weight");

                    PatientProfile patientProfile = new PatientProfile();
                    patientProfile.setUserId(userId);
                    patientProfile.setFullName(fullName);
                    patientProfile.setBirthDay(birthDay);
                    patientProfile.setGender(gender);
                    patientProfile.setHeight(height);
                    patientProfile.setWeight(weight);
                    patientProfile.setActive(1);
                    patientProfile.setIsDeleted(0);

                    String invalidData = new UserValidation().validateInsertProfile(patientProfile);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData, new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                    } else {

                        try {
                            userService.saveProfile(patientProfile);
                            response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), new MessageContent(patientProfile));
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

    public ResponseMessage updateProfile(Map<String, Object> bodyParam, Map<String, String> headerParam) {
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
                    String userId = (String) bodyParam.get("userId");
                    String fullName = (String) bodyParam.get("fullName");
                    String birthDay = (String) bodyParam.get("birthDay");
                    Integer gender = (Integer) bodyParam.get("gender");
                    String height = (String) bodyParam.get("height");
                    String weight = (String) bodyParam.get("weight");

                    PatientProfile patientProfile = new PatientProfile();
                    patientProfile.setId(id);
                    patientProfile.setUserId(userId);
                    patientProfile.setFullName(fullName);
                    patientProfile.setBirthDay(birthDay);
                    patientProfile.setGender(gender);
                    patientProfile.setHeight(height);
                    patientProfile.setWeight(weight);

                    String invalidData = new UserValidation().validateInsertProfile(patientProfile);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, invalidData));
                    } else {
                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        patientProfile.setLastUpdate(now);

                        try {
                            userService.updatePatientProfile(patientProfile);
                            response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(), new MessageContent(patientProfile));
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

    public ResponseMessage deleteProfile(Map<String, Object> bodyParam, Map<String, String> headerParam) {
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

                String id = (String) bodyParam.get("id");
                PatientProfile patientProfile = userService.findProfileById(id);

                if (patientProfile == null) {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, new MessageContent(HttpStatus.NOT_FOUND.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                } else {
                    userService.deletePatientProfile(patientProfile);
                    response = new ResponseMessage(new MessageContent(id));
                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }

        return response;
    }

}
