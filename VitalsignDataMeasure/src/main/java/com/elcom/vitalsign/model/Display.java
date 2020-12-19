/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

/**
 *
 * @author admin
 */
@Entity
@Table(name = "display")
//@Proxy(lazy = false)
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@org.springframework.cache.annotation.Cacheable
public class Display implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "gate_id")
    private String gateId;

    @Column(name = "serial_number")
    private String serial_number;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    public Display() {
    }

    @PrePersist
    void preInsert() {
        if (this.isDeleted == null) {
            this.setIsDeleted(0);
        }
        if (this.status == null) {
            this.setStatus(1);
        }
    }

    @PreUpdate
    void preUpdate() {
        if (this.isDeleted == null) {
            this.setIsDeleted(0);
        }
        if (this.status == null) {
            this.setStatus(1);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGateId() {
        return gateId;
    }

    public void setGateId(String gateId) {
        this.gateId = gateId;
    }
    

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

}
