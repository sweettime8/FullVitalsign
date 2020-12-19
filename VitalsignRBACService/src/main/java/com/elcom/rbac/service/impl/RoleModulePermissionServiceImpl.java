/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.service.impl;

import com.elcom.rbac.constant.Constant;
import com.elcom.rbac.model.Path;
import com.elcom.rbac.model.Role;
import com.elcom.rbac.model.RolePathPermission;
import com.elcom.rbac.service.RoleModulePermissionService;
import com.elcom.rbac.utils.StringUtil;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.elcom.rbac.repository.RolePathPermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Admin
 */
@Service
public class RoleModulePermissionServiceImpl implements RoleModulePermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleModulePermissionServiceImpl.class);

    @Autowired
    private RolePathPermissionRepository roleModulePermissionRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<RolePathPermission> findByRoleAndPath(String requestMethod, List<Role> roleCode,
            List<Path> apiPath) {
        List<RolePathPermission> permissionList = null;
        String keyHash = Constant.REDIS_ROLE_MODULE_PERMISSION_KEY;
        String hash = requestMethod + "_" + Role.toListRoleCodeString(roleCode) + Path.toListPathString(apiPath);

        if (redisTemplate.opsForHash().hasKey(keyHash, hash)) {
            LOGGER.info("findByRoleAndPath load from Redis cache for key: '{}', hash: '{}'", keyHash, hash);
            permissionList = (List<RolePathPermission>) redisTemplate.opsForHash().get(keyHash, hash);
        } else {
            switch (requestMethod) {
                case "GET":
                    //Read
                    permissionList = roleModulePermissionRepository.findByRoleCodeInAndApiPathInOrderByCanReadDesc(roleCode, apiPath);
                    break;
                case "POST":
                    //Create
                    permissionList = roleModulePermissionRepository.findByRoleCodeInAndApiPathInOrderByCanCreateDesc(roleCode, apiPath);
                    break;
                case "PUT":
                    //Update put
                    permissionList = roleModulePermissionRepository.findByRoleCodeInAndApiPathInOrderByCanUpdateDesc(roleCode, apiPath);
                    break;
                case "PATCH":
                    //Update patch
                    permissionList = roleModulePermissionRepository.findByRoleCodeInAndApiPathInOrderByCanUpdateDesc(roleCode, apiPath);
                    break;
                case "DELETE":
                    //Delete
                    permissionList = roleModulePermissionRepository.findByRoleCodeInAndApiPathInOrderByCanDeleteDesc(roleCode, apiPath);
                    break;
                default:
                    break;
            }
            if (permissionList != null && !permissionList.isEmpty()) {
                redisTemplate.opsForHash().put(keyHash, hash, permissionList);
            }
        }
        return permissionList;
    }

    @Override
    public RolePathPermission findByRoleAndPath(Role roleCode, Path apiPath) {
        return roleModulePermissionRepository.findByRoleCodeAndApiPath(roleCode, apiPath);
    }

    @Override
    public void save(RolePathPermission permission) {
        boolean result = true;
        try {
            roleModulePermissionRepository.save(permission);
        } catch (Exception ex) {
            result = false;
            ex.printStackTrace();
        }
        if (result) {
            //Process Redis
            String keyHash = Constant.REDIS_ROLE_MODULE_PERMISSION_KEY;
            redisTemplate.delete(keyHash);
        }
    }

    @Override
    public boolean update(RolePathPermission permission, int canCreate, int canRead, int canUpdate, int canDelete) {
        boolean result = roleModulePermissionRepository.updateRolePathPermission(permission.getRoleCode().getRoleCode(),
                permission.getApiPath().getApiPath(), canCreate, canRead, canUpdate, canDelete) > 0;
        if (result) {
            //Process Redis
            String keyHash = Constant.REDIS_ROLE_MODULE_PERMISSION_KEY;
            redisTemplate.delete(keyHash);
        }
        return result;
    }

    @Override
    public boolean remove(RolePathPermission permission) {
        boolean result = roleModulePermissionRepository.deleteRolePathPermission(permission.getRoleCode().getRoleCode(),
                permission.getApiPath().getApiPath()) > 0;
        if (result) {
            //Process Redis
            String keyHash = Constant.REDIS_ROLE_MODULE_PERMISSION_KEY;
            redisTemplate.delete(keyHash);
        }
        return result;
    }

    @Override
    public List<RolePathPermission> findByServiceAndRoleAndPath(String serviceCode, String roleCode, String apiPath) {
        if (!StringUtil.isNullOrEmpty(serviceCode) && !StringUtil.isNullOrEmpty(roleCode) && !StringUtil.isNullOrEmpty(apiPath)) {
            return roleModulePermissionRepository.findByServiceCodeAndRoleCodeAndApiPathAndIsDeleteNotOrderByRoleCodeAsc(new com.elcom.rbac.model.Service(serviceCode),
                    new Role(roleCode), new Path(serviceCode, apiPath), 1);
        } else if (!StringUtil.isNullOrEmpty(serviceCode) && !StringUtil.isNullOrEmpty(roleCode)) {
            return roleModulePermissionRepository.findByServiceCodeAndRoleCodeAndIsDeleteNotOrderByRoleCodeAsc(new com.elcom.rbac.model.Service(serviceCode),
                    new Role(roleCode), 1);
        } else if (!StringUtil.isNullOrEmpty(serviceCode) && !StringUtil.isNullOrEmpty(apiPath)) {
            return roleModulePermissionRepository.findByServiceCodeAndApiPathAndIsDeleteNotOrderByRoleCodeAsc(new com.elcom.rbac.model.Service(serviceCode),
                    new Path(serviceCode, apiPath), 1);
        } else if (!StringUtil.isNullOrEmpty(serviceCode)) {
            return roleModulePermissionRepository.findByServiceCodeAndIsDeleteNotOrderByRoleCodeAsc(new com.elcom.rbac.model.Service(serviceCode), 1);
        }
        return null;
    }

    @Override
    public Optional<RolePathPermission> findById(Long id) {
        return roleModulePermissionRepository.findById(id);
    }
}
