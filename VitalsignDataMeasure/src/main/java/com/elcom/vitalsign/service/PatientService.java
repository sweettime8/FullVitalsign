/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.service;

import com.elcom.vitalsign.model.Patient;

/**
 *
 * @author admin
 */
public interface PatientService {

    Patient findByUuid(String id);

    Patient findByPatientCode(String patientCode);

    void save(Patient item);

    void remove(Patient item);
}
