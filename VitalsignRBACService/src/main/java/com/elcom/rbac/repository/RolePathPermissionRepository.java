/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.repository;

import com.elcom.rbac.model.Path;
import com.elcom.rbac.model.Role;
import com.elcom.rbac.model.RolePathPermission;
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
public interface RolePathPermissionRepository extends CrudRepository<RolePathPermission, Long> {

    List<RolePathPermission> findByRoleCodeInAndApiPathInOrderByCanCreateDescCanReadDescCanUpdateDescCanDeleteDesc(List<Role> roleCode, List<Path> moduleCode);

    List<RolePathPermission> findByRoleCodeInAndApiPathInOrderByCanCreateDesc(List<Role> roleCode, List<Path> moduleCode);

    List<RolePathPermission> findByRoleCodeInAndApiPathInOrderByCanReadDesc(List<Role> roleCode, List<Path> moduleCode);

    List<RolePathPermission> findByRoleCodeInAndApiPathInOrderByCanUpdateDesc(List<Role> roleCode, List<Path> moduleCode);

    List<RolePathPermission> findByRoleCodeInAndApiPathInOrderByCanDeleteDesc(List<Role> roleCode, List<Path> moduleCode);
    
    RolePathPermission findByRoleCodeAndApiPath(Role roleCode, Path apiPath);
    
    List<RolePathPermission> findByServiceCodeAndRoleCodeAndApiPathAndIsDeleteNotOrderByRoleCodeAsc(Service serviceCode, Role roleCode, Path apiPath, Integer isDelete);
    
    List<RolePathPermission> findByServiceCodeAndRoleCodeAndIsDeleteNotOrderByRoleCodeAsc(Service serviceCode, Role roleCode, Integer isDelete);
    
    List<RolePathPermission> findByServiceCodeAndApiPathAndIsDeleteNotOrderByRoleCodeAsc(Service serviceCode, Path apiPath, Integer isDelete);
    
    List<RolePathPermission> findByServiceCodeAndIsDeleteNotOrderByRoleCodeAsc(Service serviceCode, Integer isDelete);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RolePathPermission rmp SET rmp.canCreate = :canCreate, rmp.canUpdate = :canUpdate, "
            + "rmp.canRead = :canRead, rmp.canDelete = :canDelete WHERE rmp.roleCode.roleCode = :roleCode "
            + " AND rmp.apiPath.apiPath = :apiPath ")
    int updateRolePathPermission(@Param("roleCode") String roleCode, @Param("apiPath") String apiPath, 
            @Param("canCreate") int canCreate, @Param("canUpdate") int canUpdate, 
            @Param("canRead") int canRead, @Param("canDelete") int canDelete);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RolePathPermission rmp SET rmp.isDelete = 1 WHERE rmp.roleCode.roleCode = :roleCode "
            + " AND rmp.apiPath.apiPath = :apiPath")
    int deleteRolePathPermission(@Param("roleCode") String roleCode, @Param("apiPath") String apiPath);
}
