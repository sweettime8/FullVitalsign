package com.elcom.device.repository;

import com.elcom.device.utils.StringUtil;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhdv
 */
@Repository
public class DeviceCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceCustomizeRepository.class);

    @Autowired
    public DeviceCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

    /*public List<DeviceInfoByPatientDTO> findDeviceInfoByPatient(String[] patients) {
        Session session = openSession();
        try {
            String sql = " SELECT s.patient_id AS patientId, s.mac AS sensorId, d.serial_number AS displayId, g.serial_number AS gateId " +
                        " FROM sensor s " +
                        " INNER JOIN gate g ON g.serial_number = s.gate_id " +
                        " INNER JOIN display d ON g.serial_number = d.gate_id " +
                        " WHERE s.patient_id IN :patients ORDER BY s.patient_id ";
            return session.createNativeQuery(sql)
                    .setParameterList("patients", patients)
                    .setResultTransformer(Transformers.aliasToBean(DeviceInfoByPatientDTO.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }*/
    
    public String validateGateReturnHomeAdminId(String deviceId) {
        Session session = openSession();
        try {
            String sql = " SELECT home_admin_id FROM gate WHERE status = 1 AND serial_number = :serialNumber ";
            return (String) session.createNativeQuery(sql)
                          .setParameter("serialNumber", deviceId.trim())
                          .uniqueResult();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }
    
    public boolean updateNewHealthProfileForGate(String homeAdminId, String currentHealthProfileId, String newHealthProfileId, String gateId) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String sql = " UPDATE gate SET health_profile_id = :newHealthProfileId " +
                         " WHERE home_admin_id = :homeAdminId AND health_profile_id = :currentHealthProfileId AND serial_number = :gateId ";
            Query query = session.createNativeQuery(sql);
            query.setParameter("newHealthProfileId", newHealthProfileId);
            query.setParameter("homeAdminId", homeAdminId);
            query.setParameter("currentHealthProfileId", currentHealthProfileId);
            query.setParameter("gateId", gateId);
            int updateStatus = query.executeUpdate();
            tx.commit();
            return updateStatus >= 1;
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return false;
    }
}
