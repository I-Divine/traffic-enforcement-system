package com.project.traffic_enforcement.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OwnerUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private String address;
    private String city;
    private String state;
    private String driversLicenseNumber;
}
