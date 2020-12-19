/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.usersystem.repository;

import com.elcom.usersystem.model.DoctorHomeAdminMaps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ducnh
 */
@Repository
public interface DoctorHomeAdminMapRepository extends JpaRepository<DoctorHomeAdminMaps, String> {
    
}
