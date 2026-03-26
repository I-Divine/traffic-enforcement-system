package com.project.traffic_enforcement.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class PaymentRequest {
    private UUID violationId;
    private Float amount;
}
