/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service.impl;

import com.elcom.rbac.constant.Constant;
import com.elcom.rbac.model.Module;
import com.elcom.rbac.model.ModulePath;
import com.elcom.rbac.repository.ModulePathRepository;
import com.elcom.rbac.service.ModulePathService;
import com.elcom.rbac.utils.StringUtil;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class ModulePathServiceImpl implements ModulePathService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModulePathServiceImpl.class);
    
    @Autowired
    private ModulePathRepository modulePathRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ModulePath> findByApiPath(String apiPath) {
        List<ModulePath> modulePathList = null;

        String keyLst = Constant.REDIS_MODULE_PATH_KEY + "_" + apiPath;
        Long sizeLst = redisTemplate.opsForList().size(keyLst);
        if (sizeLst != null && !sizeLst.equals(0L)) {
            LOGGER.info("findByApiPath load from Redis cache for key '" + keyLst + "'");
            modulePathList = (List<ModulePath>) redisTemplate.opsForList().range(keyLst, 0, -1);
        } else {
            modulePathList = modulePathRepository.findByApiPathOrderByModuleCodeAsc(apiPath);
            if (modulePathList != null && !modulePathList.isEmpty()) {
                redisTemplate.opsForList().rightPushAll(keyLst, modulePathList);
            }
        }
        return modulePathList;
    }

    @Override
    public void save(ModulePath modulePath) {
        modulePathRepository.save(modulePath);
    }

    @Override
    public boolean update(ModulePath modulePath, String newPath) {
        return modulePathRepository.updateModulePath(modulePath.getModuleCode().getModuleCode(),
                modulePath.getApiPath(), newPath) > 0;
    }

    @Override
    public boolean remove(ModulePath modulePath) {
        return modulePathRepository.deleteModulePath(modulePath.getModuleCode().getModuleCode(),
                modulePath.getApiPath()) > 0;
    }

    @Override
    public ModulePath findByModuleCodeAndApiPath(Module moduleCode, String apiPath) {
        return modulePathRepository.findByModuleCodeAndApiPath(moduleCode, apiPath);
    }

    @Override
    public List<ModulePath> findByServiceAndModule(String serviceCode, String moduleCode) {
        if (!StringUtil.isNullOrEmpty(serviceCode) && !StringUtil.isNullOrEmpty(moduleCode)) {
            return modulePathRepository.findByServiceCodeAndModuleCodeAndIsDeleteNotOrderByCreatedAtAsc(
                    new com.elcom.rbac.model.Service(serviceCode), new Module(moduleCode), 1);
        } else if(!StringUtil.isNullOrEmpty(serviceCode)){
            return modulePathRepository.findByServiceCodeAndIsDeleteNotOrderByCreatedAtAsc(
                    new com.elcom.rbac.model.Service(serviceCode), 1);
        } else if(!StringUtil.isNullOrEmpty(moduleCode)){
            return modulePathRepository.findByModuleCodeAndIsDeleteOrderByCreatedAtAsc(new Module(moduleCode), 1);
            
        }
        return null;
    }

    @Override
    public Optional<ModulePath> findById(Long id) {
        return modulePathRepository.findById(id);
    }

}
