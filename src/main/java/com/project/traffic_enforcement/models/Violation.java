package com.project.traffic_enforcement.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID violationId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicles vehicle;

    @ManyToOne
    @JoinColumn(name = "officer_id", nullable = false)
    private Officers officer;

    private String plateNumber;
    private LocalDateTime violationDate;
    private String gpsCoordinates;
    private Float fineAmount;
    private String status;
    private String state;
    private String lga;
}
