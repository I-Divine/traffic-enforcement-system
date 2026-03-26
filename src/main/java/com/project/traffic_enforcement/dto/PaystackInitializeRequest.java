package com.project.traffic_enforcement.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class PaystackInitializeRequest {
    private UUID violationId;
}
