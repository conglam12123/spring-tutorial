package com.gtel.springtutorial.service;

import com.gtel.springtutorial.constant.Message;
import com.gtel.springtutorial.constant.RegexConstant;
import com.gtel.springtutorial.domains.OtpDomain;
import com.gtel.springtutorial.exception.ApplicationException;
import com.gtel.springtutorial.model.entity.UserEntity;
import com.gtel.springtutorial.model.request.RegisterRequest;
import com.gtel.springtutorial.model.response.RegisterResponse;
import com.gtel.springtutorial.redis.entities.PasswordChangeLimitEntity;
import com.gtel.springtutorial.redis.entities.UserRegisterRedisEntity;
import com.gtel.springtutorial.redis.repository.PasswordChangeLimitRepository;
import com.gtel.springtutorial.repository.UserRepo;
import com.gtel.springtutorial.utils.ERROR_CODE;
import com.gtel.springtutorial.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
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

    final PasswordChangeLimitRepository passwordChangeLimitRepository;

    public RegisterResponse register(RegisterRequest request) {

        log.info("[register]: {}", request.getPhoneNumber());
        // validate phoneNum
        String validPhoneNum = validatePhoneNumber(request.getPhoneNumber());
        if (userRepo.existsByPhoneNumber(validPhoneNum))
            throw new ApplicationException(ERROR_CODE.PHONE_NUMBER_INVALID);
        validatePassword(request.getPassword());

        UserRegisterRedisEntity userRegisterRedisEntity = otpDomain.genOtpWhenUserRegister(validPhoneNum, request.getPassword());

        //send OTP to queue
        otpProducer.sendOtp(validPhoneNum, userRegisterRedisEntity.getOtp());

        return new RegisterResponse(userRegisterRedisEntity);
    }

    public ResponseEntity<String> activateAccountWithOtp(String transactionId, String otp) {
        log.info("[activateAccountWithOtp]: activate account for transaction {}", transactionId);

        UserRegisterRedisEntity userRegisterRedisEntity = otpDomain.checkOtpWhenUserSubmit(transactionId, otp);

        userRepo.save(new UserEntity(userRegisterRedisEntity));

        return ResponseEntity.ok().body("Register success!. You now can login with the password you submitted before.");
    }

    public ResponseEntity<String> updatePassword(String phoneNumber, String oldPass, String newPass) {

        log.info("[UpdatePassword] for {} START", phoneNumber);

        String validPhoneNum = standardizePhoneNumber(phoneNumber);

        validatePassword(newPass);
        // dùng orElseGet mà không dùng Optional do passwordChangeLimitEntity cần phải có giá trị
        PasswordChangeLimitEntity passwordChangeLimitEntity = passwordChangeLimitRepository.findById(validPhoneNum).orElseGet(() -> new PasswordChangeLimitEntity(validPhoneNum));

        int cooldownRemaining = (int) (passwordChangeLimitEntity.getCooldownTime() - System.currentTimeMillis() / 1000);

        if (passwordChangeLimitEntity.getFailedAttempt() >= 5 && cooldownRemaining > 0)
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, String.format("Too many incorrect password attempts. Please wait %d minutes before trying again", (cooldownRemaining + 59) / 60));


        Optional<UserEntity> userEntityOpt = userRepo.findByPhoneNumber(validPhoneNum);

        if (userEntityOpt.isEmpty()) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "No user with phone number " + phoneNumber + " found!");
        }

        UserEntity userEntity = userEntityOpt.get();

        if (!EncryptionUtils.bcryptPasswordCheck(oldPass, userEntity.getPassword())) {
            //đếm số lần fail, nếu fail 5 lần thì đặt cooldown
            passwordChangeLimitEntity.setFailedAttempt(passwordChangeLimitEntity.getFailedAttempt() + 1);

            if (passwordChangeLimitEntity.getFailedAttempt() == 5) {
                passwordChangeLimitEntity.setCooldownTime(Math.toIntExact(System.currentTimeMillis() / 1000 + 30 * 60));
            }

            passwordChangeLimitRepository.save(passwordChangeLimitEntity);

            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, Message.OLD_PASSWORD_NOT_MATCH);

        } else {
            passwordChangeLimitRepository.deleteById(validPhoneNum);
            userEntity.setPassword(EncryptionUtils.bcryptEncode(newPass));
            userRepo.save(userEntity);
        }


        log.info("[UpdatePassword] for {} SUCCESS", phoneNumber);

        return ResponseEntity.ok().body(Message.PASSWORD_UPDATE_SUCCESS);

    }

    private String validatePhoneNumber(String phoneNum) {
        if (!Pattern.matches(RegexConstant.IS_PHONE_NUM, phoneNum))
            throw new ApplicationException(ERROR_CODE.PHONE_NUMBER_INVALID);

        return standardizePhoneNumber(phoneNum);
    }

    private void validatePassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Password required!");
        }
        if (!Pattern.matches(RegexConstant.PASSWORD_PATTERN, password))
            throw new ApplicationException(ERROR_CODE.PASSWORD_NOT_STRONG);
    }
}
