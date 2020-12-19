package com.elcom.id.service.impl;

import com.elcom.id.auth.CustomUserDetails;
import com.elcom.id.model.User;
import com.elcom.id.repository.UserCustomizeRepository;
import com.elcom.id.service.AuthService;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author anhdv
 */
@Service
public class AuthServiceImpl implements UserDetailsService, AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    @Autowired
    private UserCustomizeRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userInfo) {
        // Kiểm tra xem user có tồn tại trong database không?
        User user = userRepository.findByEmail(userInfo);
        if (user == null) {
            user = userRepository.findByMobile(userInfo);
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found with userInfo : " + userInfo);
        } else {
            LOGGER.info("Find user with " + userInfo + " ==> uuid: " + user.getUuid());
        }
        return new CustomUserDetails(user);
    }

    // JWTAuthenticationFilter sẽ sử dụng hàm này
    @Transactional
    @Override
    public UserDetails loadUserByUuid(String uuid) {
        User user = userRepository.findByUuid(uuid);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with uuid : " + uuid);
        }
        return new CustomUserDetails(user);
    }
}
