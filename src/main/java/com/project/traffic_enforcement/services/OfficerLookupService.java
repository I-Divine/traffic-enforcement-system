package com.project.traffic_enforcement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.OfficerDetailsResponse;
import com.project.traffic_enforcement.dto.OfficerProfileResponse;
import com.project.traffic_enforcement.models.Officers;
import com.project.traffic_enforcement.repository.OfficersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfficerLookupService {

    private final OfficersRepository officersRepository;

    public OfficerProfileResponse findByBadgeNumber(String badgeNumber) {
        Officers officer = officersRepository.findByBadgeNumber(badgeNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Officer not found"));
        System.out.println(officer.getUsers().getFirstName());
        return mapOfficer(officer);
    }

    public List<OfficerProfileResponse> findByDepartment(String department) {
        return officersRepository.findByDepartmentIgnoreCase(department)
                .stream()
                .map(this::mapOfficer)
                .collect(Collectors.toList());
    }

    public List<OfficerProfileResponse> findByAssignmentArea(String assignmentArea) {
        return officersRepository.findByAssignmentAreaIgnoreCase(assignmentArea)
                .stream()
                .map(this::mapOfficer)
                .collect(Collectors.toList());
    }

    private OfficerProfileResponse mapOfficer(Officers officer) {
        OfficerProfileResponse response = new OfficerProfileResponse();
        response.setUserId(officer.getUsers().getUserId());
        response.setFirstName(officer.getUsers().getFirstName());
        response.setLastName(officer.getUsers().getLastName());
        response.setEmail(officer.getUsers().getEmail());
        response.setPhoneNumber(officer.getUsers().getPhoneNumber());
        response.setRole(officer.getUsers().getRole());
        response.setOfficerDetails(mapOfficerDetails(officer));
        return response;
    }

    private OfficerDetailsResponse mapOfficerDetails(Officers officer) {
        OfficerDetailsResponse details = new OfficerDetailsResponse();
        details.setBadgeNumber(officer.getBadgeNumber());
        details.setDepartment(officer.getDepartment());
        details.setRank(officer.getRank());
        details.setAssignmentArea(officer.getAssignmentArea());
        return details;
    }
}
