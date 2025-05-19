package com.gtel.springtutorial.redis.repository;

import com.gtel.springtutorial.redis.entities.LoginRedisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRedisRepository extends JpaRepository<LoginRedisEntity, String> {
}
