package com.elcom.notify.service.impl;

import com.elcom.notify.model.dto.PatientByEmployeeDTO;
import com.elcom.notify.repository.HospitalCustomizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.elcom.notify.utils.StringUtil;
import com.elcom.notify.service.HospitalService;

/**
 *
 * @author anhdv
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalServiceImpl.class);
    
    @Autowired
    private HospitalCustomizeRepository hospitalRepository;
    
    @Override
    public List<PatientByEmployeeDTO> findPatientByEmployee(String employeeId) {
        List<PatientByEmployeeDTO> lst = null;
        try {
            lst = this.hospitalRepository.findPatientByEmployee(employeeId);
        }catch(Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return lst;
    } 
}
