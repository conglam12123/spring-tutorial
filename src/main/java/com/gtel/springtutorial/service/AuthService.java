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
            String validPhoneNum = standardizePhoneNumber(phoneNum);
            String storedOtp = redisService.get(getOtpKey(validPhoneNum));
            if (!StringUtils.hasText(storedOtp)) {
                throw new ApplicationException(ERROR_CODE.OTP_EXPIRED);
            }
            if (otp.equals(storedOtp)) {
                return ResponseEntity.ok().body(Message.ACTIVATION_SUCCESS);
            } else {
                throw new ApplicationException(ERROR_CODE.INCORRECT_OTP);
            }
        } catch (ApplicationException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    public Object updatePassword(String phoneNumber, String password) {
        try {
            log.info("UpdatePassword for {}", phoneNumber);

            String validPhoneNum = standardizePhoneNumber(phoneNumber);

            validatePassword(password);
            userRepo.save(new UserEntity(validPhoneNum, EncryptionUtils.sha256(password)));
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
        if (!Pattern.matches(RegexConstant.PASSWORD_PATTERN, password))
            throw new ApplicationException(ERROR_CODE.PASSWORD_NOT_STRONG);
    }
}
