package com.project.traffic_enforcement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaystackInitializeApiResponse {
    private boolean status;
    private String message;
    private PaystackInitializeApiData data;

    @Data
    public static class PaystackInitializeApiData {
        @JsonProperty("authorization_url")
        private String authorizationUrl;

        @JsonProperty("access_code")
        private String accessCode;

        private String reference;
    }
}
