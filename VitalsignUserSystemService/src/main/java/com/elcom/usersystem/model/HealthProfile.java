package com.elcom.usersystem.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 *
 * @author anhdv
 */
@Entity
@Table(name = "health_profile")
public class HealthProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "home_admin_id")
    private String homeAdminId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "birth_day")
    private String birthDay;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "height")
    private Float height;

    @Column(name = "active")
    private Integer active;

    @Column(name = "is_delete")
    private Integer isDelete;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    @PrePersist
    void preInsert() {
        if (this.getId() == null) {
            this.setId(UUID.randomUUID().toString());
        if( this.getActive()==null )
            this.setActive(1);
        }
        if (this.getCreatedAt() == null) {
            this.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        if (this.getLastUpdatedAt()== null) {
            this.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
        }
    }

    @PreUpdate
    void preUpdate() {
    }

    public HealthProfile() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    /**
     * @return the lastUpdatedAt
     */
    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    /**
     * @param lastUpdatedAt the lastUpdatedAt to set
     */
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    /**
     * @return the homeAdminId
     */
    public String getHomeAdminId() {
        return homeAdminId;
    }

    /**
     * @param homeAdminId the homeAdminId to set
     */
    public void setHomeAdminId(String homeAdminId) {
        this.homeAdminId = homeAdminId;
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
     * @return the birthDay
     */
    public String getBirthDay() {
        return birthDay;
    }

    /**
     * @param birthDay the birthDay to set
     */
    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    /**
     * @return the gender
     */
    public Integer getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * @return the weight
     */
    public Float getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(Float weight) {
        this.weight = weight;
    }

    /**
     * @return the height
     */
    public Float getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(Float height) {
        this.height = height;
    }

    /**
     * @return the active
     */
    public Integer getActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Integer active) {
        this.active = active;
    }

    /**
     * @return the isDelete
     */
    public Integer getIsDelete() {
        return isDelete;
    }

    /**
     * @param isDelete the isDelete to set
     */
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
