/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.usersystem.repository;

import com.elcom.usersystem.model.DoctorHomeAdminMaps;
import com.elcom.usersystem.model.HomeAdmin;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ducnh
 */
@Repository
public class DoctorHomeAdminMapCustomRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorHomeAdminMapCustomRepository.class);

    @Autowired
    public DoctorHomeAdminMapCustomRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public DoctorHomeAdminMaps findDoctorPairHomeAdmin(String customerId, String doctorId) {
        Session session = openSession();
        Object result = null;
        try {
            Query query = session.createNativeQuery("SELECT * FROM doctor_home_admin_map WHERE home_admin_id = ? AND "
                    + " doctor_id = ?", DoctorHomeAdminMaps.class);
            query.setParameter(1, customerId);
            query.setParameter(2, doctorId);
            result = query.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (DoctorHomeAdminMaps) result : null;
    }

    public boolean removeDoctorPairCustomer(Long id) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String sql = "DELETE FROM doctor_home_admin_map WHERE id = :id ";
            Query query = session.createNativeQuery(sql);
            query.setParameter("id", id);
            int result = query.executeUpdate();
            tx.commit();
            return result > 0;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
            ex.printStackTrace();
        } finally {
            closeSession(session);
        }
        return false;
    }

}
