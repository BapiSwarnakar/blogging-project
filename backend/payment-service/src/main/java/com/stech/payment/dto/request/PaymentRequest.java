package com.stech.payment.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    private Long planId;
    private Double amount;
    private String currency; // INR
}
