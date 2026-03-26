package com.project.traffic_enforcement.dto;

import java.util.UUID;

import com.project.traffic_enforcement.models.enums.AppealStatus;

import lombok.Data;

@Data
public class AppealResponse {
    private UUID appealId;
    private UUID violationId;
    private String description;
    private String evidenceUrl;
    private AppealStatus status;
}
