package com.gtel.springtutorial.model.response;

import com.gtel.springtutorial.redis.entities.UserRegisterRedisEntity;
import lombok.Data;

@Data
public class RegisterResponse {
    private String transactionId;

    private long otpExpiredTime;

    private long resendOtpTime;

    public RegisterResponse(UserRegisterRedisEntity entity){
        this.transactionId = entity.getTransactionId();
        this.otpExpiredTime = entity.getOtpExpiredTime() - System.currentTimeMillis()/1000;
        this.resendOtpTime = entity.getOtpResendTime() - System.currentTimeMillis()/1000;
    }
}
