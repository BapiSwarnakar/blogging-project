package com.stech.payment.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.common.library.GlobalApiResponse;
import com.stech.common.security.util.SecurityUtils;
import com.stech.payment.dto.request.PaymentRequest;
import com.stech.payment.service.PaymentService;
import com.stech.payment.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/create-order")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> createOrder(@RequestBody PaymentRequest paymentRequest) throws Exception {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalApiResponse.error("User not authenticated", "Authentication Error"));
        }
        return ResponseEntity.ok(GlobalApiResponse.success(paymentService.createOrder(paymentRequest, userId), "Order created successfully"));
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> verifyPayment(@RequestBody Map<String, String> paymentDetails) throws Exception {
        boolean isValid = paymentService.verifyPayment(paymentDetails);
        if (isValid) {
            Long userId = SecurityUtils.getCurrentUserId();
            Long planId = Long.parseLong(paymentDetails.get("planId"));
            subscriptionService.upgradeSubscription(userId, planId);
            return ResponseEntity.ok(GlobalApiResponse.success("Payment verified and plan upgraded", "Success"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GlobalApiResponse.error("Payment verification failed", "Payment Error"));
        }
    }

    @GetMapping("/current-subscription")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> getCurrentSubscription() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalApiResponse.error("User not authenticated", "Authentication Error"));
        }
        return ResponseEntity.ok(GlobalApiResponse.success(subscriptionService.getUserSubscription(userId), "User subscription fetched successfully"));
    }

}

