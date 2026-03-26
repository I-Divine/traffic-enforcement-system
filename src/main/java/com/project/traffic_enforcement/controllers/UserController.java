package com.project.traffic_enforcement.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.traffic_enforcement.dto.UserProfileResponse;
import com.project.traffic_enforcement.services.UserProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('OWNERS','ADMIN','ROAD_OFFICERS','APPEAL_OFFICERS','REGISTRATION_OFFICERS')")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        return ResponseEntity.ok(userProfileService.getCurrentUserProfile());
    }
}
