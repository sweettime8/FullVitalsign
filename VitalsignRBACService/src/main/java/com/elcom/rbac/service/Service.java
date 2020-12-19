/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service;

import java.util.List;

/**
 *
 * @author Admin
 */
public interface Service {

    void save(com.elcom.rbac.model.Service service);
    
    boolean update(com.elcom.rbac.model.Service service);
    
    boolean remove(com.elcom.rbac.model.Service service);

    com.elcom.rbac.model.Service findByServiceCode(String serviceCode);
    
    List<com.elcom.rbac.model.Service> findAll();
}
