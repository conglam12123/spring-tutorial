package com.gtel.springtutorial.repository;

import com.gtel.springtutorial.model.entity.AirportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AirportRepo2 extends JpaRepository<AirportEntity, String>,
                                    PagingAndSortingRepository<AirportEntity, String> {


}
