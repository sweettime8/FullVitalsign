/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service.impl;

import com.elcom.rbac.constant.Constant;
import com.elcom.rbac.model.Path;
import com.elcom.rbac.repository.PathRepository;
import com.elcom.rbac.service.PathService;
import java.util.ArrayList;
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
public class PathServiceImpl implements PathService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathServiceImpl.class);

    @Autowired
    private PathRepository pathRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Path> findByApiPath(String apiPath) {
        List<Path> pathList = null;

        String keyHash = Constant.REDIS_SERVICE_PATH_KEY;
        if (redisTemplate.opsForHash().hasKey(keyHash, apiPath)) {
            LOGGER.info("findByApiPath load from Redis cache for key: '{}' - hash: '{}'", keyHash, apiPath);
            pathList = (List<Path>) redisTemplate.opsForHash().get(keyHash, apiPath);
        } else {
            pathList = pathRepository.findByApiPath(apiPath);
            if (pathList != null && !pathList.isEmpty()) {
                redisTemplate.opsForHash().put(keyHash, apiPath, pathList);
            }
        }
        return pathList;
    }

    @Override
    public void save(Path path) {
        boolean result = true;
        try {
            pathRepository.save(path);
        } catch (Exception ex) {
            result = false;
            ex.printStackTrace();
        }
        //Process for Redis
        if (result) {
            String keyHash = Constant.REDIS_SERVICE_PATH_KEY;
            String apiPath = path.getApiPath();
            List<Path> pathList = null;
            if (redisTemplate.opsForHash().hasKey(keyHash, apiPath)) {
                pathList = (List<Path>) redisTemplate.opsForHash().get(keyHash, apiPath);
            }
            if (pathList == null) {
                pathList = new ArrayList<>();
            }
            pathList.add(path);
            redisTemplate.opsForHash().put(keyHash, apiPath, pathList);
        }
    }

    @Override
    public boolean update(Path path, String newPath) {
        boolean result = pathRepository.updatePath(path.getServiceCode().getServiceCode(),
                path.getApiPath(), newPath) > 0;
        //Process for Redis
        if (result) {
            String keyHash = Constant.REDIS_SERVICE_PATH_KEY;
            redisTemplate.delete(keyHash);
            List<Path> pathList = pathRepository.findByApiPath(newPath);
            if (pathList != null && !pathList.isEmpty()) {
                redisTemplate.opsForHash().put(keyHash, newPath, pathList);
            }

        }
        return result;
    }

    @Override
    public boolean remove(Path path) {
        boolean result = pathRepository.deletePath(path.getServiceCode().getServiceCode(), path.getApiPath()) > 0;
        //Process for Redis
        if (result) {
            String keyHash = Constant.REDIS_SERVICE_PATH_KEY;
            String apiPath = path.getApiPath();
            if (redisTemplate.opsForHash().hasKey(keyHash, apiPath)) {
                List<Path> pathList = (List<Path>) redisTemplate.opsForHash().get(keyHash, apiPath);
                //Remove path from list
                if (pathList != null && !pathList.isEmpty()) {
                    for (Path tmp : pathList) {
                        if (tmp.getApiPath().equals(path.getApiPath())) {
                            pathList.remove(tmp);
                            break;
                        }
                    }
                }
                redisTemplate.opsForHash().put(keyHash, apiPath, pathList);
            }
        }
        return result;
    }

    @Override
    public Path findByServiceCodeAndApiPath(com.elcom.rbac.model.Service serviceCode, String apiPath) {
        return pathRepository.findByServiceCodeAndApiPath(serviceCode, apiPath);
    }

    @Override
    public Optional<Path> findById(Integer id) {
        return pathRepository.findById(id);
    }

    @Override
    public List<Path> findByServiceCode(String serviceCode) {
        return pathRepository.findByServiceCodeAndIsDeleteNotOrderByCreatedAtAsc(new com.elcom.rbac.model.Service(serviceCode), 1);
    }

}
