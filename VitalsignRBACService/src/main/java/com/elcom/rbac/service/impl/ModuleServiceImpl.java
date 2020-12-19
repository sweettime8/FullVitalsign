/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service.impl;

import com.elcom.rbac.model.Module;
import com.elcom.rbac.repository.ModuleRepository;
import com.elcom.rbac.service.ModuleService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public void save(Module module) {
        moduleRepository.save(module);
    }

    @Override
    public boolean update(Module module) {
        return moduleRepository.updateModule(module.getModuleCode(), module.getModuleName()) > 0;
    }

    @Override
    public boolean remove(Module module) {
        return moduleRepository.deleteModule(module.getModuleCode()) > 0;
    }

    @Override
    public Module findByModuleCode(String moduleCode) {
        return moduleRepository.findByModuleCode(moduleCode);
    }

    @Override
    public List<Module> findByServiceCode(String serviceCode) {
        return moduleRepository.findByServiceCodeAndIsDeleteNot(new com.elcom.rbac.model.Service(serviceCode), 1);
    }

    @Override
    public Optional<Module> findById(Long id) {
        return moduleRepository.findById(id);
    }
}
