package com.gtel.springtutorial.repository;

import com.gtel.springtutorial.model.entity.AirportEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirportRepo extends JpaRepository<AirportEntity, String> {

    void deleteByIata(String iata);

    Optional<AirportEntity> findByIata(String iata);

    boolean existsByIata(String iata);

    Page<AirportEntity> findAll (Pageable pageable);

}
