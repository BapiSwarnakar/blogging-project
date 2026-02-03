package com.stech.payment.service;

import com.stech.payment.dto.request.PaymentRequest;
import com.stech.payment.dto.response.PaymentResponse;

import java.util.Map;

public interface PaymentService {
    PaymentResponse createOrder(PaymentRequest paymentRequest, Long userId) throws Exception;
    boolean verifyPayment(Map<String, String> paymentDetails) throws Exception;
}
