/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Admin
 */
public interface AuthService {

    UserDetails loadUserByUuid(String uuid);
}
