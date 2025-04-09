package com.gtel.springtutorial.redis.repository;

import com.gtel.springtutorial.redis.entities.UserRegisterRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRegisterRedisRepository extends CrudRepository<UserRegisterRedisEntity, String> {
}
