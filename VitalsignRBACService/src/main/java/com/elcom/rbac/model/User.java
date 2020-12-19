package com.elcom.rbac.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "elcom_user")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.cache.annotation.Cacheable
@ApiModel(value = "User entity")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @ApiModelProperty(notes = "ID tự tăng")
    private Long id;

    @Column(name = "USERNAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "ROLE_NAME")
    private String roleName;
    
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @PrePersist
    void preInsert() {
       if (this.getCreatedAt() == null)
           this.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }
    
    @PreUpdate
    void preUpdate() {
       if (this.getCreatedAt() == null)
           this.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }
    
    public User() {
    }
    
    public User(String userName, String password, String fullName, String roleName) {
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the createdAt
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /*CREATE TABLE `elcom_user` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
        `password` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '$2a$10$QlCVldsah3fjkyNOsq6Xi.t9x75UOjxISjTiVvFkamgDaO59vQb.O' COMMENT 'Mặc định: ''123456''',
        `full_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
        `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
        PRIMARY KEY (`id`),
        UNIQUE KEY `username` (`username`)
      ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
    */

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName the roleName to set
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    
}
