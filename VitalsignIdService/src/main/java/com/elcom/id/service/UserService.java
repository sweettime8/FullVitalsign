package com.elcom.id.service;

import com.elcom.id.model.PatientProfile;
import com.elcom.id.model.User;
import com.elcom.id.model.dto.DoctorPatientMapDTO;
import com.elcom.id.model.dto.ListUserProfileDTO;
import com.elcom.id.model.dto.UserPagingDTO;
import java.util.List;
//import java.util.Optional;

/**
 *
 * @author anhdv
 */
public interface UserService {

    UserPagingDTO findAll(String keyword, Integer status, Integer currentPage, Integer rowsPerPage, String sort);
    
    void saveProfile(PatientProfile patientProfile);
    
    boolean updatePatientProfile(PatientProfile patientProfile);
    
    PatientProfile findProfileById(String id);
    
    boolean deletePatientProfile(PatientProfile patientProfile);
    
    List<DoctorPatientMapDTO> findDoctorsByPatient(String uuid);
    
    List<ListUserProfileDTO> findListUserProfile(String uuid);
    
    User findByUuid(String uuid);
    
    User findByEmail(String email);
    
    User findByMobile(String mobile);
    
    User findByUserName(String userName);
    
    User findBySocial(Integer signupType, String socialId);
    
    User findAppleAccount(String userId, String email);
    
    User findByEmailOrMobile(String userInfo);
    
    List<User> findByUuidIn(List<String> uuidList);
    
    List<User> findByStatus(Integer status);
    
    void save(User user);
    
    boolean update(User user);
    
    boolean changePassword(User user);
    
    boolean changeEmail(User user);
    
    boolean changeMobile(User user);
    
    boolean changeStatus(User user);
    
    boolean changeOtpMobile(User user);

    void remove(User user);
    
    boolean insertTest();
    
    boolean updateLastLogin(String uuid, String loginIp);
    
    boolean connectSocial(User user, Integer socialType, String socialId);
    
    boolean createOTP(User user, String otp);
    
    boolean updateMobileVerify(User user, int mobileVerify);
    //User cacheGet(Long id);
}