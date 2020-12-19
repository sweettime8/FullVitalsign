package com.elcom.usersystem.repository;

import com.elcom.usersystem.model.HomeAdmin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhdv
 */
@Repository
public interface HomeAdminRepository extends JpaRepository<HomeAdmin, String> {
    
    @Override
    Optional<HomeAdmin> findById(String id);
}
