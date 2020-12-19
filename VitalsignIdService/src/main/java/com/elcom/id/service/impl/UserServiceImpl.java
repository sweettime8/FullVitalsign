package com.elcom.id.service.impl;

import com.elcom.id.model.DoctorPatientMap;
import com.elcom.id.model.PatientProfile;
import com.elcom.id.model.User;
import com.elcom.id.model.UserSpecs;
import com.elcom.id.model.dto.DoctorPatientMapDTO;
import com.elcom.id.model.dto.ListUserProfileDTO;
import com.elcom.id.model.dto.UserPagingDTO;
import com.elcom.id.repository.DoctorPatientMapCustomizeRepository;
import com.elcom.id.repository.UserCustomizeRepository;
import com.elcom.id.repository.PatientProfileCustomizeRepository;
import com.elcom.id.repository.PatientProfileRepository;
import com.elcom.id.repository.UserRepository;
import com.elcom.id.service.UserService;
import io.netty.util.internal.StringUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCustomizeRepository userCustomizeRepository;

    @Autowired
    private PatientProfileCustomizeRepository patientProfileCustomizeRepository;

    @Autowired
    private DoctorPatientMapCustomizeRepository doctorUserMapCustomizeRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    public long countAll() {
        return userRepository.count();
    }

    @Override
    public UserPagingDTO findAll(String keyword, Integer status, Integer currentPage,
            Integer rowsPerPage, String sort) {
        UserPagingDTO result = new UserPagingDTO();
        try {
            if (currentPage > 0) {
                currentPage--;
            }
            if (StringUtil.isNullOrEmpty(sort)) {
                sort = "createdAt";
            }
            Pageable paging = PageRequest.of(currentPage, rowsPerPage, Sort.by(sort).descending());
            Page<User> pagedResult = null;
            //LOGGER.info("keyword: " + keyword + ", status: " + status);
            if (StringUtil.isNullOrEmpty(keyword) && status == null) {
                pagedResult = userRepository.findAll(paging);
            } else if (StringUtil.isNullOrEmpty(keyword) && status != null) {
                pagedResult = userRepository.findByStatus(status, paging);
            } else if (!StringUtil.isNullOrEmpty(keyword) && status == null) {
                pagedResult = userRepository.findAll(Specification.where(UserSpecs.searchByKeyword(keyword)), paging);
            } else {
                pagedResult = userRepository.findAll(Specification.where(UserSpecs.searchByStatusAndKeyword(status, keyword)), paging);
            }
            if (pagedResult != null && pagedResult.hasContent()) {
                result.setDataRows(pagedResult.getContent());
                result.setTotalRows(pagedResult.getTotalElements());
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
    }

    @Override
    public User findByUuid(String uuid) {
        return userCustomizeRepository.findByUuid(uuid);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void remove(User user) {
        userRepository.delete(user);
    }

    @Override
    public boolean insertTest() {
        return userCustomizeRepository.insertTest();
    }

    @Override
    public boolean updateLastLogin(String uuid, String loginIp) {
        return userCustomizeRepository.updateLastLogin(uuid, loginIp);
    }

    @Override
    public boolean update(User user) {
        return userCustomizeRepository.updateUser(user);
    }

    @Override
    public User findByEmail(String email) {
        try {
            return userCustomizeRepository.findByEmail(email);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public User findByMobile(String mobile) {
        return userCustomizeRepository.findByMobile(mobile);
    }

    @Override
    public User findByUserName(String userName) {
        return userCustomizeRepository.findByUserName(userName);
    }

    @Override
    public User findBySocial(Integer signupType, String socialId) {
        try {
            return userCustomizeRepository.findBySocial(signupType, socialId);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public User findAppleAccount(String userId, String email) {
        try {
            return userCustomizeRepository.findAppleAccount(userId, email);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public User findByEmailOrMobile(String userInfo) {
        try {
            return userCustomizeRepository.findByEmailOrMobile(userInfo);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean connectSocial(User user, Integer socialType, String socialId) {
        return userCustomizeRepository.connectSocial(user, socialType, socialId);
    }

    @Override
    public List<User> findByUuidIn(List<String> uuidList) {
        return userRepository.findByUuidIn(uuidList);
    }

    @Override
    public boolean changePassword(User user) {
        return userRepository.changePassword(user.getPassword(), user.getSetPassword(), user.getUuid()) > 0;
    }

    @Override
    public boolean changeEmail(User user) {
        return userRepository.changeEmail(user.getEmail(), user.getEmailVerify(), user.getUuid()) > 0;
    }

    @Override
    public boolean changeMobile(User user) {
        return userRepository.changeMobile(user.getMobile(), user.getMobileVerify(), user.getUuid()) > 0;
    }

    @Override
    public boolean changeStatus(User user) {
        return userRepository.changeStatus(user.getStatus(), user.getUuid()) > 0;
    }

    @Override
    public boolean changeOtpMobile(User user) {
        return userRepository.changeOtpMobile(user.getOtpMobile(), user.getMobileVerify(), user.getUuid()) > 0;
    }

    @Override
    public List<User> findByStatus(Integer status) {
        return userRepository.findByStatus(status);
    }

    @Override
    public boolean createOTP(User user, String otp) {
        return userRepository.createOTP(otp, user.getOtpTime(), user.getUuid()) > 0;
    }

    @Override
    public boolean updateMobileVerify(User user, int mobileVerify) {
        return userRepository.updateMobileVerify(mobileVerify, user.getUuid()) > 0;
    }

    @Override
    public List<ListUserProfileDTO> findListUserProfile(String uuid) {
        return patientProfileCustomizeRepository.findByUserId(uuid);
    }

    @Override
    public List<DoctorPatientMapDTO> findDoctorsByPatient(String uuid) {
        return doctorUserMapCustomizeRepository.findByPatientId(uuid);
    }

    @Override
    public void saveProfile(PatientProfile patientProfile) {
        patientProfileRepository.save(patientProfile);
    }

    @Override
    public boolean updatePatientProfile(PatientProfile patientProfile) {
        return patientProfileCustomizeRepository.updatePatientProfile(patientProfile);
    }

    @Override
    public PatientProfile findProfileById(String id) {
         return patientProfileCustomizeRepository.findById(id);
    }

    @Override
    public boolean deletePatientProfile(PatientProfile patientProfile) {
        return patientProfileCustomizeRepository.deletePatientProfile(patientProfile);
    }
}
