/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.device.model.dto;

import java.io.Serializable;

/**
 *
 * @author ducnh
 */
public class DeviceHaDTO implements Serializable {
    private String homeAdminId;
    private Integer countDevice;
   
    public String getHomeAdminId() {
        return homeAdminId;
    }

    public void setHomeAdminId(String homeAdminId) {
        this.homeAdminId = homeAdminId;
    }

    public Integer getCountDevice() {
        return countDevice;
    }

    public void setCountDevice(Integer countDevice) {
        this.countDevice = countDevice;
    }
    
    
}
