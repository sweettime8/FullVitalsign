package com.elcom.rbac.service.impl;

import com.elcom.rbac.constant.Constant;
import com.elcom.rbac.model.Role;
import com.elcom.rbac.model.RoleUser;
import com.elcom.rbac.model.dto.RoleUserPagingDTO;
import com.elcom.rbac.repository.RoleUserRepository;
import com.elcom.rbac.service.RoleUserService;
import io.netty.util.internal.StringUtil;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class RoleUserServiceImpl implements RoleUserService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleUserServiceImpl.class);

    @Autowired
    private RoleUserRepository roleUserRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<RoleUser> findByUuidUser(String uuidUser) {
        List<RoleUser> roleUserList = null;

        String keyHash = Constant.REDIS_ROLE_USER_KEY;//uuidUser
        if (redisTemplate.opsForHash().hasKey(keyHash, uuidUser)) {
            LOGGER.info("findByUuidUser load from Redis cache for key: '{}', uuidUser: '{}'", keyHash, uuidUser);
            roleUserList = (List<RoleUser>) redisTemplate.opsForHash().get(keyHash, uuidUser);
        } else {
            try {
                roleUserList = roleUserRepository.findByUuidUserAndIsDeleteOrderByRoleCodeAsc(uuidUser, 0);
                if (roleUserList != null && !roleUserList.isEmpty()) {
                    redisTemplate.opsForHash().put(keyHash, uuidUser, roleUserList);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return roleUserList;
    }

    @Override
    public void save(RoleUser role) {
        boolean status = true;
        try {
            roleUserRepository.save(role);
        } catch (Exception ex) {
            status = false;
            ex.printStackTrace();
        }
        if (status) {
            //Reload redis cache
            String keyHash = Constant.REDIS_ROLE_USER_KEY;
            redisTemplate.opsForHash().delete(keyHash, role.getUuidUser());
            try {
                List<RoleUser> roleUserList = roleUserRepository.findByUuidUserAndIsDeleteOrderByRoleCodeAsc(role.getUuidUser(), 0);
                if (roleUserList != null && !roleUserList.isEmpty()) {
                    redisTemplate.opsForHash().put(keyHash, role.getUuidUser(), roleUserList);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean update(RoleUser role, String newRole) {
        boolean status = roleUserRepository.updateRoleUser(role.getUuidUser(), role.getRoleCode().getRoleCode(), newRole) > 0;
        if (status) {
            //Reload redis cache
            String keyHash = Constant.REDIS_ROLE_USER_KEY;
            redisTemplate.opsForHash().delete(keyHash, role.getUuidUser());
            try {
                List<RoleUser> roleUserList = roleUserRepository.findByUuidUserAndIsDeleteOrderByRoleCodeAsc(role.getUuidUser(), 0);
                if (roleUserList != null && !roleUserList.isEmpty()) {
                    redisTemplate.opsForHash().put(keyHash, role.getUuidUser(), roleUserList);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return status;
    }

    @Override
    public boolean remove(RoleUser role) {
        boolean status = roleUserRepository.deleteRoleUser(role.getUuidUser(), role.getRoleCode().getRoleCode()) > 0;
        if (status) {
            String keyHash = Constant.REDIS_ROLE_USER_KEY;
            redisTemplate.opsForHash().delete(keyHash, role.getUuidUser());
            try {
                List<RoleUser> roleUserList = roleUserRepository.findByUuidUserAndIsDeleteOrderByRoleCodeAsc(role.getUuidUser(), 0);
                if (roleUserList != null && !roleUserList.isEmpty()) {
                    redisTemplate.opsForHash().put(keyHash, role.getUuidUser(), roleUserList);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return status;
    }

    @Override
    public RoleUser findByUuidUserAndRoleCode(String uuidUser, Role roleCode) {
        return roleUserRepository.findByUuidUserAndRoleCode(uuidUser, roleCode);
    }
    
    @Override
    public RoleUser findByUuidUserAndRoleCodeAndIsDelete(String uuidUser, Role roleCode, Integer isDelete) {
        return roleUserRepository.findByUuidUserAndRoleCodeAndIsDelete(uuidUser, roleCode, isDelete);
    }

    @Override
    public Optional<RoleUser> findById(Long id) {
        return roleUserRepository.findById(id);
    }

    @Override
    public List<RoleUser> findByRoleCode(Role roleCode) {
        return roleUserRepository.findByRoleCodeAndIsDelete(roleCode, 0);
    }

    @Override
    public RoleUserPagingDTO findAdmin(String roleCode, Integer currentPage, Integer rowsPerPage, String sort) {
        RoleUserPagingDTO result = new RoleUserPagingDTO();
        try {
            if (currentPage > 0) {
                currentPage--;
            }
            if (StringUtil.isNullOrEmpty(sort)) {
                sort = "createdAt";
            }
            Pageable paging = PageRequest.of(currentPage, rowsPerPage, Sort.by(sort).descending());
            Page<RoleUser> pagedResult = null;
            if (StringUtil.isNullOrEmpty(roleCode)) {
                pagedResult = roleUserRepository.findByRoleCodeNotAndIsDelete(new Role(RoleUser.ROLE_USER), 0, paging);
            } else {
                pagedResult = roleUserRepository.findByRoleCodeAndIsDelete(new Role(roleCode), 0, paging);
            }
            if (pagedResult.hasContent()) {
                result.setDataRows(pagedResult.getContent());
                result.setTotalRows(pagedResult.getTotalElements());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(ex.toString());
        }
        return result;
    }

    @Override
    public RoleUser findByUuidUserAndRoleCodeInAndIsDelete(String uuidUser, List<Role> roleCode, Integer isDelete) {
        return roleUserRepository.findByUuidUserAndRoleCodeInAndIsDelete(uuidUser, roleCode, isDelete);
    }

}
