package com.elcom.device.repository;

import com.elcom.device.model.Gate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.elcom.device.model.dto.MqttResourceInfoDTO;

/**
 *
 * @author anhdv
 */
@Repository
public interface DeviceRepository extends JpaRepository<Gate, String> {

    @Query(value=" SELECT name, protocol, ip_address AS ipAddress, port_number AS portNumber, account, password " +
                 " FROM sys_resources WHERE active = 1 AND resource_code = :resourceCode ", nativeQuery = true)
    MqttResourceInfoDTO findResourceInfo(
        @Param("resourceCode") String resourceCode
    );
    
    
}
