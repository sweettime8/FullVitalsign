package com.elcom.vitalsign.validation;

import com.elcom.vitalsign.constant.Constant;
import com.elcom.vitalsign.model.dto.DataTempDTO;
import com.elcom.vitalsign.model.dto.DataTempRowsDTO;
import com.elcom.vitalsign.utils.StringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasureDataValidation extends AbstractValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureDataValidation.class);
    
    public String validateFindDataWithRangeByHealthProfileId(String healthProfileId, String filterType) {
        
        if( StringUtil.isNullOrEmpty(healthProfileId) )
            getMessageDes().add("healthProfileId không được để trống");
        else if( !StringUtil.isValidUuid(healthProfileId) )
            getMessageDes().add("healthProfileId (UUID) chứa giá trị không hợp lệ: [" + healthProfileId.trim() + "]");
        
        if( StringUtil.isNullOrEmpty(filterType) )
            getMessageDes().add("filterType không được để trống");
        else if( !Arrays.asList(Constant.DATA_FILTER_LST).contains(filterType.trim()) )
            getMessageDes().add("filterType chứa giá trị không hợp lệ: ["+filterType.trim()+"]");
        
        return !isValid() ? this.buildValidationMessage() : null;
    }
    
    public DataTempDTO validateInsertMeasureData(Map<String, Object> bodyParam) {
        
        DataTempDTO result = new DataTempDTO();
        
        String gateId = (String) bodyParam.get("gateId");
        if( StringUtil.isNullOrEmpty(gateId) )
            return new DataTempDTO("gateId không được để trống");
        result.setGateId(gateId);
        
        String patientId = (String) bodyParam.get("patientId");
        if( StringUtil.isNullOrEmpty(patientId) )
            return new DataTempDTO("patientId không được để trống");
        result.setPatientId(patientId);
        
        List<DataTempRowsDTO> lst;
        try {
            lst = (List<DataTempRowsDTO>) bodyParam.get("data");
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return new DataTempDTO("data rows không hợp lệ");
        }
        if( lst!=null && !lst.isEmpty() ) {
            try {
                lst = new ObjectMapper().convertValue(lst, new TypeReference<List<DataTempRowsDTO>>(){});
            } catch (Exception e) {
                LOGGER.error(e.toString());
                return new DataTempDTO("data rows không hợp lệ");
            }
            if( lst!=null && !lst.isEmpty() )
                result.setData(lst);
        }else
            return new DataTempDTO("data rows không được để trống");
        return result;
    }
}
