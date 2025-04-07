package com.gtel.springtutorial.redis.entities;

import com.gtel.springtutorial.utils.EncryptionUtils;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.UUID;


@Data
@RedisHash("user_register")
public class UserRegisterRedisEntity implements Serializable {
    @Id
    private String transactionId;

    private String otp;

    private long otpExpiredTime;

    private long otpResendTime;

    private int otpResendCount;

    private String phoneNumber;

    private String password;

    private int otpFail;

    @TimeToLive
    private long ttl;

    public UserRegisterRedisEntity(String otp, String phoneNumber, String password) {
        this.otp = otp;
        this.phoneNumber = phoneNumber;
//        this.password = EncryptionUtils.bcryptEncode(password);
        this.password = password;

        this.transactionId = UUID.randomUUID().toString();
        this.ttl = 900;
        this.otpFail = 0;

        this.setOtpExpiredTime(System.currentTimeMillis() / 1000 + 300);
        this.setOtpResendTime(System.currentTimeMillis() / 1000 + 60);
    }
}
