/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service;

import com.elcom.rbac.model.Path;
import com.elcom.rbac.model.Role;
import com.elcom.rbac.model.RolePathPermission;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Admin
 */
public interface RoleModulePermissionService {
    
    List<RolePathPermission> findByRoleAndPath(String requestMethod, List<Role> roleCode, List<Path> apiPath);
    
    RolePathPermission findByRoleAndPath(Role roleCode, Path apiPath);
    
    Optional<RolePathPermission> findById(Long id);
    
    List<RolePathPermission> findByServiceAndRoleAndPath(String serviceCode, String roleCode, String apiPath);

    void save(RolePathPermission permission);

    boolean update(RolePathPermission permission, int canCreate, int canRead, int canUpdate, int canDelete);

    boolean remove(RolePathPermission permission);
}
