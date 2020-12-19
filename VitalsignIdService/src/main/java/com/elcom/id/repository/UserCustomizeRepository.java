package com.elcom.id.repository;

import com.elcom.id.constant.Constant;
import com.elcom.id.model.User;
import com.elcom.id.utils.StringUtil;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhdv
 */
@Repository
public class UserCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCustomizeRepository.class);

    @Autowired
    public UserCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public User findByUuid(String uuid) {
        Session session = openSession();
        try {
            User user = session.load(User.class, uuid);
            return user;
        } catch (EntityNotFoundException ex) {
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
            Query query = session.createNativeQuery("SELECT * FROM user WHERE email = ?", User.class);
            query.setParameter(1, userName);
            result = query.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (User) result : null;
    }

    public boolean updateLastLogin(String uuid, String loginIp) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String update = " ";
            if (!StringUtil.isNullOrEmpty(loginIp)) {
                update += ", login_ip = :loginIp ";
            }
            String sql = "Update user SET last_login = now() " + update + " WHERE uuid = :uuid ";
            Query query = session.createNativeQuery(sql);
            if (!StringUtil.isNullOrEmpty(loginIp)) {
                query.setParameter("loginIp", loginIp);
            }
            query.setParameter("uuid", uuid);
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

    public boolean updateUser(User user) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String update = " ";
            if (!StringUtil.isNullOrEmpty(user.getMobile())) {
                update += ", mobile = :mobile ";
            }
            if (!StringUtil.isNullOrEmpty(user.getFullName())) {
                update += ", full_name = :fullName ";
            }
            if (!StringUtil.isNullOrEmpty(user.getSkype())) {
                update += ", skype = :skype ";
            }
            if (!StringUtil.isNullOrEmpty(user.getFacebook())) {
                update += ", facebook = :facebook ";
            }
            if (!StringUtil.isNullOrEmpty(user.getAvatar())) {
                update += ", avatar = :avatar ";
            }
            if (!StringUtil.isNullOrEmpty(user.getAddress())) {
                update += ", address = :address ";
            }
            if (!StringUtil.isNullOrEmpty(user.getBirthDay())) {
                update += ", birth_day = :birthDay ";
            }
            if (user.getGender() != null) {
                update += ", gender = :gender ";
            }
            if(user.getProfileUpdate() != null) {
                update += ", profile_update = :profileUpdate ";
            }
            if(user.getAvatarUpdate() != null) {
                update += ", avatar_update = :avatarUpdate ";
            }
            String sql = "Update user SET last_update = now() " + update + " WHERE uuid = :uuid ";
            Query query = session.createNativeQuery(sql);
            if (!StringUtil.isNullOrEmpty(user.getMobile())) {
                query.setParameter("mobile", user.getMobile());
            }
            if (!StringUtil.isNullOrEmpty(user.getFullName())) {
                query.setParameter("fullName", user.getFullName());
            }
            if (!StringUtil.isNullOrEmpty(user.getSkype())) {
                query.setParameter("skype", user.getSkype());
            }
            if (!StringUtil.isNullOrEmpty(user.getFacebook())) {
                query.setParameter("facebook", user.getFacebook());
            }
            if (!StringUtil.isNullOrEmpty(user.getAvatar())) {
                query.setParameter("avatar", user.getAvatar());
            }
            if (!StringUtil.isNullOrEmpty(user.getAddress())) {
                query.setParameter("address", user.getAddress());
            }
            if (!StringUtil.isNullOrEmpty(user.getBirthDay())) {
                query.setParameter("birthDay", user.getBirthDay());
            }
            if (user.getGender() != null) {
                query.setParameter("gender", user.getGender());
            }
            if(user.getProfileUpdate() != null) {
                query.setParameter("profileUpdate", user.getProfileUpdate());
            }
            if(user.getAvatarUpdate() != null) {
                query.setParameter("avatarUpdate", user.getAvatarUpdate());
            }
            query.setParameter("uuid", user.getUuid());
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

    public boolean insertTest() {
        Session session = openSession();
        try {
            for (int i = 1; i <= 4; i++) {
                Query query = session.createNativeQuery(" insert into elcom_user(username, password, full_name) "
                        + " values(:userName, :password, :fullName ) ");
                query.setParameter("userName", "anhdv_" + i);
                query.setParameter("password", "anhdv_pw_" + i);
                query.setParameter("fullName", "do viet anh_" + i);
                query.executeUpdate();
            }
        } catch (NoResultException ex) {
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
            LOGGER.info("total user: " + total);

        } catch (NoResultException ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return true; //enter your condition
    }

    public User findByEmail(String email) {
        Session session = openSession();
        Object result = null;
        try {
            Query query = session.createNativeQuery(" SELECT * FROM user WHERE email = ? ", User.class);
            query.setParameter(1, email.trim());
            result = query.getSingleResult();
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (User) result : null;
    }
    
    public User findByMobile(String mobile) {
        Session session = openSession();
        Object result = null;
        try {
            Query query = session.createNativeQuery("SELECT * FROM user WHERE mobile = ?", User.class);
            query.setParameter(1, mobile);
            result = query.getSingleResult();
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (User) result : null;
    }
    
    public User findByUserName(String userName) {
        Session session = openSession();
        Object result = null;
        try {
            Query query = session.createNativeQuery("SELECT * FROM user WHERE user_name = ?", User.class);
            query.setParameter(1, userName);
            result = query.getSingleResult();
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (User) result : null;
    }

    public User findAppleAccount(String userId, String email) {
        Session session = openSession();
        try {
            String sql = " FROM User u WHERE u.appleId = :appleId AND u.email = :email ";
            Query query = session.createQuery(sql, User.class)
                                .setParameter("appleId", userId.trim())
                                .setParameter("email", email.trim());
            Object object = query.getSingleResult();
            return object != null ? (User) object : null;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return null;
    }
    
    public User findByEmailOrMobile(String userInfo) {
        Session session = openSession();
        Object result = null;
        try {
            Query query = session.createNativeQuery("SELECT * FROM user WHERE "
                    + " (email = ? AND is_delete = 0) OR (mobile = ? AND is_delete = 0) ", User.class);
            query.setParameter(1, userInfo);
            query.setParameter(2, userInfo);
            result = query.getSingleResult();
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return result != null ? (User) result : null;
    }
    
    public User findBySocial(Integer signupType, String socialId) {
        Session session = openSession();
        try {
            String sql = "FROM User u WHERE u.signupType = :signupType ";
            switch (signupType) {
                case Constant.USER_SIGNUP_FACEBOOK:
                    sql += " AND u.fbId = :socialId ";
                    break;
                case Constant.USER_SIGNUP_GOOGLE:
                    sql += " AND u.ggId = :socialId ";
                    break;
                case Constant.USER_SIGNUP_APPLE:
                    sql += " AND u.appleId = :socialId ";
                    break;
                default:
                    break;
            }
            
            Query query = session.createQuery(sql, User.class)
                                .setParameter("signupType", signupType)
                                .setParameter("socialId", socialId.trim());
            Object object = query.getSingleResult();
            return object != null ? (User) object : null;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return null;
    }
    
    public boolean connectSocial(User user, Integer socialType, String socialId) {
        Session session = openSession();
        try {
            Transaction tx = session.beginTransaction();
            String update = "";
            switch (socialType) {
                case Constant.USER_SIGNUP_FACEBOOK:
                    update = " u.fbId = :socialId ";
                    break;
                case Constant.USER_SIGNUP_GOOGLE:
                    update = " u.ggId = :socialId ";
                    break;
                case Constant.USER_SIGNUP_APPLE:
                    update = " u.appleId = :socialId ";
                    break;
                default:
                    break;
            }
            String sql = "Update User u SET " + update + " WHERE u.uuid = :uuid ";
            Query query = session.createQuery(sql);
            query.setParameter("socialId", socialId.trim());
            query.setParameter("uuid", user.getUuid());
            
            int result = query.executeUpdate();
            tx.commit();
            return result > 0;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return false;
    }
}
