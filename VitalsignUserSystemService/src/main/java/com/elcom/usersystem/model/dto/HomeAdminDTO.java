/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.usersystem.model.dto;

/**
 *
 * @author ducnh
 */
public class HomeAdminDTO {
    private String id;
    private String accountId;
    private String businessUnitId;
    private Integer countDevice;   
    private Integer countDoctor;
    private Integer countProfile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getBusinessUnitId() {
        return businessUnitId;
    }

    public void setBusinessUnitId(String businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public Integer getCountDevice() {
        return countDevice;
    }

    public void setCountDevice(Integer countDevice) {
        this.countDevice = countDevice;
    }

    public Integer getCountDoctor() {
        return countDoctor;
    }

    public void setCountDoctor(Integer countDoctor) {
        this.countDoctor = countDoctor;
    }
   
    public Integer getCountProfile() {
        return countProfile;
    }

    public void setCountProfile(Integer countProfile) {
        this.countProfile = countProfile;
    }
    
    
    
}
