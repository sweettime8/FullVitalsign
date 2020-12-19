package com.elcom.usersystem.repository;

import com.elcom.usersystem.model.HealthProfile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhdv
 */
@Repository
public interface HealthProfileRepository extends JpaRepository<HealthProfile, String> {
    
    List<HealthProfile> findByHomeAdminId(String homeAdminId);
}
