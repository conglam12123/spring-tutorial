package com.gtel.springtutorial.redis.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("login")
public class LoginRedisEntity {
    @Id
    private String token;

    @Indexed
    private String username;

    @TimeToLive
    private long ttl;
}
