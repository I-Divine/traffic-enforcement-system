
package com.project.traffic_enforcement.dto;

import java.util.List;
import java.util.UUID;

import com.project.traffic_enforcement.models.enums.Roles;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OwnerLookupResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Roles role;
    private String profilePictureUrl;
    private OwnerDetailsResponse ownerDetails;
    private List<VehicleResponse> vehicles;
}