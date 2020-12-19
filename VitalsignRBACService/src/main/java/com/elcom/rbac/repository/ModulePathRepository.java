/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.repository;

import com.elcom.rbac.model.Module;
import com.elcom.rbac.model.ModulePath;
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
public interface ModulePathRepository extends CrudRepository<ModulePath, Long> {

    List<ModulePath> findByApiPathOrderByModuleCodeAsc(String apiPath);

    ModulePath findByModuleCodeAndApiPath(Module moduleCode, String apiPath);
    
    List<ModulePath> findByServiceCodeAndIsDeleteNotOrderByCreatedAtAsc(Service serviceCode, Integer isDelete);
    
    List<ModulePath> findByModuleCodeAndIsDeleteOrderByCreatedAtAsc(Module moduleCode, Integer isDelete);
    
    List<ModulePath> findByServiceCodeAndModuleCodeAndIsDeleteNotOrderByCreatedAtAsc(Service serviceCode, 
            Module moduleCode, Integer isDelete);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ModulePath mp SET mp.apiPath = :newPath "
            + " WHERE mp.moduleCode.moduleCode = :moduleCode AND mp.apiPath = :apiPath")
    int updateModulePath(@Param("moduleCode") String moduleCode, @Param("apiPath") String apiPath,
            @Param("newPath") String newPath);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ModulePath mp SET mp.isDelete = 1 WHERE mp.moduleCode.moduleCode = :moduleCode "
            + " AND mp.apiPath = :apiPath")
    int deleteModulePath(@Param("moduleCode") String roleCode, @Param("apiPath") String apiPath);
}
