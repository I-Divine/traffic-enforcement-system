package com.project.traffic_enforcement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.traffic_enforcement.dto.AppealDetailsResponse;
import com.project.traffic_enforcement.dto.UserSummaryResponse;
import com.project.traffic_enforcement.dto.UserWithPendingAppealResponse;
import com.project.traffic_enforcement.dto.UserWithPendingPaymentsResponse;
import com.project.traffic_enforcement.dto.UserWithViolationsResponse;
import com.project.traffic_enforcement.dto.ViolationDetailsResponse;
import com.project.traffic_enforcement.models.Appeal;
import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.models.Vehicles;
import com.project.traffic_enforcement.models.Violation;
import com.project.traffic_enforcement.models.enums.ViolationStatus;
import com.project.traffic_enforcement.repository.AppealRepository;
import com.project.traffic_enforcement.repository.OwnersRepository;
import com.project.traffic_enforcement.repository.PaymentRepository;
import com.project.traffic_enforcement.repository.UsersRepository;
import com.project.traffic_enforcement.repository.ViolationRepository;
import com.project.traffic_enforcement.repository.VehiclesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfficerDetailsService {

    private final UsersRepository usersRepository;
    private final ViolationRepository violationRepository;
    private final PaymentRepository paymentRepository;
    private final AppealRepository appealRepository;
    private final OwnersRepository ownersRepository;
    private final VehiclesRepository vehiclesRepository;

    /**
     * Get all users in the system
     */
    public List<UserSummaryResponse> getAllUsers() {
        return usersRepository.findAll()
                .stream()
                .map(this::mapToUserSummary)
                .collect(Collectors.toList());
    }

    /**
     * Get all users with their violations
     */
    public List<UserWithViolationsResponse> getAllUsersWithViolations() {
        return usersRepository.findAll()
                .stream()
                .map(this::mapUserWithViolations)
                .filter(response -> !response.getViolations().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Get all users with pending/unpaid violations
     */
    public List<UserWithPendingPaymentsResponse> getAllUsersWithPendingPayments() {
        List<Violation> unpaidViolations = paymentRepository.findUnpaidViolations();
        
        // Group violations by owner user
        return unpaidViolations.stream()
                .collect(Collectors.groupingBy(v -> v.getVehicle().getOwner().getOwner()))
                .entrySet()
                .stream()
                .map(entry -> mapUserWithPendingPayments(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Get all users with unresolved violations
     */
    public List<UserWithViolationsResponse> getAllUsersWithUnresolvedViolations() {
        List<Violation> unresolvedViolations = violationRepository.findAllUnresolvedViolations();
        
        // Group violations by owner user
        return unresolvedViolations.stream()
                .collect(Collectors.groupingBy(v -> v.getVehicle().getOwner().getOwner()))
                .entrySet()
                .stream()
                .map(entry -> {
                    UserWithViolationsResponse response = new UserWithViolationsResponse();
                    response.setUser(mapToUserSummary(entry.getKey()));
                    
                    List<ViolationDetailsResponse> violations = entry.getValue()
                            .stream()
                            .map(this::mapToViolationDetails)
                            .collect(Collectors.toList());
                    
                    response.setViolations(violations);
                    response.setTotalViolations(violations.size());
                    response.setUnresolvedViolations(violations.size());
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get all users with pending appeals
     */
    public List<UserWithPendingAppealResponse> getAllUsersWithPendingAppeals() {
        List<Appeal> pendingAppeals = appealRepository.findAllPendingAppeals();
        
        // Group appeals by owner user
        return pendingAppeals.stream()
                .collect(Collectors.groupingBy(a -> a.getViolation().getVehicle().getOwner().getOwner()))
                .entrySet()
                .stream()
                .map(entry -> mapUserWithPendingAppeals(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Get specific user with all violations
     */
    public UserWithViolationsResponse getUserWithViolations(UUID userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        return mapUserWithViolations(user);
    }

    /**
     * Get specific user with pending payments
     */
    public UserWithPendingPaymentsResponse getUserWithPendingPayments(UUID userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        List<Violation> unpaidViolations = paymentRepository.findUserUnpaidViolations(userId);
        return mapUserWithPendingPayments(user, unpaidViolations);
    }

    /**
     * Get specific user with unresolved violations
     */
    public UserWithViolationsResponse getUserWithUnresolvedViolations(UUID userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        List<Violation> unresolvedViolations = violationRepository.findUserUnresolvedViolations(userId);
        
        UserWithViolationsResponse response = new UserWithViolationsResponse();
        response.setUser(mapToUserSummary(user));
        
        List<ViolationDetailsResponse> violations = unresolvedViolations
                .stream()
                .map(this::mapToViolationDetails)
                .collect(Collectors.toList());
        
        response.setViolations(violations);
        response.setTotalViolations(violations.size());
        response.setUnresolvedViolations(violations.size());
        
        return response;
    }

    /**
     * Get specific user with pending appeals
     */
    public UserWithPendingAppealResponse getUserWithPendingAppeals(UUID userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        List<Appeal> pendingAppeals = appealRepository.findUserPendingAppeals(userId);
        return mapUserWithPendingAppeals(user, pendingAppeals);
    }

    // ==================== PRIVATE MAPPER METHODS ====================

    private UserSummaryResponse mapToUserSummary(Users user) {
        UserSummaryResponse response = new UserSummaryResponse();
        response.setUserId(user.getUserId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setProfilePictureUrl(user.getProfilePictureUrl());
        response.setRole(user.getRole());
        return response;
    }

    private UserWithViolationsResponse mapUserWithViolations(Users user) {
        // Get all vehicles owned by this user
        Optional<Owners> owner = ownersRepository.findByOwner(user);
        List<Violation> violations = new ArrayList<>();
        
        if (owner.isPresent()) {
            List<Vehicles> vehicles = vehiclesRepository.findByOwner(owner.get());
            violations = violationRepository.findByVehicleIn(vehicles);
        }
        
        UserWithViolationsResponse response = new UserWithViolationsResponse();
        response.setUser(mapToUserSummary(user));
        
        List<ViolationDetailsResponse> violationDetails = violations
                .stream()
                .map(this::mapToViolationDetails)
                .collect(Collectors.toList());
        
        response.setViolations(violationDetails);
        response.setTotalViolations(violationDetails.size());
        
        long unresolvedCount = violationDetails.stream()
                .filter(v -> v.getStatus() != ViolationStatus.RESOLVED)
                .count();
        response.setUnresolvedViolations((int) unresolvedCount);
        
        return response;
    }

    private UserWithPendingPaymentsResponse mapUserWithPendingPayments(Users user, List<Violation> unpaidViolations) {
        UserWithPendingPaymentsResponse response = new UserWithPendingPaymentsResponse();
        response.setUser(mapToUserSummary(user));
        
        List<ViolationDetailsResponse> violations = unpaidViolations
                .stream()
                .map(this::mapToViolationDetails)
                .collect(Collectors.toList());
        
        response.setUnpaidViolations(violations);
        response.setTotalUnpaidCount(violations.size());
        response.setTotalUnpaidAmount(violations.stream()
                .map(ViolationDetailsResponse::getFineAmount)
                .reduce(0f, Float::sum));
        
        return response;
    }

    private UserWithPendingAppealResponse mapUserWithPendingAppeals(Users user, List<Appeal> pendingAppeals) {
        UserWithPendingAppealResponse response = new UserWithPendingAppealResponse();
        response.setUser(mapToUserSummary(user));
        
        List<AppealDetailsResponse> appeals = pendingAppeals
                .stream()
                .map(this::mapToAppealDetails)
                .collect(Collectors.toList());
        
        response.setPendingAppeals(appeals);
        response.setTotalPendingAppeals(appeals.size());
        
        return response;
    }

    private ViolationDetailsResponse mapToViolationDetails(Violation violation) {
        ViolationDetailsResponse response = new ViolationDetailsResponse();
        response.setViolationId(violation.getViolationId());
        response.setPlateNumber(violation.getPlateNumber());
        response.setVehicleInfo(violation.getVehicle().getMake() + " " + violation.getVehicle().getModel() + " " + violation.getVehicle().getYear());
        response.setViolationDate(violation.getViolationDate());
        response.setFineAmount(violation.getFineAmount());
        response.setViolationType(violation.getViolationType());
        response.setDescription(violation.getDescription());
        response.setStatus(violation.getStatus());
        response.setState(violation.getState());
        response.setLga(violation.getLga());
        response.setOwnerUserId(violation.getVehicle().getOwner().getOwner().getUserId());
        
        // Check if violation is paid
        Optional<com.project.traffic_enforcement.models.Payment> payment = paymentRepository.findByViolation(violation);
        response.setIsPaid(payment.isPresent());
        
        return response;
    }

    private AppealDetailsResponse mapToAppealDetails(Appeal appeal) {
        AppealDetailsResponse response = new AppealDetailsResponse();
        response.setAppealId(appeal.getAppealId());
        response.setViolationId(appeal.getViolation().getViolationId());
        response.setViolationDescription(appeal.getViolation().getDescription());
        response.setFineAmount(appeal.getViolation().getFineAmount());
        response.setDescription(appeal.getDescription());
        response.setEvidenceUrl(appeal.getEvidenceUrl());
        response.setStatus(appeal.getStatus());
        response.setOwnerUserId(appeal.getViolation().getVehicle().getOwner().getOwner().getUserId());
        
        return response;
    }
}
