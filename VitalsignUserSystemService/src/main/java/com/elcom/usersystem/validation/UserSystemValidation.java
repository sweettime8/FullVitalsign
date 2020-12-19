package com.elcom.usersystem.validation;

import com.elcom.usersystem.model.Doctor;
import com.elcom.usersystem.model.DoctorHomeAdminMaps;
import com.elcom.usersystem.model.HealthProfile;
import com.elcom.usersystem.model.HomeAdmin;
import com.elcom.usersystem.utils.StringUtil;

public class UserSystemValidation extends AbstractValidation {

    public String validateAddDoctorForCustomer(DoctorHomeAdminMaps doctorHomeAdminMaps) {
        if (doctorHomeAdminMaps == null) {
            return "payLoad không hợp lệ";
        }
        if (StringUtil.isNullOrEmpty(doctorHomeAdminMaps.getDoctorId())) {
            getMessageDes().add("DoctorId không được để trống");
        } else if (!StringUtil.validUuid(doctorHomeAdminMaps.getDoctorId())) {
            getMessageDes().add("DoctorId (UUID) chứa giá trị không hợp lệ: [" + doctorHomeAdminMaps.getDoctorId().trim() + "]");
        }
        if (StringUtil.isNullOrEmpty(doctorHomeAdminMaps.getHomeAdminId())) {
            getMessageDes().add("CustomerId không được để trống");
        } else if (!StringUtil.validUuid(doctorHomeAdminMaps.getHomeAdminId())) {
            getMessageDes().add("CustomerId (UUID) chứa giá trị không hợp lệ: [" + doctorHomeAdminMaps.getHomeAdminId().trim() + "]");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateUpdateCustomer(HomeAdmin ha) {
        if (ha == null) {
            return "payLoad không hợp lệ";
        }

        if (StringUtil.isNullOrEmpty(ha.getFullName())) {
            getMessageDes().add("fullName không được để trống");
        }
        if (StringUtil.isNullOrEmpty(ha.getEmail())) {
            getMessageDes().add("email không được để trống");
        }
        if (!StringUtil.isNullOrEmpty(ha.getEmail()) && !StringUtil.validateEmail(ha.getEmail())) {
            getMessageDes().add("email không đúng định dạng");
        }
        if (!StringUtil.isNullOrEmpty(ha.getPhoneNumber()) && !StringUtil.checkMobilePhoneNumberNew(ha.getPhoneNumber())) {
            getMessageDes().add("Phone Number không đúng định dạng");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateAddCustomer(HomeAdmin ha) {
        if (ha == null) {
            return "payLoad không hợp lệ";
        }

        if (StringUtil.isNullOrEmpty(ha.getFullName())) {
            getMessageDes().add("fullName không được để trống");
        }
        if (StringUtil.isNullOrEmpty(ha.getEmail())) {
            getMessageDes().add("email không được để trống");
        }
        if (!StringUtil.isNullOrEmpty(ha.getEmail()) && !StringUtil.validateEmail(ha.getEmail())) {
            getMessageDes().add("email không đúng định dạng");
        }
        if (!StringUtil.isNullOrEmpty(ha.getPhoneNumber()) && !StringUtil.checkMobilePhoneNumberNew(ha.getPhoneNumber())) {
            getMessageDes().add("Phone Number không đúng định dạng");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateFindCustomerById(String id) {
        if (StringUtil.isNullOrEmpty(id)) {
            getMessageDes().add("Id không được để trống");
        } else if (!StringUtil.validUuid(id)) {
            getMessageDes().add("Id (UUID) chứa giá trị không hợp lệ: [" + id.trim() + "]");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateFindDoctorByDoctorId(String doctorId) {

        if (StringUtil.isNullOrEmpty(doctorId)) {
            getMessageDes().add("doctorId không được để trống");
        } else if (!StringUtil.validUuid(doctorId)) {
            getMessageDes().add("doctorId (UUID) chứa giá trị không hợp lệ: [" + doctorId.trim() + "]");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateFindDoctors(String specialist, String fullName) {

        if (StringUtil.isNullOrEmpty(specialist)) {
            getMessageDes().add("specialist không được để trống");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateFindHealthProfilesByDoctor(String doctorId) {

        if (StringUtil.isNullOrEmpty(doctorId)) {
            getMessageDes().add("doctorId không được để trống");
        } else if (!StringUtil.validUuid(doctorId)) {
            getMessageDes().add("doctorId (UUID) chứa giá trị không hợp lệ: [" + doctorId.trim() + "]");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateFindDetailsHealthProfile(String healthProfileId) {

        if (StringUtil.isNullOrEmpty(healthProfileId)) {
            getMessageDes().add("healthProfileId không được để trống");
        } else if (!StringUtil.validUuid(healthProfileId)) {
            getMessageDes().add("healthProfileId (UUID) chứa giá trị không hợp lệ: [" + healthProfileId.trim() + "]");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateFindListProfileByHomeAdmin(String homeAdminId) {

        if (StringUtil.isNullOrEmpty(homeAdminId)) {
            getMessageDes().add("homeAdminId không được để trống");
        } else if (!StringUtil.validUuid(homeAdminId)) {
            getMessageDes().add("homeAdminId (UUID) chứa giá trị không hợp lệ: [" + homeAdminId.trim() + "]");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateInsertProfile(HealthProfile healthProfile) {
        if (healthProfile == null) {
            return "payLoad không hợp lệ";
        }

        if (StringUtil.isNullOrEmpty(healthProfile.getFullName())) {
            getMessageDes().add("fullName không được để trống");
        }

        if (StringUtil.isNullOrEmpty(healthProfile.getBirthDay())) {
            getMessageDes().add("BirthDay không được để trống");
        }
        if (StringUtil.isFloatNumberNullOrEmpty(healthProfile.getHeight())) {
            getMessageDes().add("Height không được để trống");
        }
        if (StringUtil.isFloatNumberNullOrEmpty(healthProfile.getWeight())) {
            getMessageDes().add("Weight không được để trống");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateAddDoctor(Doctor doctor) {
        if (doctor == null) {
            return "payLoad không hợp lệ";
        }

        if (StringUtil.isNullOrEmpty(doctor.getFullName())) {
            getMessageDes().add("fullName không được để trống");
        }
        if (StringUtil.isNullOrEmpty(doctor.getEmail())) {
            getMessageDes().add("email không được để trống");
        }
        if (!StringUtil.isNullOrEmpty(doctor.getEmail()) && !StringUtil.validateEmail(doctor.getEmail())) {
            getMessageDes().add("email không đúng định dạng");
        }

        if (StringUtil.isNullOrEmpty(doctor.getSpecialist())) {
            getMessageDes().add("Specialist không được để trống");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateUpdateDoctor(Doctor doctor) {
        if (doctor == null) {
            return "payLoad không hợp lệ";
        }

        if (StringUtil.isNullOrEmpty(doctor.getFullName())) {
            getMessageDes().add("fullName không được để trống");
        }

        if (StringUtil.isNullOrEmpty(doctor.getSpecialist())) {
            getMessageDes().add("Specialist không được để trống");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }
}
