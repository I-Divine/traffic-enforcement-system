package com.project.traffic_enforcement.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OwnerDetailsResponse {
    private String address;
    private String city;
    private String state;
    private String driversLicenseNumber;
}
