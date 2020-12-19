package com.elcom.vitalsign.repository.measuredata;

import com.elcom.vitalsign.model.dto.Spo2DataByHealthProfile;
import com.elcom.vitalsign.model.measuredata.DataSpo2;
import com.elcom.vitalsign.model.measuredata.DataSpo2Latest;
import com.elcom.vitalsign.utils.StringUtil;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
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
public class DataSpo2CustomizeRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSpo2CustomizeRepository.class);

    private SessionFactory sessionFactory;

    @Autowired
    public DataSpo2CustomizeRepository(EntityManagerFactory factory) {
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("factory is not a hibernate factory");
        }

        this.sessionFactory = factory.unwrap(SessionFactory.class);
    }

    public List<Spo2DataByHealthProfile> findDataSpo2LatestByHealthProfileIdLst(String[] healthProfileIdLst) {
        Session session = openSession();
        try {
            String sql = " SELECT user_profile_id AS healthProfileId, spo2, pi, pr, step, ts, UNIX_TIMESTAMP(last_updated_at) AS lastUpdatedAt " +
                         " FROM data_spo2_latest " +
                         " WHERE user_profile_id IN :healthProfileId ";
            return session.createNativeQuery(sql)
                    .setParameterList("healthProfileId", healthProfileIdLst)
                    .addScalar("healthProfileId", StringType.INSTANCE)
                    .addScalar("spo2", LongType.INSTANCE)
                    .addScalar("pi", IntegerType.INSTANCE)
                    .addScalar("pr", DoubleType.INSTANCE)
                    .addScalar("step", LongType.INSTANCE)
                    .addScalar("ts", LongType.INSTANCE)
                    .addScalar("lastUpdatedAt", LongType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(Spo2DataByHealthProfile.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }
    
    public List<Spo2DataByHealthProfile> findDataSpo2WithRangeByHealthProfileId(String healthProfileId, String filterType) {
        Session session = openSession();
        try {
            String partition = StringUtil.getPartitionName(filterType);
            if( partition==null )
                return null;
            String sql = " SELECT user_profile_id AS healthProfileId, spo2, pi, pr, step, ts, UNIX_TIMESTAMP(created_at) AS lastUpdatedAt " +
                         " FROM data_spo2 PARTITION ("+ partition +") " +
                         " WHERE user_profile_id = :healthProfileId ORDER BY created_at DESC ";
            return session.createNativeQuery(sql)
                    .setParameter("healthProfileId", healthProfileId.trim())
                    .addScalar("healthProfileId", StringType.INSTANCE)
                    .addScalar("spo2", LongType.INSTANCE)
                    .addScalar("pi", IntegerType.INSTANCE)
                    .addScalar("pr", DoubleType.INSTANCE)
                    .addScalar("step", LongType.INSTANCE)
                    .addScalar("ts", LongType.INSTANCE)
                    .addScalar("lastUpdatedAt", LongType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(Spo2DataByHealthProfile.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }
    
    public void progressDataLatest(DataSpo2 item, String gateIdLst) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            try {
                Query query = session.createNativeQuery(" SELECT id FROM data_spo2_latest WHERE gate_id = ? AND user_profile_id = ?");
                query.setParameter(1, item.getGateId());
                query.setParameter(2, item.getUserProfileId());
                Object result = query.getSingleResult();

                // Không throws NoResultException thì là tìm thấy, cần update
                Long id = ((Integer) result).longValue();
                query = session.createNativeQuery(" UPDATE data_spo2_latest SET measure_id = ?, ts = ? "
                        + ", spo2 = ?, pi = ?, pr = ?, step = ?  WHERE id = ? ");
                query.setParameter(1, item.getMeasureId());
                query.setParameter(2, item.getTs());
                query.setParameter(3, item.getSpo2());
                query.setParameter(4, item.getPi());
                query.setParameter(5, item.getPr());
                query.setParameter(6, item.getStep());
                query.setParameter(7, id);
                query.executeUpdate();

                //Đánh dấu gate đang hoạt động
//                query = session.createNativeQuery(" UPDATE gate SET activity_state = ?, activity_last_time = current_timestamp() WHERE status = 1 AND activity_state = ? AND id IN (" + gateIdLst + ") ");
//                query.setParameter(1, Constant.SENSOR_MEASURE_STATE_ACTIVE);
//                query.setParameter(2, Constant.SENSOR_MEASURE_STATE_INACTIVE);
//                query.executeUpdate();

            } catch (NoResultException ex) {
                //Ko tìm thấy thì insert mới
                LOGGER.error(ex.toString());
                session.save(new DataSpo2Latest(item.getGateId(), item.getUserProfileId(), item.getMeasureId(),
                        item.getTs(), item.getSpo2(), item.getPi(), item.getPr(), item.getStep()));
            }
            tx.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
    }

    private Session openSession() {
        return this.sessionFactory.openSession();
    }

    private void closeSession(Session session) {
        if (session != null) {
            session.close();
        }
    }
}
