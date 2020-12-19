package com.elcom.usersystem.service;

import com.elcom.usersystem.model.Doctor;
import com.elcom.usersystem.model.DoctorHomeAdminMaps;
import com.elcom.usersystem.model.HealthProfile;
import com.elcom.usersystem.model.HomeAdmin;
import com.elcom.usersystem.model.dto.DetailsHealthProfile;
import com.elcom.usersystem.model.dto.HealthProfilesByDoctor;
import com.elcom.usersystem.model.dto.HomeAdminDTO;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author anhdv
 */
public interface UserSystemService {

    List<HealthProfilesByDoctor> findHealthProfilesByDoctor(String doctorAccountId, String healthProfileFullName);

    DetailsHealthProfile findDetailsHealthProfile(String healthProfileId);

    Optional<HomeAdmin> findHomeAdminById(String homeAdminId);

    List<HealthProfile> findHealthProfileByHomeAdminId(String homeAdminId);

    List<Doctor> findDoctorsByHomeAdminId(String homeAdminId);

    void saveHealthProfile(HealthProfile healthProfile);

    boolean updateHealthProfile(HealthProfile healthProfile);

    HealthProfile findHealthProfileById(String id);

    boolean deleteHealthProfile(HealthProfile healthProfile);

    void addDoctorForBu(Doctor doctor);

    boolean updateDoctor(Doctor doctor);

    boolean deleteDoctor(String doctorId);

    Doctor findDoctorByDoctorId(String doctorId);

    List<Doctor> findDoctors(String buId, String specialist, String fullName);

    List<Doctor> findDoctorsbyName(String buId, String fullName);

    List<HomeAdminDTO> findCustomerByBuId(String buId);

    void addCustomer(HomeAdmin ha);

    HomeAdmin findCustomerById(String id);

    boolean updateCustomer(HomeAdmin ha);

    boolean deleteCustomer(String haId);

    boolean deleteHealthProfileOfCustomer(String haId);
    
    void addDoctorForCustomer(DoctorHomeAdminMaps doctorHomeAdminMaps);
    
    DoctorHomeAdminMaps findDoctorPairHomeAdmin(String customerId,String doctorId);
    
    boolean removeDoctorPairCustomer(Long id);
}
