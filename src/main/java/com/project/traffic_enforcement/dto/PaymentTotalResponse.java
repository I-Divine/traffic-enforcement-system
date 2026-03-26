package com.project.traffic_enforcement.dto;

import java.time.LocalDateTime;

import com.project.traffic_enforcement.models.enums.PaymentTotalPeriod;

import lombok.Data;

@Data
public class PaymentTotalResponse {
    private PaymentTotalPeriod period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalAmount;
}
