package com.gtel.springtutorial.service;

import com.gtel.springtutorial.constant.Constant;
import com.gtel.springtutorial.constant.Message;
import com.gtel.springtutorial.constant.RegexConstant;
import com.gtel.springtutorial.domains.OtpDomain;
import com.gtel.springtutorial.exception.ApplicationException;
import com.gtel.springtutorial.model.entity.UserEntity;
import com.gtel.springtutorial.model.request.RegisterRequest;
import com.gtel.springtutorial.model.response.RegisterResponse;
import com.gtel.springtutorial.redis.entities.UserRegisterRedisEntity;
import com.gtel.springtutorial.repository.UserRepo;
import com.gtel.springtutorial.utils.ERROR_CODE;
import com.gtel.springtutorial.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.gtel.springtutorial.utils.AppUtils.standardizePhoneNumber;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    final RedisService redisService;

    final RedisTemplate<String, Object> redisTemplate;

    final UserRepo userRepo;

    final OtpProducer otpProducer;

    final OtpDomain otpDomain;

    public Object register(RegisterRequest request) {
        try {
            log.info("register: {}", request.getPhoneNumber());
            // validate phoneNum
            String validPhoneNum = validatePhoneNumber(request.getPhoneNumber());
            if (userRepo.existsByPhoneNumber(validPhoneNum))
                throw new ApplicationException(ERROR_CODE.PHONE_NUMBER_INVALID);
            validatePassword(request.getPassword());

            UserRegisterRedisEntity userRegisterRedisEntity = otpDomain.genOtpWhenUserRegister(validPhoneNum, request.getPassword());

            return ResponseEntity.ok().body(new RegisterResponse(userRegisterRedisEntity));

        } catch (ApplicationException e) {
            log.error("register failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Object activateAccountWithOtp(String phoneNum, String otp) {
        try {
            log.info("activateAccountWithOtp: {}", phoneNum);
            // validate
            String validPhoneNum = validatePhoneNumber(phoneNum);

            UserRegisterRedisEntity userRegisterRedisEntity = otpDomain.checkOtpWhenUserSubmit(validPhoneNum, otp);
            if (Objects.nonNull(userRegisterRedisEntity)) {
                userRepo.save(new UserEntity(userRegisterRedisEntity));

                return ResponseEntity.ok().body("Register success!. You now can login with the password you submitted before.");

            } else {
                throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "No transaction for " + phoneNum + " found, please register first!");
            }
        } catch (ApplicationException e) {
            e.printStackTrace();
            log.error("[activateAccountWithOtp] failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    public Object updatePassword(String phoneNumber, String oldPass, String newPass) {
        try {
            log.info("[UpdatePassword] for {}", phoneNumber);

            String validPhoneNum = standardizePhoneNumber(phoneNumber);

            validatePassword(newPass);

            UserEntity userEntity = userRepo.findByPhoneNumber(validPhoneNum).orElse(null);

            if (Objects.isNull(userEntity)) {
                throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "No user with phone number " + phoneNumber + " found!");
            }

            if (EncryptionUtils.bcryptPasswordCheck(oldPass, userEntity.getPassword())) {
                userEntity.setPassword(EncryptionUtils.bcryptEncode(newPass));
                userRepo.save(userEntity);

            }
            else  throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, Message.OLD_PASSWORD_NOT_MATCH);


            return ResponseEntity.ok().body(Message.PASSWORD_UPDATE_SUCCESS);
        } catch (ApplicationException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    private String validatePhoneNumber(String phoneNum) {
        if (!Pattern.matches(RegexConstant.IS_PHONE_NUM, phoneNum))
            throw new ApplicationException(ERROR_CODE.PHONE_NUMBER_INVALID);

        return standardizePhoneNumber(phoneNum);
    }

    private String getOtpKey(String phoneNum) {
        return Constant.OTP_PREFIX + phoneNum;
    }

    private void validatePassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Password required!");
        }
        if (!Pattern.matches(RegexConstant.PASSWORD_PATTERN, password))
            throw new ApplicationException(ERROR_CODE.PASSWORD_NOT_STRONG);
    }
}
