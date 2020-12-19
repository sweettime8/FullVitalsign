/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.repository.measuredata;

import com.elcom.vitalsign.constant.Constant;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public class DataPartition {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataPartition.class);

    private SessionFactory sessionFactory;

    @Autowired
    public DataPartition(EntityManagerFactory factory) {
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("factory is not a hibernate factory");
        }

        this.sessionFactory = factory.unwrap(SessionFactory.class);
    }

    public void addDataPartition(String[] tables) {
        Session session = openSession();
        try {
            SimpleDateFormat format = new SimpleDateFormat("YYYY_MM_dd");

            Calendar calTomorrow = Calendar.getInstance();
            calTomorrow.add(Calendar.DATE, 1);
            String tomorrow = format.format(calTomorrow.getTime());

            Calendar calAfterTomorrow = Calendar.getInstance();
            calAfterTomorrow.add(Calendar.DATE, 2);
            String afterTomorrow = format.format(calAfterTomorrow.getTime());

            String partitionName = "p_" + tomorrow;
            String partitionValue = afterTomorrow + " 00:00:00";

            Transaction tx = session.beginTransaction();
            String strSql;
            Query query;
            for (String table : tables) {
                strSql = " ALTER TABLE " + table + " ADD PARTITION (PARTITION " + partitionName + " VALUES LESS THAN (UNIX_TIMESTAMP(?))) ";
                query = session.createNativeQuery(strSql);
                query.setParameter(1, partitionValue);
                query.executeUpdate();

                strSql = " DELETE FROM " + table + " WHERE measure_at < NOW() - INTERVAL ? DAY ";
                query = session.createNativeQuery(strSql);
                query.setParameter(1, Constant.DELETE_DATA_AFTER_DAY);
                query.executeUpdate();
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
