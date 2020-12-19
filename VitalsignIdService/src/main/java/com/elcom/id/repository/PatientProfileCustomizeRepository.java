/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.id.repository;

import com.elcom.id.model.PatientProfile;
import com.elcom.id.model.dto.ListUserProfileDTO;
import com.elcom.id.utils.StringUtil;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ducnh
 */
@Repository
public class PatientProfileCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientProfileCustomizeRepository.class);

    @Autowired
    public PatientProfileCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public PatientProfile findById(String id) {
        Session session = openSession();
        try {
            PatientProfile patientProfile = session.load(PatientProfile.class, id);
            return patientProfile;
        } catch (EntityNotFoundException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return null;
    }

    public List<ListUserProfileDTO> findByUserId(String uuid) {
        Session session = openSession();
        List<ListUserProfileDTO> lstUserProfile = null;
        try {
            String sql = "select id as id, full_name as fullName, birth_day as birthDay,full_name as fullName\n"
                    + ", gender as gender, height as height,weight as weight\n"
                    + "from patient_profile \n"
                    + "where user_id = :id\n AND active = 1 AND is_deleted = 0";

            NativeQuery query = session.createNativeQuery(sql);
            query.setParameter("id", uuid);

            query.addScalar("id", StandardBasicTypes.STRING);
            query.addScalar("fullName", StandardBasicTypes.STRING);
            query.addScalar("birthDay", StandardBasicTypes.STRING);
            query.addScalar("gender", StandardBasicTypes.INTEGER);
            query.addScalar("height", StandardBasicTypes.STRING);
            query.addScalar("weight", StandardBasicTypes.STRING);

            query.setResultTransformer(Transformers.aliasToBean(ListUserProfileDTO.class));
            Object result = query.list();

            return result != null ? (List<ListUserProfileDTO>) result : null;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return null;
    }

    public boolean updatePatientProfile(PatientProfile patientProfile) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String update = " ";
            if (!StringUtil.isNullOrEmpty(patientProfile.getFullName())) {
                update += ", full_name = :fullName ";
            }
            if (!StringUtil.isNullOrEmpty(patientProfile.getBirthDay())) {
                update += ", birth_day = :birthDay ";
            }
            if (patientProfile.getGender() != null) {
                update += ", gender = :gender ";
            }
            if (patientProfile.getHeight() != null) {
                update += ", height = :height ";
            }
            if (patientProfile.getWeight() != null) {
                update += ", weight = :weight ";
            }

            String sql = "Update patient_profile SET last_update = now() " + update + " WHERE id = :id ";
            Query query = session.createNativeQuery(sql);

            if (!StringUtil.isNullOrEmpty(patientProfile.getFullName())) {
                query.setParameter("fullName", patientProfile.getFullName());
            }
            if (!StringUtil.isNullOrEmpty(patientProfile.getBirthDay())) {
                query.setParameter("birthDay", patientProfile.getBirthDay());
            }
            if (patientProfile.getGender() != null) {
                query.setParameter("gender", patientProfile.getGender());
            }
            if (patientProfile.getHeight() != null) {
                query.setParameter("height", patientProfile.getHeight());
            }
            if (patientProfile.getWeight() != null) {
                query.setParameter("weight", patientProfile.getWeight());
            }

            query.setParameter("id", patientProfile.getId());
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

    public boolean deletePatientProfile(PatientProfile patientProfile) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String sql = "Update patient_profile SET last_update = now(), is_deleted = 1  WHERE id = :id ";
            Query query = session.createNativeQuery(sql);
            query.setParameter("id", patientProfile.getId());
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
