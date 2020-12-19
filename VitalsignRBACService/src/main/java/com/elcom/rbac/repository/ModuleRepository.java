/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.repository;

import com.elcom.rbac.model.Module;
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
public interface ModuleRepository extends CrudRepository<Module, Long> {
    
    Module findByModuleCode(String moduleCode);
    
    List<Module> findByServiceCodeAndIsDeleteNot(Service serviceCode, Integer isDelete);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Module m SET m.moduleName = :moduleName WHERE m.moduleCode = :moduleCode")
    int updateModule(@Param("moduleCode") String moduleCode, @Param("moduleName") String moduleName);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Module m SET m.isDelete = 1 WHERE m.moduleCode = :moduleCode")
    int deleteModule(@Param("moduleCode") String moduleCode);
}
