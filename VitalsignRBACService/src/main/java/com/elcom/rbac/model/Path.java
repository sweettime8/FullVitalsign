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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "path")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Path.findAll", query = "SELECT p FROM Path p")})
public class Path implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Size(max = 500)
    @Column(name = "api_path")
    private String apiPath;
    
    @Column(name = "is_delete")
    private Integer isDelete;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @JoinColumn(name = "service_code", referencedColumnName = "service_code")
    @ManyToOne(fetch = FetchType.LAZY)
    private Service serviceCode;

    @JsonIgnore
    @OneToMany(mappedBy = "apiPath")
    @JsonManagedReference
    private List<RolePathPermission> rolePathPermissionList;

    public Path() {
    }

    public Path(Integer id) {
        this.id = id;
    }

    public Path(String serviceCode, String apiPath) {
        this.serviceCode = new Service(serviceCode);
        this.apiPath = apiPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
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
    public List<RolePathPermission> getRolePathPermissionList() {
        return rolePathPermissionList;
    }

    public void setRolePathPermissionList(List<RolePathPermission> rolePathPermissionList) {
        this.rolePathPermissionList = rolePathPermissionList;
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
        if (!(object instanceof Path)) {
            return false;
        }
        Path other = (Path) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.elcom.rbac.model.Path[ id=" + id + " ]";
    }
    
    public static String toListPathString(List<Path> pathList) {
        try {
            if (pathList != null && !pathList.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (Path tmp : pathList) {
                    builder.append(tmp.getApiPath());
                    builder.append("-");
                }
                String result = builder.toString();
                return result.substring(0, result.length() - 1);
            }
        } catch (Exception ex) {
            ex.toString();
        }
        return null;
    }
}
