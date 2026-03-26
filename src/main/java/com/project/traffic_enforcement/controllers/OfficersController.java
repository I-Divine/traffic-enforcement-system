package com.project.traffic_enforcement.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.OfficerProfileResponse;
import com.project.traffic_enforcement.dto.UserSummaryResponse;
import com.project.traffic_enforcement.dto.UserWithPendingAppealResponse;
import com.project.traffic_enforcement.dto.UserWithPendingPaymentsResponse;
import com.project.traffic_enforcement.dto.UserWithViolationsResponse;
import com.project.traffic_enforcement.services.OfficerDetailsService;
import com.project.traffic_enforcement.services.OfficerLookupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/officers")
@RequiredArgsConstructor
public class OfficersController {

    private final OfficerLookupService officerLookupService;
    private final OfficerDetailsService officerDetailsService;

    @GetMapping("/badge/{badgeNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','ROAD_OFFICERS','APPEAL_OFFICERS','REGISTRATION_OFFICERS')")
    public ResponseEntity<OfficerProfileResponse> getByBadgeNumber(@PathVariable String badgeNumber) {
        return ResponseEntity.ok(officerLookupService.findByBadgeNumber(badgeNumber));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','ROAD_OFFICERS','APPEAL_OFFICERS','REGISTRATION_OFFICERS')")
    public ResponseEntity<List<OfficerProfileResponse>> searchOfficers(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String assignmentArea) {
        if (department == null && assignmentArea == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide department or assignmentArea");
        }
        if (department != null && assignmentArea != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide only one search parameter");
        }
        if (department != null) {
            return ResponseEntity.ok(officerLookupService.findByDepartment(department));
        }
        return ResponseEntity.ok(officerLookupService.findByAssignmentArea(assignmentArea));
    }

    /**
     * Get all users in the system
     * Accessible by ROAD_OFFICERS, APPEAL_OFFICERS
     */
    @GetMapping("/users/all")
    @PreAuthorize("hasAnyRole('ROAD_OFFICERS','APPEAL_OFFICERS','ADMIN')")
    public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
        return ResponseEntity.ok(officerDetailsService.getAllUsers());
    }

    /**
     * Get all users with their violations
     * Accessible by ROAD_OFFICERS, APPEAL_OFFICERS
     */
    @GetMapping("/users/violations")
    @PreAuthorize("hasAnyRole('ROAD_OFFICERS','APPEAL_OFFICERS','ADMIN')")
    public ResponseEntity<List<UserWithViolationsResponse>> getAllUsersWithViolations() {
        return ResponseEntity.ok(officerDetailsService.getAllUsersWithViolations());
    }

    /**
     * Get all users with pending/unpaid violations
     * Accessible by ROAD_OFFICERS, APPEAL_OFFICERS
     */
    @GetMapping("/users/pending-payments")
    @PreAuthorize("hasAnyRole('ROAD_OFFICERS','APPEAL_OFFICERS','ADMIN')")
    public ResponseEntity<List<UserWithPendingPaymentsResponse>> getAllUsersWithPendingPayments() {
        return ResponseEntity.ok(officerDetailsService.getAllUsersWithPendingPayments());
    }

    /**
     * Get all users with unresolved violations
     * Accessible by ROAD_OFFICERS, APPEAL_OFFICERS
     */
    @GetMapping("/users/unresolved-violations")
    @PreAuthorize("hasAnyRole('ROAD_OFFICERS','APPEAL_OFFICERS','ADMIN')")
    public ResponseEntity<List<UserWithViolationsResponse>> getAllUsersWithUnresolvedViolations() {
        return ResponseEntity.ok(officerDetailsService.getAllUsersWithUnresolvedViolations());
    }

    /**
     * Get all users with pending appeals
     * Accessible by APPEAL_OFFICERS, ADMIN
     */
    @GetMapping("/users/pending-appeals")
    @PreAuthorize("hasAnyRole('APPEAL_OFFICERS','ADMIN')")
    public ResponseEntity<List<UserWithPendingAppealResponse>> getAllUsersWithPendingAppeals() {
        return ResponseEntity.ok(officerDetailsService.getAllUsersWithPendingAppeals());
    }

    /**
     * Get specific user with all violations
     * Accessible by ROAD_OFFICERS, APPEAL_OFFICERS
     */
    @GetMapping("/users/{userId}/violations")
    @PreAuthorize("hasAnyRole('ROAD_OFFICERS','APPEAL_OFFICERS','ADMIN')")
    public ResponseEntity<UserWithViolationsResponse> getUserWithViolations(@PathVariable UUID userId) {
        return ResponseEntity.ok(officerDetailsService.getUserWithViolations(userId));
    }

    /**
     * Get specific user with pending payments
     * Accessible by ROAD_OFFICERS, APPEAL_OFFICERS
     */
    @GetMapping("/users/{userId}/pending-payments")
    @PreAuthorize("hasAnyRole('ROAD_OFFICERS','APPEAL_OFFICERS','ADMIN')")
    public ResponseEntity<UserWithPendingPaymentsResponse> getUserWithPendingPayments(@PathVariable UUID userId) {
        return ResponseEntity.ok(officerDetailsService.getUserWithPendingPayments(userId));
    }

    /**
     * Get specific user with unresolved violations
     * Accessible by ROAD_OFFICERS, APPEAL_OFFICERS
     */
    @GetMapping("/users/{userId}/unresolved-violations")
    @PreAuthorize("hasAnyRole('ROAD_OFFICERS','APPEAL_OFFICERS','ADMIN')")
    public ResponseEntity<UserWithViolationsResponse> getUserWithUnresolvedViolations(@PathVariable UUID userId) {
        return ResponseEntity.ok(officerDetailsService.getUserWithUnresolvedViolations(userId));
    }

    /**
     * Get specific user with pending appeals
     * Accessible by APPEAL_OFFICERS, ADMIN
     */
    @GetMapping("/users/{userId}/pending-appeals")
    @PreAuthorize("hasAnyRole('APPEAL_OFFICERS','ADMIN')")
    public ResponseEntity<UserWithPendingAppealResponse> getUserWithPendingAppeals(@PathVariable UUID userId) {
        return ResponseEntity.ok(officerDetailsService.getUserWithPendingAppeals(userId));
    }
}
