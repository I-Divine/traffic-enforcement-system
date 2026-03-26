package com.project.traffic_enforcement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class PaymentResponse {
    private UUID paymentId;
    private String referenceId;
    private UUID violationId;
    private String userEmail;
    private String fullName;
    private Float amount;
    private LocalDateTime createdAt;
}
