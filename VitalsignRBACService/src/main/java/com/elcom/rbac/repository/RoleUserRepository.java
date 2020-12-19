/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.repository;

import com.elcom.rbac.model.Role;
import com.elcom.rbac.model.RoleUser;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
public interface RoleUserRepository extends CrudRepository<RoleUser, Long> {
    
    //@Query("SELECT t FROM RoleUser t WHERE t.uuidUser = ?1")
    List<RoleUser> findByUuidUserAndIsDeleteOrderByRoleCodeAsc(String uuidUser, Integer isDelete);
    
    List<RoleUser> findByRoleCodeAndIsDelete(Role roleCode, Integer isDelete);
    
    RoleUser findByUuidUserAndRoleCode(String uuidUser, Role roleCode);
    
    RoleUser findByUuidUserAndRoleCodeAndIsDelete(String uuidUser, Role roleCode, Integer isDelete);
    
    RoleUser findByUuidUserAndRoleCodeInAndIsDelete(String uuidUser, List<Role> roleCode, Integer isDelete);
    
    Page<RoleUser> findByRoleCodeNotAndIsDelete(Role roleCode, Integer isDelete, Pageable pageable);
    
    Page<RoleUser> findByRoleCodeAndIsDelete(Role roleCode, Integer isDelete, Pageable pageable);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RoleUser ru SET ru.roleCode.roleCode = :newRole WHERE ru.uuidUser = :uuidUser "
            + " AND ru.roleCode.roleCode = :roleCode ")
    int updateRoleUser(@Param("uuidUser") String uuidUser, @Param("roleCode") String roleCode, 
            @Param("newRole") String newRole);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RoleUser ru SET ru.isDelete = 1 WHERE ru.uuidUser = :uuidUser "
            + " AND ru.roleCode.roleCode = :roleCode")
    int deleteRoleUser(@Param("uuidUser") String uuidUser, @Param("roleCode") String roleCode);
}
