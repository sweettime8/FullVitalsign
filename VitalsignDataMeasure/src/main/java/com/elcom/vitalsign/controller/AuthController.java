///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.elcom.vitalsign.controller;
//
//import com.elcom.vitalsign.auth.CustomAccountDetails;
//import com.elcom.vitalsign.auth.LoginRequest;
//import com.elcom.vitalsign.auth.LoginResponse;
//import com.elcom.vitalsign.auth.SignupRequest;
//import com.elcom.vitalsign.auth.jwt.JwtTokenProvider;
//import com.elcom.vitalsign.exception.ValidationException;
//import com.elcom.vitalsign.message.MessageContent;
//import com.elcom.vitalsign.model.Account;
//import com.elcom.vitalsign.repository.AccountRepository;
//import com.elcom.vitalsign.validation.UserValidation;
//import javax.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// *
// * @author admin
// */
//@RestController
//public class AuthController {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
//
//    @Autowired
//    public AuthController() {
//    }
//
//    @Autowired
//    private JwtTokenProvider tokenProvider;
//
//    @Autowired
//    AuthenticationManager authenticationManager;
//
//    @Autowired
//    AccountRepository accountRepository;
//
//    @Autowired
//    PasswordEncoder encoder;
//
//    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
//    public LoginResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//
//        new UserValidation().validateLogin(loginRequest.getUsername(), loginRequest.getPassword());
//
//        // Xác thực thông tin người dùng Request lên, nếu không xảy ra exception tức là thông tin hợp lệ
//        Authentication authentication = null;
//        try {
//            authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginRequest.getUsername(),
//                            loginRequest.getPassword()
//                    )
//            );
//        } catch (AuthenticationException ex) {
//            LOGGER.error(ex.toString());
//            throw new ValidationException("Sai username/password.");
//        }
//
//        // Set thông tin authentication vào Security Context
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Trả về jwt cho người dùng.
//        String jwt = tokenProvider.generateToken((CustomAccountDetails) authentication.getPrincipal());
//        return new LoginResponse(jwt);
//    }
//
//    @RequestMapping(value = "/auth/signup", method = RequestMethod.POST)
//    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
//
//        // Create new user's account
//        Account account = new Account();
//        account.setUsername(signUpRequest.getUsername());
//        account.setPassword(encoder.encode(signUpRequest.getPassword()));
//        account.setEmail(signUpRequest.getEmail());
//
//        accountRepository.save(account);
//
//        return ResponseEntity.ok(new MessageContent("User registered successfully!"));
//    }
//}
