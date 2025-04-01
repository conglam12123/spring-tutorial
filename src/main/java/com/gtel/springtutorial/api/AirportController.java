package com.gtel.springtutorial.api;

import com.gtel.springtutorial.model.request.AirportRequest;
import com.gtel.springtutorial.model.response.AirportResponse;
import com.gtel.springtutorial.service.AirportService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/airports")
public class AirportController {

    @Autowired
    private AirportService airportService;

    @GetMapping
    public List<AirportResponse> getAirports(@RequestParam Map<String, String > param ) {
        Integer version = Integer.valueOf(param.get("version"));
        switch (version) {
            case 1:
                return airportService.getAirportsV1(param);
            case 2:
                return airportService.getAirportsV2(param);
            default:
                return null;
        }
    }

    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity countAirports() {
        int count = airportService.countAirports();

        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(count)).build();
    }


    @GetMapping("/{iata}")
    public ResponseEntity<?> getAirport(@PathVariable String iata) {
        return airportService.getAirport(iata);
    }

    @PostMapping
    public void createAirport(@RequestBody AirportRequest airportRequest) {
        airportService.createAirport(airportRequest);
    }

    @PutMapping("/{iata}")
    public void updateAirport(@PathVariable String iata, @RequestBody AirportRequest airportRequest) {
        airportService.updateAirports(iata, airportRequest);
    }

    @PatchMapping("/{iata}")
    public void updatePatchAirport(@PathVariable String iata, @RequestBody AirportRequest airportRequest) {
        airportService.updatePathAirports(iata, airportRequest);
    }


    @DeleteMapping("/{iata}")
    public void deleteAirport(@PathVariable String iata) {
        airportService.deleteAirport(iata);
    }
}
