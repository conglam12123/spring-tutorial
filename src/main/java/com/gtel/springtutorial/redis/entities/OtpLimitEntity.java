package com.gtel.springtutorial.redis.entities;

import com.gtel.springtutorial.utils.AppUtils;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("otp")
@Data
public class OtpLimitEntity {
    @Id
    private String phoneNumber;

    private int dailyOtpCounter = 0;
    @TimeToLive
    private long ttl;

    String transactionId;

    public OtpLimitEntity(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.ttl = AppUtils.getSecondsUntilMidnight();
        this.dailyOtpCounter = 0;
    }
}
