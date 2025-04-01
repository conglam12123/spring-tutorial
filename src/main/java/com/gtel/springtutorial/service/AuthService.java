package com.gtel.springtutorial.service;

import com.gtel.springtutorial.constant.Constant;
import com.gtel.springtutorial.constant.Message;
import com.gtel.springtutorial.constant.RegexConstant;
import com.gtel.springtutorial.exception.ApplicationException;
import com.gtel.springtutorial.model.entity.UserEntity;
import com.gtel.springtutorial.repository.UserRepo;
import com.gtel.springtutorial.utils.ERROR_CODE;
import com.gtel.springtutorial.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    final RedisService redisService;

    final RedisTemplate<String, Object> redisTemplate;

    final UserRepo userRepo;

    final OtpProducer otpProducer;

    public Object register(String phoneNum) {
        try {
            log.info("register: {}", phoneNum);
            String validPhoneNum = validatePhoneNumber(phoneNum);
            if (userRepo.existsByPhoneNumber(validPhoneNum))
                throw new ApplicationException(ERROR_CODE.PHONE_NUMBER_INVALID);




            String otp = generateOTP();
            redisService.save(getOtpKey(validPhoneNum), otp, 60L * 3);
            otpProducer.sendOtp(validPhoneNum, otp);
            return ResponseEntity.ok().body(Message.OK + otp);

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

    private String standardizePhoneNumber(String phoneNum) {
        if (!Pattern.matches(RegexConstant.VALID_PHONE_NUM, phoneNum)) {
            return phoneNum
                    .replaceFirst("^0", "84")
                    .replaceFirst("\\+", "");
        } else {
            return phoneNum;
        }
    }

    private static String generateOTP() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private String getOtpKey(String phoneNum) {
        return Constant.OTP_PREFIX + phoneNum;
    }

    private void validatePassword(String password) {
        if (!Pattern.matches(RegexConstant.PASSWORD_PATTERN, password))
            throw new ApplicationException(ERROR_CODE.PASSWORD_NOT_STRONG);
    }

    public static long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();

        return Duration.between(now, midnight).getSeconds();
    }

}
