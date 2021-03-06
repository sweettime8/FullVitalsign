/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.id.repository;

import com.elcom.id.model.PatientProfile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ducnh
 */
@Repository
public interface PatientProfileRepository extends PagingAndSortingRepository<PatientProfile, String>, JpaSpecificationExecutor<PatientProfile>  {
    
}
