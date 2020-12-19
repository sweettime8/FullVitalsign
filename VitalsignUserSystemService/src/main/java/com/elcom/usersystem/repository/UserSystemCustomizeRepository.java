package com.elcom.usersystem.repository;

import com.elcom.usersystem.model.Doctor;
import com.elcom.usersystem.model.dto.DetailsHealthProfile;
import com.elcom.usersystem.model.dto.HealthProfilesByDoctor;
import com.elcom.usersystem.utils.StringUtil;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhdv
 */
@Repository
public class UserSystemCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSystemCustomizeRepository.class);

    @Autowired
    public UserSystemCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public List<HealthProfilesByDoctor> findHealthProfilesByDoctor(String doctorAccountId, String healthProfileFullName) {
        Session session = openSession();
        try {
            String filterCondition = "";
            if( !StringUtil.isNullOrEmpty(healthProfileFullName) )
                filterCondition += " AND hp.full_name = :healthProfileFullName ";
            String sql = " SELECT hp.id AS healthProfileId, hp.home_admin_id AS homeAdminId, hp.full_name AS fullName " +
                         " FROM health_profile hp " +
                         " INNER JOIN home_admin ha ON ha.id = hp.home_admin_id " +
                         " INNER JOIN doctor_home_admin_map dham ON ha.id = dham.home_admin_id " +
                         " INNER JOIN doctor d ON d.id = dham.doctor_id " +
                         " WHERE hp.active = 1 AND d.account_id = :doctorAccountId " + filterCondition +
                         " ORDER BY hp.full_name ";
            NativeQuery query = session.createNativeQuery(sql);
            query.setParameter("doctorAccountId", doctorAccountId);
            if( !StringUtil.isNullOrEmpty(healthProfileFullName) )
                query.setParameter("healthProfileFullName", healthProfileFullName.trim());
            query.setResultTransformer(Transformers.aliasToBean(HealthProfilesByDoctor.class));
            return query.getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }
    
    public DetailsHealthProfile findDetailsHealthProfile(String healthProfileId) {
        Session session = openSession();
        try {
            String sql = " SELECT hp.id AS healthProfileId, hp.full_name AS fullName, hp.birth_day AS birthDate, hp.gender " +
                         " FROM health_profile hp " +
                         " INNER JOIN home_admin ha ON ha.id = hp.home_admin_id " +
                         " WHERE hp.active = 1 AND hp.id = :healthProfileId ";
            NativeQuery query = session.createNativeQuery(sql);
            query.setParameter("healthProfileId", healthProfileId);
            query.setResultTransformer(Transformers.aliasToBean(DetailsHealthProfile.class));
            Object result = query.uniqueResult();
            return result!=null ? (DetailsHealthProfile) query.uniqueResult() : null;
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }
    
    public List<Doctor> findDoctorsByHomeAdminId(String homeAdminId) {
        Session session = openSession();
        try {
            String sql = " SELECT d.* FROM doctor d " +
                         " INNER JOIN doctor_home_admin_map dham ON d.id = dham.doctor_id " +
                         " WHERE d.status = 1 AND dham.home_admin_id = :homeAdminId ";
            return session.createNativeQuery(sql, Doctor.class)
                        .setParameter("homeAdminId", homeAdminId)
                        .list();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }
}
