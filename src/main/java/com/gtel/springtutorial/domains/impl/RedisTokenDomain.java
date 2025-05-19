package com.gtel.springtutorial.domains.impl;

import com.gtel.springtutorial.domains.TokenDomain;
import com.gtel.springtutorial.redis.entities.LoginRedisEntity;
import com.gtel.springtutorial.redis.repository.LoginRedisRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class RedisTokenDomain implements TokenDomain {
    private final LoginRedisRepository loginRedisRepository;
    @Override
    public String genToken(String userName) {
        String token = UUID.randomUUID().toString();

        LoginRedisEntity loginRedisEntity = new LoginRedisEntity();
        loginRedisEntity.setToken(token);
        loginRedisEntity.setUsername(userName);
        loginRedisEntity.setTtl(3600);

        loginRedisRepository.save(loginRedisEntity);
        return  token;
    }

    @Override
    public String validateToken(String token) {
        return null;
    }

    @Override
    public void extendTTL(String token) {
//        Optional<LoginRedisEntity>
    }
}
