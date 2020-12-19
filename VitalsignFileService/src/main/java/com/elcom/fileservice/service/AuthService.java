package com.elcom.fileservice.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author anhdv
 */
public interface AuthService {

    UserDetails loadUserById(Long id);
}