/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.rbac.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "module")
@XmlRootElement
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.cache.annotation.Cacheable
public class Module implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 255)
    @Column(name = "module_code")
    private String moduleCode;

    @Size(max = 255)
    @Column(name = "module_name")
    private String moduleName;
    
    @Column(name = "is_delete")
    private Integer isDelete;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @JoinColumn(name = "service_code", referencedColumnName = "service_code")
    @ManyToOne
    private Service serviceCode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "moduleCode")
    @JsonIgnore
    @JsonManagedReference
    private List<ModulePath> modulePathList;

    //@OneToMany(mappedBy = "moduleCode")
    //@JsonIgnore
    //@JsonManagedReference
    //private List<RoleModulePermission> roleModulePermissionList;
    
    @Transient
    private List<RolePathPermission> roleModulePermissionList;

    public Module() {
    }

    public Module(Long id) {
        this.id = id;
    }
    
    public Module(String moduleCode) {
        this.moduleCode = moduleCode;
    }
    
    public Module(String serviceCode, String moduleCode, String moduleName){
        this.serviceCode = new Service(serviceCode);
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public Service getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(Service serviceCode) {
        this.serviceCode = serviceCode;
    }

    @XmlTransient
    public List<ModulePath> getModulePathList() {
        return modulePathList;
    }

    public void setModulePathList(List<ModulePath> modulePathList) {
        this.modulePathList = modulePathList;
    }

    @XmlTransient
    public List<RolePathPermission> getRoleModulePermissionList() {
        return roleModulePermissionList;
    }

    public void setRoleModulePermissionList(List<RolePathPermission> roleModulePermissionList) {
        this.roleModulePermissionList = roleModulePermissionList;
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
        if (!(object instanceof Module)) {
            return false;
        }
        Module other = (Module) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.elcom.rbac.model.Module[ id=" + id + " ]";
    }

    public static String toListModuleCodeString(List<Module> moduleList) {
        try {
            if (moduleList != null && !moduleList.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (Module tmp : moduleList) {
                    builder.append(tmp.getModuleCode());
                    builder.append("-");
                }
                return builder.toString();
            }
        } catch (Exception ex) {
            ex.toString();
        }
        return null;
    }
}
