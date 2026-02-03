package com.stech.payment.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String orderId;
    private String razorpayKey;
    private Double amount;
    private String currency;
    private String status;
}
