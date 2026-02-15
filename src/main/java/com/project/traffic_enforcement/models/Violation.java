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
private UUID vehicleId;
@ManyToOne
@JoinColumn(name = "officer_id", nullable = false)
private Officers officer;
private String plateNumber;
@ManyToOne
@JoinColumn(name = "owner_id", nullable = false)
private Owners owner;
private LocalDateTime violationDate;
private String gps_coordinates;
private Float fineAmount;
private String state;
private String lga;
}
