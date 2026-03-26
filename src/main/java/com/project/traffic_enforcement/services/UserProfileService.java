package com.project.traffic_enforcement.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.OfficerDetailsResponse;
import com.project.traffic_enforcement.dto.OwnerDetailsResponse;
import com.project.traffic_enforcement.dto.UserProfileResponse;
import com.project.traffic_enforcement.models.Officers;
import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.models.enums.Roles;
import com.project.traffic_enforcement.repository.OfficersRepository;
import com.project.traffic_enforcement.repository.OwnersRepository;
import com.project.traffic_enforcement.repository.UsersRepository;
import com.project.traffic_enforcement.security.config.UserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UsersRepository usersRepository;
    private final OwnersRepository ownersRepository;
    private final OfficersRepository officersRepository;

    public UserProfileResponse getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = resolveEmail(authentication);
        System.out.println("Resolved email: " + email); // Debugging statement
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : "+email));

        return buildProfile(user);
    }

    private String resolveEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return authentication.getName();
    }

    private UserProfileResponse buildProfile(Users user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getUserId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());
        response.setProfilePictureUrl(user.getProfilePictureUrl());
        response.setLastLogin(user.getLastLogin());
        response.setCreatedAt(user.getCreatedAt());

        if (user.getRole() == Roles.OWNERS) {
            ownersRepository.findByOwner(user).ifPresent(owner -> response.setOwnerDetails(mapOwner(owner)));
        }
        if (user.getRole() == Roles.ROAD_OFFICERS || user.getRole() == Roles.APPEAL_OFFICERS) {
            officersRepository.findByUsers(user).ifPresent(officer -> response.setOfficerDetails(mapOfficer(officer)));
        }

        return response;
    }

    private OwnerDetailsResponse mapOwner(Owners owner) {
        OwnerDetailsResponse details = new OwnerDetailsResponse();
        details.setAddress(owner.getAddress());
        details.setCity(owner.getCity());
        details.setState(owner.getState());
        details.setDriversLicenseNumber(owner.getDriversLicenseNumber());
        return details;
    }

    private OfficerDetailsResponse mapOfficer(Officers officer) {
        OfficerDetailsResponse details = new OfficerDetailsResponse();
        details.setBadgeNumber(officer.getBadgeNumber());
        details.setDepartment(officer.getDepartment());
        details.setRank(officer.getRank());
        details.setAssignmentArea(officer.getAssignmentArea());
        return details;
    }


}
