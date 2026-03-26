package com.project.traffic_enforcement.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.traffic_enforcement.dto.PaymentRequest;
import com.project.traffic_enforcement.dto.PaymentResponse;
import com.project.traffic_enforcement.dto.PaymentTotalResponse;
import com.project.traffic_enforcement.dto.PaystackInitializeRequest;
import com.project.traffic_enforcement.dto.PaystackInitializeResponse;
import com.project.traffic_enforcement.dto.PaystackVerifyResponse;
import com.project.traffic_enforcement.models.enums.PaymentTotalPeriod;
import com.project.traffic_enforcement.services.PaymentService;


@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNERS')")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @PostMapping("/initialize")
    @PreAuthorize("hasRole('OWNERS')")
    public ResponseEntity<PaystackInitializeResponse> initializePaystackPayment(
            @RequestBody PaystackInitializeRequest request) {
        return ResponseEntity.ok(paymentService.initializePaystackPayment(request));
    }

    @GetMapping("/verify")
    @PreAuthorize("hasRole('OWNERS')")
    public ResponseEntity<PaystackVerifyResponse> verifyPaystackPayment(@RequestParam String reference) {
        return ResponseEntity.ok(paymentService.verifyPaystackPayment(reference));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ROAD_OFFICERS','APPEAL_OFFICERS')")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('ADMIN','ROAD_OFFICERS','APPEAL_OFFICERS')")
    public ResponseEntity<PaymentTotalResponse> getPaymentTotal(@RequestParam PaymentTotalPeriod period) {
        return ResponseEntity.ok(paymentService.getTotalForPeriod(period));
    }
}
