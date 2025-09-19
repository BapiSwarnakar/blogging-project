package com.stech.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    @GetMapping("/me")
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("Payment details");
    }

    @GetMapping("/all")
    public ResponseEntity<String> getAllUsers() {
        return ResponseEntity.ok("All Payment");
    }
    
}
