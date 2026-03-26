package com.project.traffic_enforcement.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.project.traffic_enforcement.dto.PaymentRequest;
import com.project.traffic_enforcement.dto.PaymentResponse;
import com.project.traffic_enforcement.dto.PaymentTotalResponse;
import com.project.traffic_enforcement.dto.PaystackInitializeApiRequest;
import com.project.traffic_enforcement.dto.PaystackInitializeApiResponse;
import com.project.traffic_enforcement.dto.PaystackInitializeResponse;
import com.project.traffic_enforcement.dto.PaystackInitializeRequest;
import com.project.traffic_enforcement.dto.PaystackVerifyApiResponse;
import com.project.traffic_enforcement.dto.PaystackVerifyResponse;
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
    private final RestTemplate restTemplate;

    @Value("${paystack.secret-key}")
    private String paystackSecretKey;

    @Value("${paystack.base-url:https://api.paystack.co}")
    private String paystackBaseUrl;

    public PaymentResponse createPayment(PaymentRequest request) {
        Owners owner = resolveCurrentOwner();

        if (request.getViolationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Violation ID is required");
        }

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }

        Violation violation = resolveOwnedViolation(request.getViolationId(), owner);

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

    public PaystackInitializeResponse initializePaystackPayment(PaystackInitializeRequest request) {
        Owners owner = resolveCurrentOwner();

        if (request.getViolationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Violation ID is required");
        }

        Violation violation = resolveOwnedViolation(request.getViolationId(), owner);

        if (violation.getStatus() == ViolationStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Violation already paid");
        }

        Float fineAmount = violation.getFineAmount();
        if (fineAmount == null || fineAmount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Violation fine amount is invalid");
        }

        String email = owner.getOwner().getEmail();
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner email is required");
        }

        if (paystackSecretKey == null || paystackSecretKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Paystack secret key not configured");
        }

        long amountInKobo = Math.round(fineAmount * 100);
        String reference = generateReferenceId();

        PaystackInitializeApiRequest apiRequest = new PaystackInitializeApiRequest();
        apiRequest.setEmail(email);
        apiRequest.setAmount(amountInKobo);
        apiRequest.setReference(reference);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(paystackSecretKey);

        HttpEntity<PaystackInitializeApiRequest> entity = new HttpEntity<>(apiRequest, headers);
        String url = paystackBaseUrl + "/transaction/initialize";

        try {
            ResponseEntity<PaystackInitializeApiResponse> response = restTemplate.postForEntity(
                    url, entity, PaystackInitializeApiResponse.class);
            PaystackInitializeApiResponse body = response.getBody();
            if (body == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty response from Paystack");
            }
            if (!body.isStatus()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, body.getMessage());
            }
            if (body.getData() == null || body.getData().getAuthorizationUrl() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid response from Paystack");
            }

            PaystackInitializeResponse result = new PaystackInitializeResponse();
            result.setAuthorizationUrl(body.getData().getAuthorizationUrl());
            result.setAccessCode(body.getData().getAccessCode());
            result.setReference(body.getData().getReference());
            result.setMessage(body.getMessage());
            return result;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to initialize Paystack payment");
        }
    }

    public PaystackVerifyResponse verifyPaystackPayment(String reference) {
        resolveCurrentOwner();

        if (reference == null || reference.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reference is required");
        }

        if (paystackSecretKey == null || paystackSecretKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Paystack secret key not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(paystackSecretKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = paystackBaseUrl + "/transaction/verify/" + reference;

        try {
            ResponseEntity<PaystackVerifyApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, PaystackVerifyApiResponse.class);
            PaystackVerifyApiResponse body = response.getBody();
            if (body == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty response from Paystack");
            }
            if (!body.isStatus()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, body.getMessage());
            }
            if (body.getData() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid response from Paystack");
            }

            PaystackVerifyApiResponse.PaystackVerifyApiData data = body.getData();
            PaystackVerifyResponse result = new PaystackVerifyResponse();
            result.setVerified("success".equalsIgnoreCase(data.getStatus()));
            result.setPaymentStatus(data.getStatus());
            result.setReference(data.getReference());
            result.setAmount(data.getAmount());
            result.setCurrency(data.getCurrency());
            result.setGatewayResponse(data.getGatewayResponse());
            result.setPaidAt(data.getPaidAt());
            result.setMessage(body.getMessage());
            return result;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to verify Paystack payment");
        }
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

    private Violation resolveOwnedViolation(UUID violationId, Owners owner) {
        Violation violation = violationRepository.findById(violationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Violation not found"));

        Vehicles vehicle = violation.getVehicle();
        if (vehicle == null || vehicle.getOwner() == null
                || !vehicle.getOwner().getOwnerId().equals(owner.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only pay for your own violations");
        }

        return violation;
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
