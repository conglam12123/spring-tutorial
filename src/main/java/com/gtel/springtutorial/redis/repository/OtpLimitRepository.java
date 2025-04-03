package com.gtel.springtutorial.redis.repository;

import com.gtel.springtutorial.redis.entities.OtpLimitEntity;
import org.springframework.data.repository.CrudRepository;

public interface OtpLimitRepository  extends CrudRepository<OtpLimitEntity, String> {
}
