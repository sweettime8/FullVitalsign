/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.usersystem.repository;

import com.elcom.usersystem.model.Doctor;
import com.elcom.usersystem.model.HomeAdmin;
import com.elcom.usersystem.model.dto.HomeAdminDTO;
import com.elcom.usersystem.utils.StringUtil;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ducnh
 */
@Repository
public class HomeAdminCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeAdminCustomizeRepository.class);

    @Autowired
    public HomeAdminCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public List<HomeAdminDTO> findByBuId(String id) {
        Session session = openSession();
        try {
            String sql = "SELECT ha.id As id, ha.account_id as accountId, ha.business_unit_id as businessUnitId,"
                    + " (select count(hp.id) from health_profile hp where hp.home_admin_id = ha.id) as countProfile,"
                    + " (select count(d.id) from doctor_home_admin_map d where d.home_admin_id = ha.id) AS countDoctor "
                    + " FROM home_admin ha "
                    + " WHERE business_unit_id = :id";

            return session.createNativeQuery(sql)
                    .setParameter("id", id)
                    .addScalar("id", StringType.INSTANCE)
                    .addScalar("accountId", StringType.INSTANCE)
                    .addScalar("businessUnitId", StringType.INSTANCE)
                    .addScalar("countProfile", IntegerType.INSTANCE)
                    .addScalar("countDoctor", IntegerType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(HomeAdminDTO.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }

    public HomeAdmin findById(String id) {
        Session session = openSession();
        Object result = null;
        try {
            Query query = session.createNativeQuery("SELECT * FROM home_admin WHERE id = ?", HomeAdmin.class);
            query.setParameter(1, id);
            result = query.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (HomeAdmin) result : null;
    }

    public boolean updateCustomer(HomeAdmin ha) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String update = " ";
            if (!StringUtil.isNullOrEmpty(ha.getFullName())) {
                update += ", full_name = :fullName ";
            }
            if (!StringUtil.isNullOrEmpty(ha.getPhoneNumber())) {
                update += ", phone_number = :phoneNumber ";
            }
            if (!StringUtil.isNullOrEmpty(ha.getEmail())) {
                update += ", email = :email ";
            }
            if (!StringUtil.isNullOrEmpty(ha.getAdditional())) {
                update += ", additional = :additional ";
            }

            String sql = "Update home_admin SET last_updated_at = now() " + update + " WHERE id = :id ";
            Query query = session.createNativeQuery(sql);

            if (!StringUtil.isNullOrEmpty(ha.getFullName())) {
                query.setParameter("fullName", ha.getFullName());
            }
            if (!StringUtil.isNullOrEmpty(ha.getPhoneNumber())) {
                query.setParameter("phoneNumber", ha.getPhoneNumber());
            }
            if (!StringUtil.isNullOrEmpty(ha.getEmail())) {
                query.setParameter("email", ha.getEmail());
            }
            if (!StringUtil.isNullOrEmpty(ha.getAdditional())) {
                query.setParameter("additional", ha.getAdditional());
            }

            query.setParameter("id", ha.getId());
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

    public boolean deleteCustomer(String haId) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String sql = "Update home_admin SET last_updated_at = now(), status = 0  WHERE id = :id ";
            Query query = session.createNativeQuery(sql);
            query.setParameter("id", haId);
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
