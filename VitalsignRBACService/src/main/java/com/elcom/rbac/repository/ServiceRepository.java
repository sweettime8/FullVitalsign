/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.repository;

import com.elcom.rbac.model.Service;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
public interface ServiceRepository extends CrudRepository<Service, Long> {

    Service findByServiceCode(String serviceCode);

    @Override
    List<Service> findAll();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Service s SET s.serviceName = :serviceName WHERE s.serviceCode = :serviceCode")
    int updateService(@Param("serviceCode") String serviceCode, @Param("serviceName") String serviceName);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Service s SET s.isDelete = 1 WHERE s.serviceCode = :serviceCode")
    int deleteService(@Param("serviceCode") String serviceCode);
}
