/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.repository;

import com.elcom.rbac.model.Path;
import com.elcom.rbac.model.Service;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
public interface PathRepository extends CrudRepository<Path, Integer> {
    
    List<Path> findByApiPath(String apiPath);

    Path findByServiceCodeAndApiPath(Service serviceCode, String apiPath);
    
    List<Path> findByServiceCodeAndIsDeleteNotOrderByCreatedAtAsc(Service serviceCode, Integer isDelete);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Path mp SET mp.apiPath = :newPath "
            + " WHERE mp.serviceCode.serviceCode = :serviceCode AND mp.apiPath = :apiPath")
    int updatePath(@Param("serviceCode") String serviceCode, @Param("apiPath") String apiPath,
            @Param("newPath") String newPath);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Path mp SET mp.isDelete = 1 WHERE mp.serviceCode.serviceCode = :serviceCode "
            + " AND mp.apiPath = :apiPath")
    int deletePath(@Param("serviceCode") String serviceCode, @Param("apiPath") String apiPath);
}
