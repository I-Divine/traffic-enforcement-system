package com.project.traffic_enforcement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class VehicleResponse {
    private UUID vehicleId;
    private String plateNumber;
    private String make;
    private String model;
    private String year;
    private String color;
    private LocalDateTime registrationDate;
    private LocalDateTime registrationExpiry;


}
