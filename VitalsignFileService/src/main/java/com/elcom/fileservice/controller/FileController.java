package com.elcom.fileservice.controller;

import com.elcom.gateway.message.RequestMessage;
import com.elcom.gateway.message.ResponseMessage;
import com.elcom.fileservice.business.MeasureDataBusiness;
import com.elcom.fileservice.config.PropertiesConfig;
import com.elcom.fileservice.config.UploadConfig;
import com.elcom.fileservice.constant.Constant;
import com.elcom.fileservice.exception.ValidationException;
import com.elcom.fileservice.messaging.rabbitmq.RabbitMQClient;
import com.elcom.fileservice.messaging.rabbitmq.RabbitMQProperties;
import com.elcom.fileservice.model.dto.MqttResourceInfoDTO;
import com.elcom.fileservice.model.dto.ResponseMessageDTO;
import com.elcom.fileservice.model.dto.UploadDTO;
import com.elcom.fileservice.service.impl.FileStorageServiceImpl;
import com.elcom.fileservice.upload.UploadFileResponse;
import com.elcom.fileservice.utils.DateUtil;
import com.elcom.fileservice.utils.JSONConverter;
import com.elcom.fileservice.utils.StringUtil;
import com.elcom.fileservice.validation.FileValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.junrar.Junrar;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/v1.0")
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageServiceImpl fileStorageService;

    @Autowired
    private RabbitMQClient rabbitMQClient;

    @Autowired
    private MeasureDataBusiness measureDataBusiness;
    
    @Value("${user.authen.use}")
    private String authenUse;

    @Value("${user.authen.http.url}")
    private String authenHttpUrl;

    /**
     * Upload file
     *
     * @param files
     * @param headerMap
     * @param request
     * @return image upload link
     */
    @RequestMapping(value = "/upload/**", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadFile(@RequestParam(value = "file", required = false) MultipartFile[] files,
            @RequestHeader Map<String, String> headerMap, HttpServletRequest request) throws ValidationException, IOException {
        //Request path
        String requestPath = request.getRequestURI();
        
        if( StringUtil.isNullOrEmpty(requestPath) )
            return new ResponseEntity(new ResponseMessageDTO("Request không hợp lệ"), HttpStatus.BAD_REQUEST);
        
        if ( requestPath.contains(Constant.SRV_VER) )
            requestPath = requestPath.replace(Constant.SRV_VER, "/");
        
        int index = requestPath.indexOf("/", "/upload/".length());
        String service;
        if (index != -1)
            service = requestPath.substring("/upload/".length(), index);
        else
            service = requestPath.replace("/upload/", "");
        
        LOGGER.info("requestPath: [{}], service: [{}]", requestPath, service);
        UploadDTO uploadDTO = UploadConfig.UPLOAD_DEFINE_MAP.get(requestPath);
        //Validation
        String validationMsg = new FileValidation().validateUpload(requestPath, service, files, uploadDTO);
        if( validationMsg != null )
            return new ResponseEntity(new ResponseMessageDTO(validationMsg), HttpStatus.BAD_REQUEST);
        
        //Authen
        ResponseMessage response = null;
        if ("http".equalsIgnoreCase(authenUse)) {
            LOGGER.info("Http authen - authorization " + headerMap.get("authorization"));
            // Http -> Call api authen
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", headerMap.get("authorization"));
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Dữ liệu đính kèm theo yêu cầu.
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ResponseMessage> result = restTemplate.exchange(authenHttpUrl, HttpMethod.GET, requestEntity, ResponseMessage.class);
            if (result != null && result.getStatusCode() == HttpStatus.OK)
                response = result.getBody();
            
            LOGGER.info("Http authen response : {}", response != null ? response.toJsonString() : null);
        } else {
            //Authen -> call rpc authen headerMap
            RequestMessage userRpcRequest = new RequestMessage();
            userRpcRequest.setRequestMethod("POST");
            userRpcRequest.setRequestPath(RabbitMQProperties.USER_RPC_AUTHEN_URL);
            userRpcRequest.setBodyParam(null);
            userRpcRequest.setUrlParam(null);
            userRpcRequest.setHeaderParam(headerMap);
            LOGGER.info("Call RPC authen - authorization " + headerMap.get("authorization"));
            LOGGER.info("RequestMessage userRpcRequest : " + userRpcRequest.toJsonString());
            String result = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                            RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, userRpcRequest.toJsonString());
            LOGGER.info("RPC authen response : {}", result);
            if (result != null) {
                ObjectMapper mapper = new ObjectMapper();
                //DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //mapper.setDateFormat(df);
                try {
                    response = mapper.readValue(result, ResponseMessage.class);
                } catch (JsonProcessingException ex) {
                    LOGGER.info("Lỗi parse json khi gọi user service verify: " + ex.toString());
                }
            }
        }

        if (response != null && response.getStatus() == HttpStatus.OK.value()) {
            //Process upload
            String ddmmyyyy = DateUtil.today("ddMMyyyy");
            String uploadDir = uploadDTO.getFolder();

            List<UploadFileResponse> list = new ArrayList<>();
            UploadFileResponse uploadFileResponse;
            
            //Xử lý cho upload data bệnh nhân(giải nén, đọc file json, đưa vào queue, lấy và insert DB + các tác vụ thông báo....)
            if( "measure-data".equals(service) ) {
                for (MultipartFile file : files) {
                    String fileName = file.getOriginalFilename();
                    if( fileName!=null && fileName.contains(".") ) {
                       File extractFolder = new File(fileName.substring(0, fileName.lastIndexOf(".")));
                        if( !extractFolder.exists() || !extractFolder.isDirectory() )
                            extractFolder.mkdir();

                        List<File> extractFiles = null;
                        File tmpConvertedFile = null;
                        try {
                            tmpConvertedFile = multipartFileToFile(file);
                            if( tmpConvertedFile!=null )
                                extractFiles = Junrar.extract(tmpConvertedFile, extractFolder);
                        } catch (Exception ex) {
                            LOGGER.error(StringUtil.printException(ex));
                        } finally {
                            if( tmpConvertedFile!=null );
                                LOGGER.info("tmpConvertedFile.delete: {}", tmpConvertedFile.delete());
                        }
                        if( extractFiles!=null && !extractFiles.isEmpty() ) {
                            for( File extractFile : extractFiles ) {
                                if( requestPath.endsWith("/measure-data/temp") )
                                    this.measureDataBusiness.saveDataTemp(extractFile);
                                else if( requestPath.endsWith("/measure-data/bp") )
                                    this.measureDataBusiness.saveDataBp(extractFile);
                                else if( requestPath.endsWith("/measure-data/spo2") )
                                    this.measureDataBusiness.saveDataSpo2(extractFile);
                            }
                        }
                        LOGGER.info("extractFolder.delete: {}", extractFolder.delete());
                    }
                }
            }else { // Xử lý lưu file upload thông thường.
                for (MultipartFile file : files) {
                    String fileName = fileStorageService.storeFile(file, uploadDir);
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path(Constant.SRV_VER + "/" + uploadDir.replace("{ddmmyyyy}", ddmmyyyy) + "/")
                            .path(fileName)
                            .toUriString();
                    LOGGER.info("Upload file url: " + fileDownloadUri);
                    uploadFileResponse = new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
                    list.add(uploadFileResponse);
                }
            }
            return new ResponseEntity(new ResponseMessageDTO(list), HttpStatus.OK);
        }else
            return new ResponseEntity(new ResponseMessageDTO("Token đăng nhập hết hạn"), HttpStatus.FORBIDDEN);
    }

    /**
     * View file upload
     *
     * @param request
     * @return file
     * @throws IOException
     */
    @RequestMapping(value = "/upload/**", method = RequestMethod.GET)
    public ResponseEntity<Resource> viewFile(HttpServletRequest request) throws IOException {
        String filePath = request.getRequestURI();
        if (filePath != null && filePath.contains("/v1.0/"))
            filePath = filePath.replace("/v1.0/", "");
        
        int lastIndex = filePath != null ? filePath.lastIndexOf("/") : -1;
        String fileName = lastIndex != -1 ? filePath.substring(lastIndex + 1) : filePath;

        // Fallback to the default content type if type could not be determined
        ContentDisposition contentDisposition = ContentDisposition.builder("inline").filename(fileName).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(filePath);
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            LOGGER.info("Không nhận dạng được kiểu file.ex: " + ex.toString());
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(resource.contentLength())
                .headers(headers)
                .body(resource);
    }
    
    /**
     * Xác thực thiết bị cần kết nối tới MQTT, nếu thiết bị hợp lệ thì trả thông tin MQTT kèm các file xác thực để kết nối MQTT
     *
     * @param requestBody
     * @return mqtt-cfg-files.zip chứa 3 files (*.crt, .*ca), header chứa thông tin kết nối mqtt (headerName: 'mqtt-conn-info')
     */
    @RequestMapping(value = "/fetch-mqtt-info", method = RequestMethod.POST, produces = "application/zip")
   public ResponseEntity<StreamingResponseBody> authDevice2ResMqttInfo(@RequestBody(required = true) Map<String, Object> requestBody) {
        if (requestBody == null || requestBody.isEmpty())
            return ResponseEntity.badRequest().body((StreamingResponseBody) (OutputStream out) -> {
                throw new ValidationException("payLoad Body is missing!");
            });
        
        String authToken = (String) requestBody.get("authToken");
        LOGGER.info("request.authToken: [{}]", authToken);
        
        if( StringUtil.isNullOrEmpty(authToken) )
            return ResponseEntity.badRequest().body((StreamingResponseBody) (OutputStream out) -> {
                throw new ValidationException("authToken không được trống");
            });
        
        Map<String, String> header = new HashMap<>();
        header.put("device-token", authToken);
        RequestMessage requestMessage = new RequestMessage("GET", Constant.SRV_VER + Constant.API_DEVICE_DECRYPT_DEVICE_TOKEN, null, null, null, header);
        String req = JSONConverter.toJSON(requestMessage);
        String deviceId = this.rabbitMQClient.callRpcService(RabbitMQProperties.VS_DEVICE_RPC_EXCHANGE
                                                    , RabbitMQProperties.VS_DEVICE_RPC_QUEUE_NAME
                                                    , RabbitMQProperties.VS_DEVICE_RPC_KEY, req);
        LOGGER.info(" Request ==> [{}]\n Response <== [{}]", req, deviceId);
        if( StringUtil.isNullOrEmpty(deviceId) )
            return ResponseEntity.badRequest().body((StreamingResponseBody) (OutputStream out) -> {
                throw new ValidationException("authToken after decrypt is invalid!");
            });
        
        // Check deviceId(gate | display) này có tồn tại trong hệ thống và đang hoạt động hay không? - Call rpc VitalsignDeviceService
        String urlParamSend = "deviceId=" + deviceId;
        requestMessage = new RequestMessage("GET", Constant.SRV_VER + Constant.API_DEVICE_CHECK_VALID_DEVICE_ID, urlParamSend, null, null, null);
        req = JSONConverter.toJSON(requestMessage);
        String userId = this.rabbitMQClient.callRpcService(RabbitMQProperties.VS_DEVICE_RPC_EXCHANGE
                                                    , RabbitMQProperties.VS_DEVICE_RPC_QUEUE_NAME
                                                    , RabbitMQProperties.VS_DEVICE_RPC_KEY, req);
        LOGGER.info(" Request ==> [{}]\n Response <== [{}]", req, userId);
        
        if( StringUtil.isNullOrEmpty(userId) )
            return ResponseEntity.badRequest().body((StreamingResponseBody) (OutputStream out) -> {
                throw new ValidationException("device not exist or inActive or userId not assaigned.");
            });
        
        // Lấy mqttInfo - Call rpc VitalsignDeviceService
        String reqFindMqttInfo = JSONConverter.toJSON(new RequestMessage("GET", Constant.SRV_VER + Constant.API_DEVICE_FIND_RESOURCE_INFO, "resourceCode=MQTT_BROKER", null, null, null));
        String resFindMqttInfo = this.rabbitMQClient.callRpcService(RabbitMQProperties.VS_DEVICE_RPC_EXCHANGE
                                                    , RabbitMQProperties.VS_DEVICE_RPC_QUEUE_NAME
                                                    , RabbitMQProperties.VS_DEVICE_RPC_KEY, reqFindMqttInfo);
        LOGGER.info(" Request ==> [{}]\n Response <== [{}]", reqFindMqttInfo, resFindMqttInfo);
        MqttResourceInfoDTO mqttResourceInfo = null;
        try {
            mqttResourceInfo = JSONConverter.toObject(resFindMqttInfo, MqttResourceInfoDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if( mqttResourceInfo==null || mqttResourceInfo.getIpAddress()==null )
            return ResponseEntity.badRequest().body((StreamingResponseBody) (OutputStream out) -> {
                throw new ValidationException("mqttResourceInfo not found or inActive.");
            });
        
        ContentDisposition contentDisposition = ContentDisposition.builder("inline").filename("mqtt-cfg-files.zip").build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);
        headers.add("user-id", userId);
        headers.add("mqtt-conn-info", JSONConverter.toJSON(mqttResourceInfo));

        return ResponseEntity.ok()
                .headers(headers)
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(out -> {
                    try (ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {
                        List<File> files = new ArrayList<>();
                        files.add(new File(PropertiesConfig.MQTT_SSL_CA_CERT));
                        files.add(new File(PropertiesConfig.MQTT_SSL_CLIENT_CERT));
                        files.add(new File(PropertiesConfig.MQTT_SSL_CLIENT_KEY));
                        // package files
                        for (File file : files) {
                            //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
                            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                IOUtils.copy(fileInputStream, zipOutputStream);
                            }
                            zipOutputStream.closeEntry();
                        }
                    }
                });
    }
    
    private File multipartFileToFile(MultipartFile file) throws IOException {
        File convFile = null;
        try {
            convFile = new File(PropertiesConfig.FILE_UPLOAD_BASE_DIR + "/" + file.getOriginalFilename());
            convFile.createNewFile();
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, convFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        return convFile;
    }
    
    private String getElapsedTime(long miliseconds) {
        //return (miliseconds / 1000.0) + "(s)";
        return miliseconds + " (ms)";
    }
}
