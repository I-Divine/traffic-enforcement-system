package com.project.traffic_enforcement.dto;

import lombok.Data;

@Data
public class PaystackInitializeResponse {
    private String authorizationUrl;
    private String accessCode;
    private String reference;
    private String message;
}
