package com.elcom.vitalsign.service;

import com.elcom.vitalsign.model.dto.BpDataByHealthProfile;
import com.elcom.vitalsign.model.dto.Spo2DataByHealthProfile;
import com.elcom.vitalsign.model.dto.TempDataByHealthProfile;
import java.util.List;

/**
 *
 * @author anhdv
 */
public interface MeasureService {

    public List<BpDataByHealthProfile> findDataBpLatestByHealthProfileIdLst(String[] healthProfileIdLst);
    public List<BpDataByHealthProfile> findDataBpWithRangeByHealthProfileId(String healthProfileId, String filterType);
    
    public List<Spo2DataByHealthProfile> findDataSpo2LatestByHealthProfileIdLst(String[] healthProfileIdLst);
    public List<Spo2DataByHealthProfile> findDataSpo2WithRangeByHealthProfileId(String healthProfileId, String filterType);
    
    public List<TempDataByHealthProfile> findDataTempLatestByHealthProfileIdLst(String[] healthProfileIdLst);
    public List<TempDataByHealthProfile> findDataTempWithRangeByHealthProfileId(String healthProfileId, String filterType);
    
    boolean saveMeasureData(String employeeId);
}