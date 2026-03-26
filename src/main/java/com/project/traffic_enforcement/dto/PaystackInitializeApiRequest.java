package com.project.traffic_enforcement.dto;

import lombok.Data;

@Data
public class PaystackInitializeApiRequest {
    private String email;
    private Long amount;
    private String reference;
}
