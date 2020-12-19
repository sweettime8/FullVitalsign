/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service.impl;

import com.elcom.rbac.model.Role;
import com.elcom.rbac.repository.RoleRepository;
import com.elcom.rbac.service.RoleService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }

    @Override
    public boolean update(Role role) {
        return roleRepository.updateRole(role.getRoleCode(), role.getRoleName()) > 0;
    }

    @Override
    public boolean remove(Role role) {
        return roleRepository.deleteRole(role.getRoleCode()) > 0;
    }

    @Override
    public Role findByRoleCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode);
    }

    @Override
    public Role findAdminRole() {
        return roleRepository.findByIsAdmin(1);
    }

    @Override
    public List<Role> findActive() {
        return roleRepository.findByIsDeleteNot(1);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<Role> findAdminRoleList() {
        return roleRepository.findByIsAdminAndIsDelete(1, 0);
    }

}
