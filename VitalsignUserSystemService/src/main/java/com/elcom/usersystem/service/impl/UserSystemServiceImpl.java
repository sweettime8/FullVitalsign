package com.elcom.usersystem.service.impl;

import com.elcom.usersystem.model.Doctor;
import com.elcom.usersystem.model.DoctorHomeAdminMaps;
import com.elcom.usersystem.model.HealthProfile;
import com.elcom.usersystem.model.HomeAdmin;
import com.elcom.usersystem.model.dto.DetailsHealthProfile;
import com.elcom.usersystem.model.dto.HealthProfilesByDoctor;
import com.elcom.usersystem.model.dto.HomeAdminDTO;
import com.elcom.usersystem.repository.DoctorCustomizeRepository;
import com.elcom.usersystem.repository.DoctorHomeAdminMapCustomRepository;
import com.elcom.usersystem.repository.DoctorHomeAdminMapRepository;
import com.elcom.usersystem.repository.DoctorRepository;
import com.elcom.usersystem.repository.HealthProfileCustomizeRepository;
import com.elcom.usersystem.repository.HealthProfileRepository;
import com.elcom.usersystem.repository.HomeAdminCustomizeRepository;
import com.elcom.usersystem.repository.HomeAdminRepository;
import com.elcom.usersystem.repository.UserSystemCustomizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.elcom.usersystem.utils.StringUtil;
import com.elcom.usersystem.service.UserSystemService;
import java.util.Optional;

/**
 *
 * @author anhdv
 */
@Service
public class UserSystemServiceImpl implements UserSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSystemServiceImpl.class);

    @Autowired
    private UserSystemCustomizeRepository userSystemCustomizeRepository;

    @Autowired
    private HomeAdminRepository homeAdminRepository;

    @Autowired
    private HomeAdminCustomizeRepository homeAdminCustomizeRepository;

    @Autowired
    private HealthProfileRepository healthProfileRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorCustomizeRepository doctorCustomizeRepository;

    @Autowired
    private HealthProfileCustomizeRepository healthProfileCustomizeRepository;

    @Autowired
    private DoctorHomeAdminMapRepository doctorHomeAdminMapRepository;

    @Autowired
    private DoctorHomeAdminMapCustomRepository doctorHomeAdminMapCustomRepository;

    @Override
    public Optional<HomeAdmin> findHomeAdminById(String homeAdminId) {
        return this.homeAdminRepository.findById(homeAdminId);
    }

    @Override
    public List<HealthProfile> findHealthProfileByHomeAdminId(String homeAdminId) {
        return this.healthProfileRepository.findByHomeAdminId(homeAdminId);
    }

    @Override
    public List<Doctor> findDoctorsByHomeAdminId(String homeAdminId) {
        return this.userSystemCustomizeRepository.findDoctorsByHomeAdminId(homeAdminId);
    }

    @Override
    public List<HealthProfilesByDoctor> findHealthProfilesByDoctor(String doctorAccountId, String healthProfileFullName) {
        List<HealthProfilesByDoctor> lst = null;
        try {
            lst = this.userSystemCustomizeRepository.findHealthProfilesByDoctor(doctorAccountId, healthProfileFullName);
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return lst;
    }

    @Override
    public DetailsHealthProfile findDetailsHealthProfile(String healthProfileId) {
        DetailsHealthProfile item = null;
        try {
            item = this.userSystemCustomizeRepository.findDetailsHealthProfile(healthProfileId);
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return item;
    }

    @Override
    public void saveHealthProfile(HealthProfile healthProfile) {
        healthProfileRepository.save(healthProfile);
    }

    @Override
    public boolean updateHealthProfile(HealthProfile healthProfile) {
        return healthProfileCustomizeRepository.updateHealthProfile(healthProfile);
    }

    @Override
    public HealthProfile findHealthProfileById(String id) {
        return healthProfileCustomizeRepository.findById(id);
    }

    @Override
    public boolean deleteHealthProfile(HealthProfile healthProfile) {
        return healthProfileCustomizeRepository.deleteHealthProfile(healthProfile);
    }

    @Override
    public void addDoctorForBu(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    @Override
    public boolean updateDoctor(Doctor doctor) {
        return doctorCustomizeRepository.updateDoctor(doctor);
    }

    @Override
    public boolean deleteDoctor(String doctorId) {
        return doctorCustomizeRepository.deleteDoctor(doctorId);
    }

    @Override
    public Doctor findDoctorByDoctorId(String doctorId) {
        Doctor doctor = null;
        try {
            doctor = this.doctorCustomizeRepository.findById(doctorId);
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return doctor;
    }

    @Override
    public List<Doctor> findDoctors(String buId, String specialist, String fullName) {
        List<Doctor> lst = null;
        try {
            lst = this.doctorCustomizeRepository.findDoctors(buId, specialist, fullName);
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return lst;
    }

    @Override
    public List<Doctor> findDoctorsbyName(String buId, String fullName) {
        List<Doctor> lst = null;
        try {
            lst = this.doctorCustomizeRepository.findDoctorsbyName(buId, fullName);
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return lst;
    }

    @Override
    public List<HomeAdminDTO> findCustomerByBuId(String buId) {
        return this.homeAdminCustomizeRepository.findByBuId(buId);
    }

    @Override
    public void addCustomer(HomeAdmin ha) {
        homeAdminRepository.save(ha);
    }

    @Override
    public HomeAdmin findCustomerById(String id) {
        HomeAdmin ha = null;
        try {
            ha = this.homeAdminCustomizeRepository.findById(id);
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return ha;
    }

    @Override
    public boolean updateCustomer(HomeAdmin ha) {
        return homeAdminCustomizeRepository.updateCustomer(ha);
    }

    @Override
    public boolean deleteCustomer(String haId) {
        return homeAdminCustomizeRepository.deleteCustomer(haId);
    }

    @Override
    public boolean deleteHealthProfileOfCustomer(String haId) {
        return healthProfileCustomizeRepository.deleteHealthProfileOfCustomer(haId);
    }

    @Override
    public void addDoctorForCustomer(DoctorHomeAdminMaps doctorHomeAdminMaps) {
        doctorHomeAdminMapRepository.save(doctorHomeAdminMaps);
    }

    @Override
    public DoctorHomeAdminMaps findDoctorPairHomeAdmin(String customerId, String doctorId) {
        DoctorHomeAdminMaps dham = null;
        try {
            dham = this.doctorHomeAdminMapCustomRepository.findDoctorPairHomeAdmin(customerId ,doctorId);
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        return dham;

    }

    @Override
    public boolean removeDoctorPairCustomer(Long id) {
        return doctorHomeAdminMapCustomRepository.removeDoctorPairCustomer(id);
    }

}
