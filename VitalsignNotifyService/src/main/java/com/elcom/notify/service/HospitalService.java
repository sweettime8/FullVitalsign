package com.elcom.notify.service;

import com.elcom.notify.model.dto.PatientByEmployeeDTO;
import java.util.List;

/**
 *
 * @author anhdv
 */
public interface HospitalService {

    List<PatientByEmployeeDTO> findPatientByEmployee(String employeeId);
}