package com.project.traffic_enforcement.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.AppealRequest;
import com.project.traffic_enforcement.dto.AppealResponse;
import com.project.traffic_enforcement.models.Appeal;
import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.models.Vehicles;
import com.project.traffic_enforcement.models.Violation;
import com.project.traffic_enforcement.models.enums.AppealStatus;
import com.project.traffic_enforcement.models.enums.Roles;
import com.project.traffic_enforcement.models.enums.ViolationStatus;
import com.project.traffic_enforcement.repository.AppealRepository;
import com.project.traffic_enforcement.repository.OwnersRepository;
import com.project.traffic_enforcement.repository.UsersRepository;
import com.project.traffic_enforcement.repository.ViolationRepository;
import com.project.traffic_enforcement.security.config.UserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppealService {

    private final AppealRepository appealRepository;
    private final ViolationRepository violationRepository;
    private final UsersRepository usersRepository;
    private final OwnersRepository ownersRepository;

    public AppealResponse createAppeal(AppealRequest request) {
        Owners owner = resolveCurrentOwner();

        if (request.getViolationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Violation ID is required");
        }

        Violation violation = violationRepository.findById(request.getViolationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Violation not found"));

        Vehicles vehicle = violation.getVehicle();
        if (vehicle == null || vehicle.getOwner() == null || !vehicle.getOwner().getOwnerId().equals(owner.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only appeal your own violations");
        }

        Appeal appeal = new Appeal();
        appeal.setViolation(violation);
        appeal.setDescription(request.getDescription());
        appeal.setEvidenceUrl(request.getEvidenceUrl());
        appeal.setStatus(AppealStatus.PENDING);

        Appeal saved = appealRepository.save(appeal);

        violation.setStatus(ViolationStatus.APPEAL_PENDING);
        violationRepository.save(violation);

        return mapToResponse(saved);
    }

    public List<AppealResponse> getAllAppeals() {
        assertAppealOfficerOrAdmin();
        return appealRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppealResponse acceptAppeal(UUID appealId) {
        assertAppealOfficerOrAdmin();
        Appeal appeal = getAppealOrThrow(appealId);
        ensurePending(appeal);

        appeal.setStatus(AppealStatus.ACCEPTED);
        Appeal saved = appealRepository.save(appeal);

        Violation violation = appeal.getViolation();
        if (violation != null) {
            violation.setStatus(ViolationStatus.APPEAL_APPROVED);
            violationRepository.save(violation);
        }

        return mapToResponse(saved);
    }

    public AppealResponse rejectAppeal(UUID appealId) {
        assertAppealOfficerOrAdmin();
        Appeal appeal = getAppealOrThrow(appealId);
        ensurePending(appeal);

        appeal.setStatus(AppealStatus.REJECTED);
        Appeal saved = appealRepository.save(appeal);

        Violation violation = appeal.getViolation();
        if (violation != null) {
            violation.setStatus(ViolationStatus.APPEAL_REJECTED);
            violationRepository.save(violation);
        }

        return mapToResponse(saved);
    }

    private Appeal getAppealOrThrow(UUID appealId) {
        if (appealId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appeal ID is required");
        }
        return appealRepository.findById(appealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appeal not found"));
    }

    private void ensurePending(Appeal appeal) {
        if (appeal.getStatus() != AppealStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appeal has already been resolved");
        }
    }

    private Owners resolveCurrentOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = resolveEmail(authentication);
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Roles.OWNERS) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners can create appeals");
        }

        return ownersRepository.findByOwner(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner profile not found"));
    }

    private void assertAppealOfficerOrAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = resolveEmail(authentication);
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Roles.APPEAL_OFFICERS && user.getRole() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only appeal officers or admin can manage appeals");
        }
    }

    private String resolveEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return authentication.getName();
    }

    private AppealResponse mapToResponse(Appeal appeal) {
        AppealResponse response = new AppealResponse();
        response.setAppealId(appeal.getAppealId());
        if (appeal.getViolation() != null) {
            response.setViolationId(appeal.getViolation().getViolationId());
        }
        response.setDescription(appeal.getDescription());
        response.setEvidenceUrl(appeal.getEvidenceUrl());
        response.setStatus(appeal.getStatus());
        return response;
    }
}
