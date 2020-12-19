/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service;

import com.elcom.rbac.model.Role;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Admin
 */
public interface RoleService {

    void save(Role role);

    boolean update(Role role);

    boolean remove(Role role);

    Role findByRoleCode(String roleCode);
    
    Optional<Role> findById(Long id);
    
    Role findAdminRole();
    
    List<Role> findAdminRoleList();
    
    List<Role> findActive();
}
