package com.elcom.usersystem.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author anhdv
 */
@Entity
@Table(name = "doctor_home_admin_map")
public class DoctorHomeAdminMaps implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "doctor_id")
    private String doctorId;

    @Column(name = "home_admin_id")
    private String homeAdminId;

    public DoctorHomeAdminMaps() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the doctorId
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * @param doctorId the doctorId to set
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
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
}
