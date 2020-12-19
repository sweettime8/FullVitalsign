/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.usersystem.repository;

import com.elcom.usersystem.model.HealthProfile;
import com.elcom.usersystem.utils.StringUtil;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
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
public class HealthProfileCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthProfileCustomizeRepository.class);

    @Autowired
    public HealthProfileCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public HealthProfile findById(String id) {
        Session session = openSession();
        try {
            HealthProfile healthProfile = session.load(HealthProfile.class, id);
            return healthProfile;
        } catch (EntityNotFoundException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return null;
    }

    public boolean updateHealthProfile(HealthProfile healthProfile) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String update = " ";
            if (!StringUtil.isNullOrEmpty(healthProfile.getFullName())) {
                update += ", full_name = :fullName ";
            }
            if (!StringUtil.isNullOrEmpty(healthProfile.getBirthDay())) {
                update += ", birth_day = :birthDay ";
            }
            if (healthProfile.getGender() != null) {
                update += ", gender = :gender ";
            }
            if (healthProfile.getHeight() != null) {
                update += ", height = :height ";
            }
            if (healthProfile.getWeight() != null) {
                update += ", weight = :weight ";
            }

            String sql = "Update health_profile SET last_updated_at = now() " + update + " WHERE id = :id ";
            Query query = session.createNativeQuery(sql);

            if (!StringUtil.isNullOrEmpty(healthProfile.getFullName())) {
                query.setParameter("fullName", healthProfile.getFullName());
            }
            if (!StringUtil.isNullOrEmpty(healthProfile.getBirthDay())) {
                query.setParameter("birthDay", healthProfile.getBirthDay());
            }
            if (healthProfile.getGender() != null) {
                query.setParameter("gender", healthProfile.getGender());
            }
            if (healthProfile.getHeight() != null) {
                query.setParameter("height", healthProfile.getHeight());
            }
            if (healthProfile.getWeight() != null) {
                query.setParameter("weight", healthProfile.getWeight());
            }

            query.setParameter("id", healthProfile.getId());
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

    public boolean deleteHealthProfile(HealthProfile healthProfile) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String sql = "Update health_profile SET last_updated_at = now(), is_delete = 1  WHERE id = :id ";
            Query query = session.createNativeQuery(sql);
            query.setParameter("id", healthProfile.getId());
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

    public boolean deleteHealthProfileOfCustomer(String haId) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String sql = "Update health_profile SET last_updated_at = now(), is_delete = 1  WHERE home_admin_id = :id ";
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
