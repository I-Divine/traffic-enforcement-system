package com.project.traffic_enforcement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaystackVerifyApiResponse {
    private boolean status;
    private String message;
    private PaystackVerifyApiData data;

    @Data
    public static class PaystackVerifyApiData {
        private Long amount;
        private String currency;
        private String status;
        private String reference;

        @JsonProperty("gateway_response")
        private String gatewayResponse;

        @JsonProperty("paid_at")
        private String paidAt;

        @JsonProperty("transaction_date")
        private String transactionDate;
    }
}
