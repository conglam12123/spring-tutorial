package com.gtel.springtutorial.repository;

import com.gtel.springtutorial.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, String> {

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
}
