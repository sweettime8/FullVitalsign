package com.elcom.fileservice.validation;

import com.elcom.fileservice.config.UploadConfig;
import com.elcom.fileservice.model.dto.UploadDTO;
import com.elcom.fileservice.utils.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public class FileValidation extends AbstractValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileValidation.class);
    
    public String validateUpload(String requestPath, String service, MultipartFile[] files, UploadDTO dto) {
        
        if (StringUtil.isNullOrEmpty(requestPath))
            getMessageDes().add("Path request không được trống");
        
        //Service
        if (StringUtil.isNullOrEmpty(service) || !UploadConfig.UPLOAD_SERVICE_LIST.contains(service)) {
            getMessageDes().add("Service request không tồn tại");
        }
        //Path
        List<String> pathList = UploadConfig.UPLOAD_SERVICE_PATH_MAP.get(service);
        if (pathList == null || pathList.isEmpty() || !pathList.contains(requestPath)) {
            getMessageDes().add("Request path không tồn tại");
        }
        //File list
        if (files == null || files.length == 0) {
            getMessageDes().add("File không được trống");
        }else {
           for (MultipartFile file : files) {
               try {
                   LOGGER.info("byteSize: " + file.getBytes().length);
               } catch (IOException ex) {
                   ex.printStackTrace();
               }
                //Content type
                String contentType = file.getContentType();
                LOGGER.info("UploadValidation - validate - content type : " + contentType);
                if (dto == null || (dto.getAccept() != null && !dto.getAccept().equals("*") && !dto.getAccept().contains(contentType)))
                    getMessageDes().add("Kiểu file upload không hợp lệ");
                else {
                    //Check by mime magic
                    try {
                        InputStream initialStream = file.getInputStream();
                        byte[] buffer = new byte[initialStream.available()];
                        initialStream.read(buffer);
                        MagicMatch match = Magic.getMagicMatch(buffer, false);
                        if(match != null){
                            contentType = match.getMimeType();
                            LOGGER.info("UploadValidation - validate by Mime Magic - content type : " + contentType);
                            if ( (dto.getAccept() != null && !dto.getAccept().equals("*") && !dto.getAccept().contains(contentType)) )
                                getMessageDes().add("Kiểu file upload " + contentType + " không hợp lệ");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                //File size
                if (dto == null || dto.getMaxSize() == null || dto.getMaxSize().compareTo(file.getSize()) < 0)
                    getMessageDes().add("File upload dung lượng tối đa " + (dto != null ? dto.getMaxSize() / 1024 + " KB" : " chưa định nghĩa"));
            } 
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }
}
