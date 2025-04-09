package com.gtel.springtutorial.redis.repository;

import com.gtel.springtutorial.redis.entities.PasswordChangeLimitEntity;
import org.springframework.data.repository.CrudRepository;

public interface PasswordChangeLimitRepository extends CrudRepository<PasswordChangeLimitEntity, String> {
}
