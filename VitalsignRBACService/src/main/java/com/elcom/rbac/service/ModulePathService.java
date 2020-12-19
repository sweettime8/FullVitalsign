/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service;

import com.elcom.rbac.model.Module;
import com.elcom.rbac.model.ModulePath;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Admin
 */
public interface ModulePathService {

    List<ModulePath> findByApiPath(String apiPath);

    void save(ModulePath modulePath);

    boolean update(ModulePath modulePath, String newPath);

    boolean remove(ModulePath modulePath);

    ModulePath findByModuleCodeAndApiPath(Module moduleCode, String apiPath);
    
    Optional<ModulePath> findById(Long id);
    
    List<ModulePath> findByServiceAndModule(String serviceCode, String moduleCode);
}
