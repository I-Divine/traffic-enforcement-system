package com.project.traffic_enforcement.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
public class Vehicles {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
private UUID vehicleId;

@Column(unique = true, nullable = false)
private String plateNumber;
@ManyToOne
@JoinColumn(name = "owner_id", nullable = false)
private Owners owner;
private String make;
private String model;
private String year;
private String color;
private LocalDateTime registrationDate;
private LocalDateTime registrationExpiry;
}
