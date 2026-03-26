package com.project.traffic_enforcement.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AppealRequest {
    private UUID violationId;
    private String description;
    private String evidenceUrl;
}
