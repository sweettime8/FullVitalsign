/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.usersystem.repository;

import com.elcom.usersystem.model.Doctor;
import com.elcom.usersystem.utils.StringUtil;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ducnh
 */
@Repository
public class DoctorCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorCustomizeRepository.class);

    @Autowired
    public DoctorCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public Doctor findById(String id) {
        Session session = openSession();
        Object result = null;
        try {
            Query query = session.createNativeQuery("SELECT * FROM doctor WHERE id = ?", Doctor.class);
            query.setParameter(1, id);
            result = query.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (Doctor) result : null;
    }

    public boolean updateDoctor(Doctor doctor) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String update = " ";
            if (!StringUtil.isNullOrEmpty(doctor.getFullName())) {
                update += ", full_name = :fullName ";
            }
            if (!StringUtil.isNullOrEmpty(doctor.getSpecialist())) {
                update += ", specialist = :specialist ";
            }
            if (!StringUtil.isNullOrEmpty(doctor.getAdditional())) {
                update += ", additional = :additional ";
            }
            if (!StringUtil.isNullOrEmpty(doctor.getAvatar())) {
                update += ", avatar = :avatar ";
            }

            String sql = "Update doctor SET last_updated_at = now() " + update + " WHERE id = :id ";
            Query query = session.createNativeQuery(sql);

            if (!StringUtil.isNullOrEmpty(doctor.getFullName())) {
                query.setParameter("fullName", doctor.getFullName());
            }
            if (!StringUtil.isNullOrEmpty(doctor.getSpecialist())) {
                query.setParameter("specialist", doctor.getSpecialist());
            }
            if (!StringUtil.isNullOrEmpty(doctor.getAdditional())) {
                query.setParameter("additional", doctor.getAdditional());
            }
            if (!StringUtil.isNullOrEmpty(doctor.getAvatar())) {
                query.setParameter("avatar", doctor.getAvatar());
            }

            query.setParameter("id", doctor.getId());
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

    public boolean deleteDoctor(String id) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String sql = "delete From doctor WHERE id = :id ";
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

    public List<Doctor> findDoctors(String buId, String specialist, String fullName) {
        Session session = openSession();
        try {
            String sql = " SELECT d.id AS id, d.account_id AS accountId, d.business_unit_id AS businessUnitId,"
                    + " d.full_name AS fullName, d.specialist as specialist, d.additional AS additional"
                    + " FROM doctor d "
                    + " WHERE  d.business_unit_id = :buId "
                    + " AND d.specialist = :specialist "
                    + " AND d.full_name like :fullName";

            return session.createNativeQuery(sql)
                    .setParameter("buId", buId)
                    .setParameter("specialist", specialist)
                    .setParameter("fullName", fullName + "%")
                    .setResultTransformer(Transformers.aliasToBean(Doctor.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }

    public List<Doctor> findDoctorsbyName(String buId, String fullName) {
        Session session = openSession();
        try {
            String sql = " SELECT d.id AS id, d.account_id AS accountId, d.business_unit_id AS businessUnitId,"
                    + " d.full_name AS fullName, d.specialist as specialist, d.additional AS additional"
                    + " FROM doctor d "
                    + " WHERE  d.business_unit_id = :buId "
                    + " AND d.full_name like :fullName";

            return session.createNativeQuery(sql)
                    .setParameter("buId", buId)
                    .setParameter("fullName", fullName + "%")
                    .setResultTransformer(Transformers.aliasToBean(Doctor.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }

}
