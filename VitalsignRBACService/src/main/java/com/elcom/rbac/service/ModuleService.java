/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service;

import com.elcom.rbac.model.Module;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Admin
 */
public interface ModuleService {

    void save(Module module);

    boolean update(Module module);

    boolean remove(Module module);
    
    Module findByModuleCode(String moduleCode);
    
    Optional<Module> findById(Long id);
    
    List<Module> findByServiceCode(String serviceCode);
}
