package com.project.traffic_enforcement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.traffic_enforcement.models.enums.ViolationStatus;
import com.project.traffic_enforcement.models.enums.ViolationType;

import lombok.Data;

@Data
public class ViolationDetailsResponse {
    private UUID violationId;
    private String plateNumber;
    private String vehicleInfo;  // "Make Model Year"
    private LocalDateTime violationDate;
    private Float fineAmount;
    private ViolationType violationType;
    private String description;
    private ViolationStatus status;
    private String state;
    private String lga;
    private Boolean isPaid;  // Check if violation has payment
    private UUID ownerUserId;
}
