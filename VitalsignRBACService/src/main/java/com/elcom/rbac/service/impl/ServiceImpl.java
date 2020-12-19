/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service.impl;

import com.elcom.rbac.repository.ServiceRepository;
import com.elcom.rbac.service.Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Admin
 */
@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public void save(com.elcom.rbac.model.Service service) {
        serviceRepository.save(service);
    }

    @Override
    public com.elcom.rbac.model.Service findByServiceCode(String serviceCode) {
        return serviceRepository.findByServiceCode(serviceCode);
    }

    @Override
    public boolean update(com.elcom.rbac.model.Service service) {
        return serviceRepository.updateService(service.getServiceCode(), service.getServiceName()) > 0;
    }

    @Override
    public boolean remove(com.elcom.rbac.model.Service service) {
        return serviceRepository.deleteService(service.getServiceCode()) > 0;
    }

    @Override
    public List<com.elcom.rbac.model.Service> findAll() {
        return serviceRepository.findAll();
    }
}
