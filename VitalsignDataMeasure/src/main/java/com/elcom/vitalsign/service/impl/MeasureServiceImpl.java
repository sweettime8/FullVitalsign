package com.elcom.vitalsign.service.impl;

import com.elcom.vitalsign.model.dto.BpDataByHealthProfile;
import com.elcom.vitalsign.model.dto.Spo2DataByHealthProfile;
import com.elcom.vitalsign.model.dto.TempDataByHealthProfile;
import com.elcom.vitalsign.repository.measuredata.DataBpCustomizeRepository;
import com.elcom.vitalsign.repository.measuredata.DataSpo2CustomizeRepository;
import com.elcom.vitalsign.repository.measuredata.DataTempCustomizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.elcom.vitalsign.service.MeasureService;
import java.util.List;

/**
 *
 * @author anhdv
 */
@Service
public class MeasureServiceImpl implements MeasureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureServiceImpl.class);
    
    //@Autowired
    //private DataMeasureCustomizeRepository dataMeasureCustomizeRepository;
    @Autowired
    private DataBpCustomizeRepository dataBpCustomizeRepository;
    @Autowired
    private DataSpo2CustomizeRepository dataSpo2CustomizeRepository;
    @Autowired
    private DataTempCustomizeRepository dataTempCustomizeRepository;
    
    @Override
    public List<BpDataByHealthProfile> findDataBpLatestByHealthProfileIdLst(String[] healthProfileIdLst) {
        return this.dataBpCustomizeRepository.findDataBpLatestByHealthProfileIdLst(healthProfileIdLst);
    }
    @Override
    public List<BpDataByHealthProfile> findDataBpWithRangeByHealthProfileId(String healthProfileId, String filterType) {
        return this.dataBpCustomizeRepository.findDataBpWithRangeByHealthProfileId(healthProfileId, filterType);
    }
    
    @Override
    public List<Spo2DataByHealthProfile> findDataSpo2LatestByHealthProfileIdLst(String[] healthProfileIdLst) {
        return this.dataSpo2CustomizeRepository.findDataSpo2LatestByHealthProfileIdLst(healthProfileIdLst);
    }
    @Override
    public List<Spo2DataByHealthProfile> findDataSpo2WithRangeByHealthProfileId(String healthProfileId, String filterType) {
        return this.dataSpo2CustomizeRepository.findDataSpo2WithRangeByHealthProfileId(healthProfileId, filterType);
    }
    
    @Override
    public List<TempDataByHealthProfile> findDataTempLatestByHealthProfileIdLst(String[] healthProfileIdLst) {
        return this.dataTempCustomizeRepository.findDataTempLatestByHealthProfileIdLst(healthProfileIdLst);
    }
    @Override
    public List<TempDataByHealthProfile> findDataTempWithRangeByHealthProfileId(String healthProfileId, String filterType) {
        return this.dataTempCustomizeRepository.findDataTempWithRangeByHealthProfileId(healthProfileId, filterType);
    }
    
    @Override
    public boolean saveMeasureData(String employeeId) {
        return true;
//        List<PatientByEmployeeDTO> lst = null;
//        try {
//            lst = this.hospitalRepository.findPatientByEmployee(employeeId);
//        }catch(Exception ex) {
//            LOGGER.error(StringUtil.printException(ex));
//        }
//        return lst;
    } 
}
