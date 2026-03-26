package com.project.traffic_enforcement.dto;

import java.util.UUID;

import com.project.traffic_enforcement.models.enums.AppealStatus;

import lombok.Data;

@Data
public class AppealDetailsResponse {
    private UUID appealId;
    private UUID violationId;
    private String violationDescription;
    private Float fineAmount;
    private String description;
    private String evidenceUrl;
    private AppealStatus status;
    private UUID ownerUserId;
}
