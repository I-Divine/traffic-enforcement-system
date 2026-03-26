package com.project.traffic_enforcement.dto;

import java.time.LocalDateTime;

import com.project.traffic_enforcement.models.enums.ViolationStatus;
import com.project.traffic_enforcement.models.enums.ViolationType;

import lombok.Data;

@Data
public class ViolationRequest {
    private String plateNumber;
    private LocalDateTime violationDate;
    private String gpsCoordinates;
    private Float fineAmount;
    private ViolationType violationType;
    private String description;
    private ViolationStatus status;
    private String state;
    private String lga;
}
