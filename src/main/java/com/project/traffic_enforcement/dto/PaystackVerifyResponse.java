package com.project.traffic_enforcement.dto;

import lombok.Data;

@Data
public class PaystackVerifyResponse {
    private boolean verified;
    private String paymentStatus;
    private String reference;
    private Long amount;
    private String currency;
    private String gatewayResponse;
    private String paidAt;
    private String message;
}
