/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.device.repository;

import com.elcom.device.model.Gate;
import com.elcom.device.model.dto.DeviceHaDTO;
import com.elcom.device.utils.StringUtil;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author admin
 */
@Repository
public class GateCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(GateCustomizeRepository.class);

    private SessionFactory sessionFactory;

    @Autowired
    public GateCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

//    public Gate findBySerialNumber(String id) {
//        Session session = openSession();
//        Object result = null;
//        try {
//            Query query = session.createNativeQuery("SELECT * FROM gate WHERE serial_number = ?", Gate.class);
//            query.setParameter(1, id);
//            result = query.getSingleResult();
//        } catch (NoResultException ex) {
//            LOGGER.error(ex.toString());
//        } finally {
//            closeSession(session);
//        }
//        return result != null ? (Gate) result : null;
//    }
    public Gate findById(String id) {
        Session session = openSession();
        Object result = null;
        try {
            Query query = session.createNativeQuery("SELECT * FROM gate WHERE id = ?", Gate.class);
            query.setParameter(1, id);
            result = query.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (Gate) result : null;
    }

    public List<Gate> findGateByBuId(String buId) {
        Session session = openSession();
        try {
            String sql = "SELECT g.id AS id, g.name As name, "
                    + "g.model AS model, g.serial_number AS serialNumber, g.own_type AS ownType "
                    + "FROM gate g "
                    + "WHERE g.business_unit_id = :buId";

            return session.createNativeQuery(sql)
                    .setParameter("buId", buId)
                    .setResultTransformer(Transformers.aliasToBean(Gate.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }

    public List<DeviceHaDTO> getCountDeviceByHaId(String[] homeAdminIdLst) {
        Session session = openSession();
        try {
            String sql = " SELECT g.home_admin_id AS homeAdminId, COUNT(g.serial_number) AS countDevice "
                    + " FROM gate g "
                    + " WHERE g.home_admin_id IN :homeAdminIdLst ";
            return session.createNativeQuery(sql)
                    .setParameterList("homeAdminIdLst", homeAdminIdLst)
                    .addScalar("homeAdminId", StringType.INSTANCE)
                    .addScalar("countDevice", IntegerType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(DeviceHaDTO.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }

    public List<Gate> findDeviceforAddCustomer(String buId,String customerId, String gateNameOrSerial) {
        Session session = openSession();
        try {
            String sql = " SELECT g.id AS id, g.name AS name, g.business_unit_id As businessUnitId, g.home_admin_id AS homeAdminId, "
                    + " g.health_profile_id AS healthProfileId, g.model AS model, g.serial_number AS serialNumber, "
                    + " g.manufacture AS manufacture, g.firmware_version As firmwareVersion"
                    + " FROM gate g "
                    + " WHERE g.business_unit_id =:buId "
                    + " AND (g.name like :gateNameOrSerial OR g.serial_number like :gateNameOrSerial)"
                    + " AND g.home_admin_id !=:customerId ";

            return session.createNativeQuery(sql)
                    .setParameter("buId", buId)
                    .setParameter("gateNameOrSerial", gateNameOrSerial + "%")
                    .setParameter("customerId", customerId)
                    .setResultTransformer(Transformers.aliasToBean(Gate.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }

}
