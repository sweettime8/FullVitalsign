/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.repository;

import com.elcom.rbac.model.Role;
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
public interface RoleRepository extends CrudRepository<Role, Long> {
    
    Role findByRoleCode(String roleCode);
    
    Role findByIsAdmin(Integer isAdmin);
    
    List<Role> findByIsDeleteNot(Integer isDelete);
    
    List<Role> findByIsAdminAndIsDelete(Integer isAdmin, Integer isDelete);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Role r SET r.roleName = :roleName WHERE r.roleCode = :roleCode")
    int updateRole(@Param("roleCode") String roleCode, @Param("roleName") String roleName);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Role r SET r.isDelete = 1 WHERE r.roleCode = :roleCode")
    int deleteRole(@Param("roleCode") String roleCode);
}
