package com.gtel.springtutorial.redis.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("password_change")
public class PasswordChangeLimitEntity {

    Integer failedAttempt;

    @Id
    String phoneNumber;

    int expireTime;

    int cooldownTime;

    public PasswordChangeLimitEntity(String phoneNumber) {
        this.failedAttempt = 0;
        this.phoneNumber = phoneNumber;
        this.cooldownTime = 0;
    }
}
