package com.gtel.springtutorial.domains;

import com.gtel.springtutorial.exception.ApplicationException;
import com.gtel.springtutorial.redis.entities.OtpLimitEntity;
import com.gtel.springtutorial.redis.entities.UserRegisterRedisEntity;
import com.gtel.springtutorial.redis.repository.OtpLimitRepository;
import com.gtel.springtutorial.redis.repository.UserRegisterRedisRepository;
import com.gtel.springtutorial.service.OtpProducer;
import com.gtel.springtutorial.utils.ERROR_CODE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpDomain {

    private final OtpLimitRepository otpLimitRepository;

    private final UserRegisterRedisRepository userRegisterRedisRepository;

    private final OtpProducer producer;

    public UserRegisterRedisEntity genOtpWhenUserRegister(String phoneNumber, String password) {
        log.info("[genOtpWhenUserRegister] START with phone {}", phoneNumber);
        //validate limit
        OtpLimitEntity otpLimit = validateLimitOtpByPhoneNumber(phoneNumber);
        String otp = generateOTP();

        //Lưu transaction info
        UserRegisterRedisEntity userRegisterRedisEntity = new UserRegisterRedisEntity(otp, phoneNumber, password);
        userRegisterRedisRepository.save(userRegisterRedisEntity);

        //update số lần gửi trong ngày
        otpLimit.setDailyOtpCounter(otpLimit.getDailyOtpCounter() + 1);
        otpLimitRepository.save(otpLimit);

        //send to queue
        producer.sendOtp(phoneNumber, otp);

        return  userRegisterRedisEntity;
    }

    public OtpLimitEntity validateLimitOtpByPhoneNumber(String phoneNumber) {

        Optional<OtpLimitEntity> otpLimitEntity = otpLimitRepository.findById(phoneNumber);

        if(otpLimitEntity.isEmpty()) {
            return new OtpLimitEntity(phoneNumber);
        }

        OtpLimitEntity otpLimit  = otpLimitEntity.get();

        if(otpLimit.getDailyOtpCounter() >= 5) {
            log.warn("[validateLimitOtpByPhoneNumber] request fail : otp limit reached with phone {}", phoneNumber);
            throw  new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP Limit reached!!");
        }

        return otpLimit;
    }

    public static String generateOTP() {
        Random random = new Random();
        int otp = random.nextInt(1000000);
        return String.format("%06d", otp);
    }

}
