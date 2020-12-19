/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.model.dto;

import com.elcom.rbac.model.RoleUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class RoleUserPagingDTO implements Serializable{

    private List<RoleUser> dataRows;
    private Long totalRows;

    public RoleUserPagingDTO() {
        totalRows = new Long("0");
        dataRows = new ArrayList<>();
    }

    public List<RoleUser> getDataRows() {
        return dataRows;
    }

    public void setDataRows(List<RoleUser> dataRows) {
        this.dataRows = dataRows;
    }

    public Long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Long totalRows) {
        this.totalRows = totalRows;
    }
    
}
