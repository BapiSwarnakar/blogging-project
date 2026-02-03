package com.stech.payment.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.stech.payment.dto.request.PaymentRequest;
import com.stech.payment.dto.response.PaymentResponse;
import com.stech.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id:rzp_test_placeholder}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:secret_placeholder}")
    private String razorpayKeySecret;

    @Override
    public PaymentResponse createOrder(PaymentRequest paymentRequest, Long userId) throws Exception {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", paymentRequest.getAmount() * 100); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + userId + "_" + System.currentTimeMillis());

        Order order = razorpay.orders.create(orderRequest);

        return PaymentResponse.builder()
                .orderId(order.get("id"))
                .razorpayKey(razorpayKeyId)
                .amount(paymentRequest.getAmount())
                .currency("INR")
                .status(order.get("status"))
                .build();
    }

    @Override
    public boolean verifyPayment(Map<String, String> paymentDetails) throws Exception {
        String razorpayOrderId = paymentDetails.get("razorpay_order_id");
        String razorpayPaymentId = paymentDetails.get("razorpay_payment_id");
        String razorpaySignature = paymentDetails.get("razorpay_signature");

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", razorpayOrderId);
        options.put("razorpay_payment_id", razorpayPaymentId);
        options.put("razorpay_signature", razorpaySignature);

        return Utils.verifyPaymentSignature(options, razorpayKeySecret);
    }
}
