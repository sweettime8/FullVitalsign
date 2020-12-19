/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service;

import com.elcom.rbac.model.Path;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Admin
 */
public interface PathService {
    
    List<Path> findByApiPath(String apiPath);

    void save(Path path);

    boolean update(Path path, String newPath);

    boolean remove(Path path);

    Path findByServiceCodeAndApiPath(com.elcom.rbac.model.Service serviceCode, String apiPath);
    
    Optional<Path> findById(Integer id);
    
    List<Path> findByServiceCode(String serviceCode);
}
