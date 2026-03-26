package com.project.traffic_enforcement.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserWithPendingPaymentsResponse {
    private UserSummaryResponse user;
    private List<ViolationDetailsResponse> unpaidViolations;
    private Integer totalUnpaidCount;
    private Float totalUnpaidAmount;
}
