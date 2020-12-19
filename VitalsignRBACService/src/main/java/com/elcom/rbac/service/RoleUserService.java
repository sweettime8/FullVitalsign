/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service;

import com.elcom.rbac.model.Role;
import java.util.List;
import com.elcom.rbac.model.RoleUser;
import com.elcom.rbac.model.dto.RoleUserPagingDTO;
import java.util.Optional;

/**
 *
 * @author Admin
 */
public interface RoleUserService {

    List<RoleUser> findByUuidUser(String uuidUser);
    
    List<RoleUser> findByRoleCode(Role roleCode);

    RoleUser findByUuidUserAndRoleCode(String uuidUser, Role roleCode);
    
    RoleUser findByUuidUserAndRoleCodeAndIsDelete(String uuidUser, Role roleCode, Integer isDelete);
    
    RoleUser findByUuidUserAndRoleCodeInAndIsDelete(String uuidUser, List<Role> roleCode, Integer isDelete);
    
    Optional<RoleUser> findById(Long id);

    void save(RoleUser role);

    boolean update(RoleUser role, String newRole);

    boolean remove(RoleUser role);
    
    RoleUserPagingDTO findAdmin(String roleCode, Integer currentPage, Integer rowsPerPage, String sort);
}
