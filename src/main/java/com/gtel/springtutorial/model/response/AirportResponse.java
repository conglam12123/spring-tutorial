package com.gtel.springtutorial.model.response;

import com.gtel.springtutorial.model.entity.AirportEntity;
import lombok.Data;

@Data
public class AirportResponse {
    private String iata;
    private String name;
    private String airportGroupCode;
    private String language;
    private Integer priority;

    public AirportResponse(AirportEntity entity) {
        this.iata = entity.getIata();
        this.name = entity.getName();
        this.airportGroupCode = entity.getAirportGroupCode();
        this.language = entity.getLanguage();
        this.priority = entity.getPriority();
    }

    public AirportResponse(String iata, String name, String airportGroupCode, String language, int priority) {
        this.iata = iata;
        this.name = name;
        this.airportGroupCode = airportGroupCode;
        this.language = language;
        this.priority = priority;
    }
}
