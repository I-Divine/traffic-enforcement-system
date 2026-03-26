package com.project.traffic_enforcement.dto;

import lombok.Data;

@Data
public class OwnerDetailsRequest {
    private String address;
    private String city;
    private String state;
    private String driversLicenseNumber;
}
