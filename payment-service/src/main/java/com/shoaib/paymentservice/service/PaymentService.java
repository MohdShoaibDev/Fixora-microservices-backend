package com.shoaib.paymentservice.service;

import com.shoaib.payment.RazorpayOrderIdRequestDto;
import com.shoaib.payment.RazorpayOrderIdResponse;
import com.shoaib.paymentservice.dtos.VerifyRazorpayPaymentRequest;
import com.shoaib.paymentservice.dtos.VerifyRazorpayPaymentResponse;

import java.util.UUID;

public interface PaymentService {
    RazorpayOrderIdResponse createRazorpayOrder(UUID userId,
                                                RazorpayOrderIdRequestDto razorpayOrderRequest);
    VerifyRazorpayPaymentResponse verifyRazorpayPayment(UUID userId, VerifyRazorpayPaymentRequest verifyRazorpayPaymentRequest);
}
