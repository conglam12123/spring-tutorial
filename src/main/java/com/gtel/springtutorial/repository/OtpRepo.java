package com.gtel.springtutorial.repository;

import com.gtel.springtutorial.model.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepo extends JpaRepository<OtpEntity, String> {
}
