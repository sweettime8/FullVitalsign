package com.elcom.id.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ducnh
 */
public class UserProfileDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String uuid;
    private String username;
    private String fullname;
    
    List<ListUserProfileDTO> lstUserProfile;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public List<ListUserProfileDTO> getLstUserProfile() {
        return lstUserProfile;
    }

    public void setLstUserProfile(List<ListUserProfileDTO> lstUserProfile) {
        this.lstUserProfile = lstUserProfile;
    }
    
    
}
