///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.elcom.vitalsign.service.impl;
//
//import com.elcom.vitalsign.auth.CustomAccountDetails;
//import com.elcom.vitalsign.model.Account;
//import com.elcom.vitalsign.repository.AccountCustomizeRepository;
//import com.elcom.vitalsign.service.AuthService;
//import javax.transaction.Transactional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
///**
// *
// * @author Admin
// */
//@Service
//public class AuthServiceImpl implements UserDetailsService, AuthService {
//
//    @Autowired
//    private AccountCustomizeRepository accountRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) {
//        // Kiểm tra xem user có tồn tại trong database không?
//        Account account = accountRepository.findByUsername(username);
//        if (account == null) {
//            throw new UsernameNotFoundException("Account not found with username : " + username);
//        }
//        return new CustomAccountDetails(account);
//    }
//
//    // JWTAuthenticationFilter sẽ sử dụng hàm này
//    @Transactional
//    @Override
//    public UserDetails loadUserByUuid(String uuid) {
//        Account account = accountRepository.findByUuid(uuid);
//        if (account == null) {
//            throw new UsernameNotFoundException("Account not found with uuid : " + uuid);
//        }
//        return new CustomAccountDetails(account);
//    }
//}
