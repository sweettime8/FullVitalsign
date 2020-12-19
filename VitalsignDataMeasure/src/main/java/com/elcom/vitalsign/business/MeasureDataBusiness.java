package com.elcom.vitalsign.business;

import com.elcom.vitalsign.message.MessageContent;
import com.elcom.vitalsign.message.ResponseMessage;
import com.elcom.vitalsign.constant.Constant;
import com.elcom.vitalsign.controller.BaseController;
import com.elcom.vitalsign.messaging.rabbitmq.RabbitMQClient;
import com.elcom.vitalsign.model.dto.AuthorizationResponseDTO;
import com.elcom.vitalsign.model.dto.BpDataByHealthProfile;
import com.elcom.vitalsign.model.dto.DataTempDTO;
import com.elcom.vitalsign.model.dto.Spo2DataByHealthProfile;
import com.elcom.vitalsign.model.dto.TempDataByHealthProfile;
import com.elcom.vitalsign.utils.StringUtil;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import com.elcom.vitalsign.service.MeasureService;
import com.elcom.vitalsign.validation.MeasureDataValidation;
import java.util.List;

/**
 *
 * @author anhdv
 */
@Controller
public class MeasureDataBusiness extends BaseController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureDataBusiness.class);
    
    @Autowired
    private MeasureService measureService;
    
    @Autowired
    private RabbitMQClient rabbitMQClient;
    
    /**
     * Lấy dữ liệu đo BP cuối cùng theo list healthProfileId truyền vào
     * VitalsignUserSystemService gọi chéo sang, Ko check Auth, Ko check RBAC
     * @param urlParam (healthProfileIdLst)
     * @return List
     */
    public List<BpDataByHealthProfile> findDataBpLatestByHealthProfileIdLst(String urlParam) {
        if (StringUtil.isNullOrEmpty(urlParam))
            LOGGER.error(Constant.VALIDATION_INVALID_PARAM_VALUE);
        else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String healthProfileIdLst = (String) params.get("healthProfileIdLst");
            if( StringUtil.isNullOrEmpty(healthProfileIdLst) )
                return null;
            return this.measureService.findDataBpLatestByHealthProfileIdLst(healthProfileIdLst.split(","));
        }
        return null;
    }
    
    /**
     * Lấy dữ liệu đo SPO2 cuối cùng theo list healthProfileId truyền vào
     * VitalsignUserSystemService gọi chéo sang, Ko check Auth, Ko check RBAC
     * @param urlParam (healthProfileIdLst)
     * @return List
     */
    public List<Spo2DataByHealthProfile> findDataSpo2LatestByHealthProfileIdLst(String urlParam) {
        if (StringUtil.isNullOrEmpty(urlParam))
            LOGGER.error(Constant.VALIDATION_INVALID_PARAM_VALUE);
        else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String healthProfileIdLst = (String) params.get("healthProfileIdLst");
            if( StringUtil.isNullOrEmpty(healthProfileIdLst) )
                return null;
            return this.measureService.findDataSpo2LatestByHealthProfileIdLst(healthProfileIdLst.split(","));
        }
        return null;
    }
    
    /**
     * Lấy dữ liệu đo TEMP cuối cùng theo list healthProfileId truyền vào
     * VitalsignUserSystemService gọi chéo sang, Ko check Auth, Ko check RBAC
     * @param urlParam (healthProfileIdLst)
     * @return List
     */
    public List<TempDataByHealthProfile> findDataTempLatestByHealthProfileIdLst(String urlParam) {
        if (StringUtil.isNullOrEmpty(urlParam))
            LOGGER.error(Constant.VALIDATION_INVALID_PARAM_VALUE);
        else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String healthProfileIdLst = (String) params.get("healthProfileIdLst");
            if( StringUtil.isNullOrEmpty(healthProfileIdLst) )
                return null;
            return this.measureService.findDataTempLatestByHealthProfileIdLst(healthProfileIdLst.split(","));
        }
        return null;
    }
    
    /**
     * Lấy dữ liệu đo BP trong khoảng thời gian cụ thể (ngày hiện tại, tháng hiện tại)
     * VitalsignUserSystemService gọi chéo sang, Ko check Auth, Ko check RBAC
     * @param urlParam (health-profile-id, filter-type)
     * @param headerMap
     * @return List
     */
    public ResponseMessage findDataBpWithRangeByHealthProfileId(String urlParam, Map<String, String> headerMap) {
        ResponseMessage response;
        if (StringUtil.isNullOrEmpty(urlParam))
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        else {
            AuthorizationResponseDTO auth = authenToken(headerMap);
            if (auth == null)
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            /*else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));*/
            else {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String healthProfileId = (String) params.get("health-profile-id");
                String filterType = (String) params.get("filter-type");

                String msgValidation = new MeasureDataValidation().validateFindDataWithRangeByHealthProfileId(healthProfileId, filterType);
                if( msgValidation!=null )
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), msgValidation,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), msgValidation, null));
                else {
                    List<BpDataByHealthProfile> resultLst = this.measureService.findDataBpWithRangeByHealthProfileId(healthProfileId, filterType);
                    if( resultLst==null || resultLst.isEmpty() )
                        response = new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    else
                        response = new ResponseMessage(new MessageContent(resultLst));
                }
            }
        }
        return response;
    }
    
    /**
     * Lấy dữ liệu đo SPO2 trong khoảng thời gian cụ thể (ngày hiện tại, tháng hiện tại)
     * VitalsignUserSystemService gọi chéo sang, Ko check Auth, Ko check RBAC
     * @param urlParam (health-profile-id, filter-type)
     * @param headerMap
     * @return List
     */
    public ResponseMessage findDataSpo2WithRangeByHealthProfileId(String urlParam, Map<String, String> headerMap) {
        ResponseMessage response;
        if (StringUtil.isNullOrEmpty(urlParam))
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        else {
            AuthorizationResponseDTO auth = authenToken(headerMap);
            if (auth == null)
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            /*else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));*/
            else {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String healthProfileId = (String) params.get("health-profile-id");
                String filterType = (String) params.get("filter-type");

                String msgValidation = new MeasureDataValidation().validateFindDataWithRangeByHealthProfileId(healthProfileId, filterType);
                if( msgValidation!=null )
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), msgValidation,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), msgValidation, null));
                else {
                    List<Spo2DataByHealthProfile> resultLst = this.measureService.findDataSpo2WithRangeByHealthProfileId(healthProfileId, filterType);
                    if( resultLst==null || resultLst.isEmpty() )
                        response = new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    else
                        response = new ResponseMessage(new MessageContent(resultLst));
                }
            }
        }
        return response;
    }
    
    /**
     * Lấy dữ liệu đo TEMP trong khoảng thời gian cụ thể (ngày hiện tại, tháng hiện tại)
     * VitalsignUserSystemService gọi chéo sang, Ko check Auth, Ko check RBAC
     * @param urlParam (health-profile-id, filter-type)
     * @param headerMap
     * @return List
     */
    public ResponseMessage findDataTempWithRangeByHealthProfileId(String urlParam, Map<String, String> headerMap) {
        ResponseMessage response;
        if (StringUtil.isNullOrEmpty(urlParam))
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
        else {
            AuthorizationResponseDTO auth = authenToken(headerMap);
            if (auth == null)
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            /*else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));*/
            else {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String healthProfileId = (String) params.get("health-profile-id");
                String filterType = (String) params.get("filter-type");

                String msgValidation = new MeasureDataValidation().validateFindDataWithRangeByHealthProfileId(healthProfileId, filterType);
                if( msgValidation!=null )
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), msgValidation,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), msgValidation, null));
                else {
                    List<TempDataByHealthProfile> resultLst = this.measureService.findDataTempWithRangeByHealthProfileId(healthProfileId, filterType);
                    if( resultLst==null || resultLst.isEmpty() )
                        response = new ResponseMessage(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND,
                                new MessageContent(HttpStatus.OK.value(), Constant.VALIDATION_DATA_NOT_FOUND, null));
                    else
                        response = new ResponseMessage(new MessageContent(resultLst));
                }
            }
        }
        return response;
    }
    
    /** Insert dữ liệu đo của bệnh nhân
     *  Service cho CMS, có check Auth và RBAC
     * @param bodyParam
     * @param headerMap
     * @param methodType
     * @param requestPath
     * @return 200|400|500 */
    public ResponseMessage saveMeasureData(Map<String, Object> bodyParam, Map<String, String> headerMap, String methodType, String requestPath) {
        ResponseMessage response = null;
        try {
            AuthorizationResponseDTO auth = authenToken(headerMap);
            if( auth == null )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Bạn chưa đăng nhập"));
            else if( !authorizeRBAC(methodType, auth.getUuid(), requestPath) )
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                           new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), String.format("Bạn không có quyền [%s] [%s]", methodType, requestPath)));
            else {
                if (bodyParam == null || bodyParam.isEmpty())
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE,
                               new MessageContent(HttpStatus.BAD_REQUEST.value(), Constant.VALIDATION_INVALID_PARAM_VALUE, null));
                else {
                    
                    long startTime = System.currentTimeMillis();
                    DataTempDTO item = new MeasureDataValidation().validateInsertMeasureData(bodyParam);
                    LOGGER.info("Elapsed pasre json [{}] ms", getElapsedTime(System.currentTimeMillis() - startTime));
                    
                    if( item.getValidationMsg()!=null )
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), item.getValidationMsg(),
                                   new MessageContent(HttpStatus.BAD_REQUEST.value(), item.getValidationMsg(), null));
                    else {
//                        for( DataTempRowsDTO i : item.getData() ) {
//                            System.out.println("measureId: " + i.getMeasureId() + " - temp: " + i.getTemp() + " - ts: " + i.getTs());
//                        }
                        response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString()
                                    , new MessageContent(HttpStatus.OK.value(), HttpStatus.OK.toString(), true));
                    }
                    
                    /*PointType pointType = new PointType(id !=null ? id.longValue() : null, code, description, isActive);
                    String validationMsg = new ScoreManagementValidation().validateSavePointType(pointType, saveType);
                    if( validationMsg != null )
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), validationMsg,
                                   new MessageContent(HttpStatus.BAD_REQUEST.value(), validationMsg, null));
                    else {
                        boolean result = this.scoreManagementService.savePointType(pointType);
                        if( !result )
                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                       new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), result));
                        else
                            response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString()
                                        , new MessageContent(HttpStatus.OK.value(), HttpStatus.OK.toString(), result));
                    }*/
                }
            }
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return response;
    }
}
