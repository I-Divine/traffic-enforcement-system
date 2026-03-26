package com.project.traffic_enforcement.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserWithPendingAppealResponse {
    private UserSummaryResponse user;
    private List<AppealDetailsResponse> pendingAppeals;
    private Integer totalPendingAppeals;
}
