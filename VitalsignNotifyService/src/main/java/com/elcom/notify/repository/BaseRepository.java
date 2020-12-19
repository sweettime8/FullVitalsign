package com.elcom.notify.repository;

import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhdv
 */
@Repository
public class BaseRepository {

    protected SessionFactory sessionFactory;

    protected BaseRepository(EntityManagerFactory factory) {
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("factory is not a hibernate factory");
        }

        this.sessionFactory = factory.unwrap(SessionFactory.class);
    }

    protected Session openSession() {
        Session session = this.sessionFactory.openSession();
        return session;
    }

    protected void closeSession(Session session) {
        if (session != null && session.isOpen()) {
            session.disconnect();
            session.close();
        }
    }
}
