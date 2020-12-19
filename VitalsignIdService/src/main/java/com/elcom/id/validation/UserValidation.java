package com.elcom.id.validation;

import com.elcom.id.constant.Constant;
import com.elcom.id.exception.ValidationException;
import com.elcom.id.model.PatientProfile;
import com.elcom.id.model.User;
import com.elcom.id.model.dto.AuthorizationResponseDTO;
import com.elcom.id.utils.StringUtil;
import com.elcom.id.utils.VNCharacterUtils;
import com.elcom.id.utils.encrypt.TravisAes;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserValidation extends AbstractValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserValidation.class);

    public String validateInsertUser(User user) {
        if (user == null) {
            return "payLoad không hợp lệ";
        }

        //if (StringUtil.isNullOrEmpty(user.getUserName())) {
        //    getMessageDes().add("userName không được để trống");
        //}
        if (user.getSignupType() == Constant.USER_SIGNUP_NORMAL && StringUtil.isNullOrEmpty(user.getEmail())
                && StringUtil.isNullOrEmpty(user.getMobile())) {
            getMessageDes().add("email hoặc mobile không được để trống");
        }

        if (!StringUtil.isNullOrEmpty(user.getEmail()) && !StringUtil.validateEmail(user.getEmail())) {
            getMessageDes().add("email không đúng định dạng");
        }

        if (!StringUtil.isNullOrEmpty(user.getMobile()) && !StringUtil.checkMobilePhoneNumberNew(user.getMobile())) {
            getMessageDes().add("mobile không đúng định dạng");
        }

        if (StringUtil.isNullOrEmpty(user.getFullName())) {
            getMessageDes().add("fullName không được để trống");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateInsertProfile(PatientProfile patientProfile) {
        if (patientProfile == null) {
            return "payLoad không hợp lệ";
        }

        if (StringUtil.isNullOrEmpty(patientProfile.getFullName())) {
            getMessageDes().add("fullName không được để trống");
        }

        if (StringUtil.isNullOrEmpty(patientProfile.getBirthDay())) {
            getMessageDes().add("BirthDay không được để trống");
        }
        if (StringUtil.isNullOrEmpty(patientProfile.getHeight())) {
            getMessageDes().add("Height không được để trống");
        }
        if (StringUtil.isNullOrEmpty(patientProfile.getWeight())) {
            getMessageDes().add("Weight không được để trống");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateInsertSocial(User user) {
        if (user == null) {
            return "payLoad không hợp lệ";
        }

        LOGGER.info("ggId: " + user.getGgId() + ", fbId: " + user.getFbId() + ", appleId: " + user.getAppleId());

        if (StringUtil.isNullOrEmpty(user.getGgId()) && StringUtil.isNullOrEmpty(user.getFbId())
                && StringUtil.isNullOrEmpty(user.getAppleId())) {
            getMessageDes().add("định danh tài khoản không được để trống.");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateLoginApple(String userId, String email) {

        if (StringUtil.isNullOrEmpty(userId)) {
            getMessageDes().add("userId không được để trống.");
        }

        if (StringUtil.isNullOrEmpty(email)) {
            getMessageDes().add("email không được để trống.");
        } else if (!StringUtil.validateEmail(email)) {
            getMessageDes().add("email sai định dạng.");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateUpdateUser(User user) {
        if (user == null || user.getUuid() == null) {
            return "payLoad không hợp lệ";
        }
        if (!StringUtil.isNullOrEmpty(user.getMobile()) && !StringUtil.checkMobilePhoneNumberNew(user.getMobile())) {
            getMessageDes().add("mobile không đúng định dạng");
        }
        if (!StringUtil.isNullOrEmpty(user.getFullName()) && user.getFullName().length() > 255) {
            getMessageDes().add("fullName chiều dài tối đa 255");
        }
        if (!StringUtil.isNullOrEmpty(user.getBirthDay()) && !StringUtil.validateBirthDay(user.getBirthDay(), "dd/MM/yyyy")) {
            getMessageDes().add("birthDay định dạng kiểu dd/MM/yyyy");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateLogin(String userInfo, String password) throws ValidationException {

        if (StringUtil.isNullOrEmpty(userInfo)) {
            getMessageDes().add("Email/Mobile không được để trống");
        }
        boolean isValidUserInfo = false;
        if (StringUtil.validateEmail(userInfo)) {
            isValidUserInfo = true;
        }
        if (StringUtil.checkMobilePhoneNumberNew(userInfo)) {
            isValidUserInfo = true;
        }
        if (!isValidUserInfo) {
            getMessageDes().add("Email/Mobile không đúng định dạng");
        }

        if (StringUtil.isNullOrEmpty(password)) {
            getMessageDes().add("password không được để trống");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateUpdatePassword(AuthorizationResponseDTO dto, String curentPassword,
            String newPassword, String rePassword) throws ValidationException {

        if ((dto.getSignupType() == Constant.USER_SIGNUP_NORMAL || ((dto.getSignupType() == Constant.USER_SIGNUP_FACEBOOK
                || dto.getSignupType() == Constant.USER_SIGNUP_GOOGLE) && dto.getSetPassword() == 1))
                && StringUtil.isNullOrEmpty(curentPassword)) {
            getMessageDes().add("Password hiện tại không được bỏ trống");
        }

        if (StringUtil.isNullOrEmpty(newPassword) || StringUtil.isNullOrEmpty(rePassword)) {
            getMessageDes().add("Password mới không được để trống");
        } else if (!newPassword.equals(rePassword)) {
            getMessageDes().add("Password mới và Re Password không trùng nhau");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateChangeEmail(AuthorizationResponseDTO dto, String password,
            String newEmail) throws ValidationException {

        if (!StringUtil.validateEmail(newEmail)) {
            getMessageDes().add("Email mới không đúng định dạng");
        }

        if ((dto.getSignupType() == Constant.USER_SIGNUP_NORMAL || ((dto.getSignupType() == Constant.USER_SIGNUP_FACEBOOK
                || dto.getSignupType() == Constant.USER_SIGNUP_GOOGLE) && dto.getSetPassword() == 1))
                && StringUtil.isNullOrEmpty(password)) {
            getMessageDes().add("Password không được để trống");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateChangeMobile(AuthorizationResponseDTO dto, String password,
            String newMobile) throws ValidationException {

        if (!StringUtil.checkMobilePhoneNumberNew(newMobile)) {
            getMessageDes().add("Mobile mới không đúng định dạng");
        }

        if ((dto.getSignupType() == Constant.USER_SIGNUP_NORMAL || ((dto.getSignupType() == Constant.USER_SIGNUP_FACEBOOK
                || dto.getSignupType() == Constant.USER_SIGNUP_GOOGLE) && dto.getSetPassword() == 1))
                && StringUtil.isNullOrEmpty(password)) {
            getMessageDes().add("Password không được để trống");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateUpdateStatus(User user) {
        if (user == null || user.getUuid() == null) {
            return "payLoad không hợp lệ";
        }
        if (StringUtil.isNullOrEmpty(user.getUuid())) {
            getMessageDes().add("uuid không được để trống");
        }
        if (user.getStatus() == null || (user.getStatus() != 1 && user.getStatus() != -1)) {
            getMessageDes().add("status phải kiểu số và chỉ nhận 1 trong 2 giá trị (1,-1)");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateUpdateSocialMobile(AuthorizationResponseDTO dto, String mobile) throws ValidationException {

        if (!StringUtil.checkMobilePhoneNumberNew(mobile)) {
            getMessageDes().add("Mobile không đúng định dạng");
        }

        //if (dto.getSignupType() != Constant.USER_SIGNUP_FACEBOOK && dto.getSignupType() != Constant.USER_SIGNUP_GOOGLE) {
        //    getMessageDes().add("API chỉ sử dụng khi đăng nhập qua mạng xã hội");
        //}
        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateUpdateForgotPassword(String token, String newPassword, String rePassword) throws ValidationException {

        if (StringUtil.isNullOrEmpty(token)) {
            getMessageDes().add("token không được để trống");
        }

        if (StringUtil.isNullOrEmpty(newPassword) || StringUtil.isNullOrEmpty(rePassword)) {
            getMessageDes().add("Password mới không được để trống");
        } else if (!newPassword.equals(rePassword)) {
            getMessageDes().add("Password mới và Re Password không trùng nhau");
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }

    public String validateSendEmail(String emailTo, String title, String content,
            String sign) throws ValidationException {

        if (!StringUtil.validateEmail(emailTo)) {
            getMessageDes().add("emailTo không đúng định dạng");
        }

        if (StringUtil.isNullOrEmpty(title)) {
            getMessageDes().add("title không được để trống");
        }

        if (StringUtil.isNullOrEmpty(content)) {
            getMessageDes().add("content không được để trống");
        }

        if (StringUtil.isNullOrEmpty(sign)) {
            getMessageDes().add("sign không được để trống");
        } else {
            try {
                String plainText = emailTo + title + content;
                String coDau2KhongDau = VNCharacterUtils.unAccent(plainText);
                //String decryptText = RSAUtil.decrypt(sign, RSAUtil.privateKey);
                TravisAes travisAes = new TravisAes();
                String decryptText = travisAes.decrypt("Elcom2020@123456", sign);
                System.out.println("plainText: '" + plainText + "'");
                System.out.println("coDau2KhongDau: '" + coDau2KhongDau + "'");
                System.out.println("decryptText: '" + decryptText + "'");
//                if (!plainText.equals(decryptText)) {
//                    getMessageDes().add("Chữ ký (sign) không khớp");
//                }
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(UserValidation.class.getName()).log(Level.SEVERE, null, ex);
                getMessageDes().add("Lỗi giải mã sign");
            }
        }

        return !isValid() ? this.buildValidationMessage() : null;
    }
}
