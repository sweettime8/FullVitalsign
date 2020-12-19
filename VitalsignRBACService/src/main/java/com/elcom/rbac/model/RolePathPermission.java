/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "role_path_permission")
@XmlRootElement
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.cache.annotation.Cacheable
public class RolePathPermission implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "can_create")
    private Integer canCreate;
    
    @Column(name = "can_read")
    private Integer canRead;
    
    @Column(name = "can_update")
    private Integer canUpdate;
    
    @Column(name = "can_delete")
    private Integer canDelete;
    
    @Column(name = "is_delete")
    private Integer isDelete;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @JsonIgnore
    @JoinColumn(name = "role_code", referencedColumnName = "role_code")
    @ManyToOne
    @JsonBackReference
    private Role roleCode;
    
    @JsonIgnore
    @JoinColumn(name = "service_code", referencedColumnName = "service_code")
    @ManyToOne
    private Service serviceCode;
    
    @JsonIgnore
    @JoinColumn(name = "api_path", referencedColumnName = "api_path")
    @ManyToOne
    @JsonBackReference
    private Path apiPath;

    public RolePathPermission() {
    }

    public RolePathPermission(Long id) {
        this.id = id;
    }
    
    public RolePathPermission(String serviceCode, String apiPath, String roleCode) {
        this.serviceCode = new Service(serviceCode);
        this.apiPath = new Path(serviceCode, apiPath);
        this.roleCode = new Role(roleCode, null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCanCreate() {
        return canCreate;
    }

    public void setCanCreat(Integer canCreate) {
        this.canCreate = canCreate;
    }

    public Integer getCanRead() {
        return canRead;
    }

    public void setCanRead(Integer canRead) {
        this.canRead = canRead;
    }

    public Integer getCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(Integer canUpdate) {
        this.canUpdate = canUpdate;
    }

    public Integer getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Integer canDelete) {
        this.canDelete = canDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Role getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(Role roleCode) {
        this.roleCode = roleCode;
    }

    public Service getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(Service serviceCode) {
        this.serviceCode = serviceCode;
    }
    
    public Path getApiPath() {
        return apiPath;
    }

    public void setApiPath(Path apiPath) {
        this.apiPath = apiPath;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RolePathPermission)) {
            return false;
        }
        RolePathPermission other = (RolePathPermission) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.elcom.rbac.model.RoleModulePermission[ id=" + id + " ]";
    }
    
}
