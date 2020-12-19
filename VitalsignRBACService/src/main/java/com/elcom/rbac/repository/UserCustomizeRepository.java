package com.elcom.rbac.repository;

import com.elcom.rbac.model.User;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhdv
 */
@Repository
public class UserCustomizeRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCustomizeRepository.class);
    
    private SessionFactory sessionFactory;
    
    @Autowired
    public UserCustomizeRepository(EntityManagerFactory factory) {
        if(factory.unwrap(SessionFactory.class) == null)
            throw new NullPointerException("factory is not a hibernate factory");
        
        this.sessionFactory = factory.unwrap(SessionFactory.class);
    }
    
    public User findById(Long id) {
        Session session = openSession();
        try {
            User user = session.load(User.class, id);
            return user;
        }catch(EntityNotFoundException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return null;
    }
    
    public User findByUsername(String userName) {
        Session session = openSession();
        Object result = null;
        try {
            Query query = session.createNativeQuery("SELECT * FROM elcom_user WHERE username = ?", User.class);
            query.setParameter(1, userName);
            result = query.getSingleResult();
        }catch(NoResultException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (User) result : null;
    }
    
    public boolean insertTest() {
        Session session = openSession();
        try {
            for( int i=1; i<=4; i++ ) {
                Query query = session.createNativeQuery(" insert into elcom_user(username, password, full_name) "
                                                        + " values(:userName, :password, :fullName ) ");
                query.setParameter("userName", "anhdv_" + i);
                query.setParameter("password", "anhdv_pw_" + i);
                query.setParameter("fullName", "do viet anh_" + i);
                query.executeUpdate();
            }
        }catch(NoResultException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return true;
    }
    
    public Boolean countUser(String username, String password) {
        
        Session session = openSession();
        try {
            StoredProcedureQuery query = session.createStoredProcedureQuery("countUser");

            query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN); //userName
            query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN); //fullName
            query.registerStoredProcedureParameter(3, Integer.class, ParameterMode.OUT); //total count

            query.setParameter(1, username);
            query.setParameter(2, password);

            query.execute();

            Integer total = (Integer) query.getOutputParameterValue(3);
            System.out.println("total user: " + total);
        
        }catch(NoResultException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return true; //enter your condition
    }
    
    private Session openSession() {
        Session session = this.sessionFactory.openSession();
        return session;
    }
    
    private void closeSession(Session session) {
        if( session.isOpen() ) {
            session.disconnect();
            session.close();
        }
    }
}
