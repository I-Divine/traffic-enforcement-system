package com.project.traffic_enforcement.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VehicleUpdateRequest {
    private String plateNumber;
    private String make;
    private String model;
    private String year;
    private String color;
    private LocalDateTime registrationDate;
    private LocalDateTime registrationExpiry;
}
