package com.gtel.springtutorial.service;

import com.gtel.springtutorial.exception.ApplicationException;
import com.gtel.springtutorial.model.entity.AirportEntity;
import com.gtel.springtutorial.model.request.AirportRequest;
import com.gtel.springtutorial.model.response.AirportResponse;
import com.gtel.springtutorial.repository.AirportHibernateRepository;
import com.gtel.springtutorial.repository.AirportRepo;
import com.gtel.springtutorial.utils.ERROR_CODE;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AirportService {
    @Autowired
    AirportRepo repo;
    @Autowired
    AirportHibernateRepository hibernateRepo;

    public List<AirportResponse> getAirportsV1(Map<String, String> params) {
        Integer page = Integer.valueOf(params.get("page"));
        Integer size = Integer.valueOf(params.get("size"));
        Pageable pageable = PageRequest.of(page - 1, size);
        List<AirportResponse> responses = repo.findAll(pageable).getContent().stream().map(AirportResponse::new).toList();
        return responses;

    }

    public List<AirportResponse> getAirportsV2(Map<String, String> param) {
        return hibernateRepo.getAirports(Integer.parseInt(param.get("page")), Integer.parseInt(param.get("size"))).stream().map(arr -> (
                new AirportResponse(
                        (String) arr[0],
                        (String) arr[1],
                        (String) arr[2],
                        (String) arr[3],
                        (int) arr[4]
                ))).toList();

    }

    public int countAirports() {
        return (int) repo.count();
    }

    public ResponseEntity<Object> getAirport(String iata) {
        try {

            AirportEntity entity = repo.findByIata(iata).orElseThrow(EntityNotFoundException::new);
            return ResponseEntity.ok(new AirportResponse(entity));

        } catch (
                EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("Không tìm thấy Sân bay tương ứng!");
        }
    }

    public void createAirport(AirportRequest airportRequest) {
        log.info("Created airport with iata {}", airportRequest.getIata());
        validateCreateAirport(airportRequest);
        AirportEntity airport = new AirportEntity(airportRequest);
        repo.save(airport);
        log.info("Created airport with iata {} success", airportRequest.getIata());

    }

    @Transactional
    public void deleteAirport(String iata) {
        repo.deleteByIata(iata);
    }

    public void updateAirports(String iata, AirportRequest airportRequest) {
        if (repo.existsByIata(iata)) {
            AirportEntity entity = new AirportEntity(airportRequest);
            repo.save(entity);
        } else throw new EntityNotFoundException();

    }

    public void updatePathAirports(String iata, AirportRequest airportRequest) {
        AirportEntity entity = repo.findByIata(iata).orElseThrow(EntityNotFoundException::new);

        if (Objects.nonNull(airportRequest.getAirportGroupCode()))
            entity.setAirportGroupCode(airportRequest.getAirportGroupCode());
        if (Objects.nonNull(airportRequest.getLanguage())) entity.setLanguage(airportRequest.getLanguage());
        if (Objects.nonNull(airportRequest.getName())) entity.setName(airportRequest.getName());
        if (Objects.nonNull(airportRequest.getPriority())) entity.setPriority(airportRequest.getPriority());

        repo.save(entity);
    }

    public void validateCreateAirport(AirportRequest request) {
        if (StringUtils.isBlank(request.getIata())) throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "IATA must not be blank");
        if (StringUtils.isBlank(request.getName())) throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Airport name must not be blank");

        if(repo.existsByIata(request.getIata())) throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "An airport with iata " + request.getIata() + "is already exist on Database!!");
    }
}
