package com.gtel.springtutorial.model.entity;

import com.gtel.springtutorial.model.request.AirportRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "airports")
public class AirportEntity extends BaseEntity {
    @Id
    @Column(name = "iata")
    private String iata;

    @Column(name = "name")
    private String name;

    @Column(name = "airportGroupCode")
    private String airportGroupCode;

    @Column(name = "language")
    private String language;

    @Column(name = "priority")
    private Integer priority;

    public AirportEntity(AirportRequest request) {
        this.iata = request.getIata();
        this.name =request.getName();
        this.airportGroupCode = request.getAirportGroupCode();
        this.language = request.getLanguage();
        this.priority = request.getPriority();
    }

    public AirportEntity() {

    }
}
