package com.project.traffic_enforcement.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserWithViolationsResponse {
    private UserSummaryResponse user;
    private List<ViolationDetailsResponse> violations;
    private Integer totalViolations;
    private Integer unresolvedViolations;
}
