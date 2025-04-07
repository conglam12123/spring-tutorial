package com.gtel.springtutorial.domains;

import com.gtel.springtutorial.exception.ApplicationException;
import com.gtel.springtutorial.redis.entities.OtpLimitEntity;
import com.gtel.springtutorial.redis.entities.UserRegisterRedisEntity;
import com.gtel.springtutorial.redis.repository.OtpLimitRepository;
import com.gtel.springtutorial.redis.repository.UserRegisterRedisRepository;
import com.gtel.springtutorial.service.OtpProducer;
import com.gtel.springtutorial.utils.ERROR_CODE;
import com.gtel.springtutorial.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import javax.swing.text.html.parser.Entity;
import java.util.Objects;
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
        String transactionId = userRegisterRedisRepository.save(userRegisterRedisEntity).getTransactionId();

        //update số lần gửi trong ngày
        otpLimit.setDailyOtpCounter(otpLimit.getDailyOtpCounter() + 1);
        otpLimit.setTransactionId(transactionId);
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

    public UserRegisterRedisEntity checkOtpWhenUserSubmit (String phoneNumber, String otp) {
        log.info("[checkOtpWhenUserSubmit]: user confirm otp {} for phone number {}", otp, phoneNumber);
        // Kiểm tra số lần thử
        Optional<OtpLimitEntity> otpLimitEntity = otpLimitRepository.findById(phoneNumber);

        if(otpLimitEntity.isEmpty()) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "No available OTP for phone number " + phoneNumber );
        }
        OtpLimitEntity entity = otpLimitEntity.get();

        // Kiểm tra nội dung OTP đúng chưa
        UserRegisterRedisEntity userEntity = userRegisterRedisRepository.findById(entity.getTransactionId()).orElse(null);
        if(Objects.isNull(userEntity)) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "No transaction available for phone Number " + phoneNumber);
        }
        if(otp.equals(userEntity.getOtp())) {
            userEntity.setPassword(EncryptionUtils.bcryptEncode(userEntity.getPassword()));
            return userEntity;
        }
        else  {
            log.warn("[checkOtpWhenUserSubmit]: failed: user sent wrong OTP for transaction: {}", entity.getTransactionId());
            userEntity.setOtpFail(userEntity.getOtpFail() + 1);
            userRegisterRedisRepository.save(userEntity);
            return null;
        }

    }

    public static String generateOTP() {
        Random random = new Random();
        int otp = random.nextInt(1000000);
        return String.format("%06d", otp);
    }

}
