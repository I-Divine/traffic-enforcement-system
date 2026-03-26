package com.project.traffic_enforcement.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.PaymentRequest;
import com.project.traffic_enforcement.dto.PaymentResponse;
import com.project.traffic_enforcement.dto.PaymentTotalResponse;
import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Payment;
import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.models.Vehicles;
import com.project.traffic_enforcement.models.Violation;
import com.project.traffic_enforcement.models.enums.PaymentTotalPeriod;
import com.project.traffic_enforcement.models.enums.Roles;
import com.project.traffic_enforcement.models.enums.ViolationStatus;
import com.project.traffic_enforcement.repository.OwnersRepository;
import com.project.traffic_enforcement.repository.PaymentRepository;
import com.project.traffic_enforcement.repository.UsersRepository;
import com.project.traffic_enforcement.repository.ViolationRepository;
import com.project.traffic_enforcement.security.config.UserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ViolationRepository violationRepository;
    private final UsersRepository usersRepository;
    private final OwnersRepository ownersRepository;

    public PaymentResponse createPayment(PaymentRequest request) {
        Owners owner = resolveCurrentOwner();

        if (request.getViolationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Violation ID is required");
        }

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }

        Violation violation = violationRepository.findById(request.getViolationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Violation not found"));

        Vehicles vehicle = violation.getVehicle();
        if (vehicle == null || vehicle.getOwner() == null || !vehicle.getOwner().getOwnerId().equals(owner.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only pay for your own violations");
        }

        Users user = owner.getOwner();

        Payment payment = new Payment();
        payment.setViolation(violation);
        payment.setAmount(request.getAmount());
        payment.setReferenceId(generateReferenceId());
        payment.setUserEmail(user.getEmail());
        payment.setFullName(buildFullName(user));
        payment.setCreatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        violation.setStatus(ViolationStatus.PAID);
        violationRepository.save(violation);

        return mapToResponse(saved);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PaymentTotalResponse getTotalForPeriod(PaymentTotalPeriod period) {
        if (period == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period is required");
        }

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = resolveStartDate(period, endDate);

        Double totalAmount = paymentRepository.sumAmountBetween(startDate, endDate);
        if (totalAmount == null) {
            totalAmount = 0d;
        }

        PaymentTotalResponse response = new PaymentTotalResponse();
        response.setPeriod(period);
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setTotalAmount(totalAmount);
        return response;
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners can make payments");
        }

        return ownersRepository.findByOwner(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner profile not found"));
    }

    private String resolveEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return authentication.getName();
    }

    private String generateReferenceId() {
        return "PAY-" + UUID.randomUUID();
    }

    private String buildFullName(Users user) {
        return String.format("%s %s", user.getFirstName(), user.getLastName()).trim();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setReferenceId(payment.getReferenceId());
        response.setUserEmail(payment.getUserEmail());
        response.setFullName(payment.getFullName());
        response.setAmount(payment.getAmount());
        response.setCreatedAt(payment.getCreatedAt());
        if (payment.getViolation() != null) {
            response.setViolationId(payment.getViolation().getViolationId());
        }
        return response;
    }

    private LocalDateTime resolveStartDate(PaymentTotalPeriod period, LocalDateTime endDate) {
        return switch (period) {
            case MONTH -> endDate.minusMonths(1);
            case QUARTER -> endDate.minusMonths(3);
            case YEAR -> endDate.minusYears(1);
            case FIVE_YEARS -> endDate.minusYears(5);
        };
    }
}
