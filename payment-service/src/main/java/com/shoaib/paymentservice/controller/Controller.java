package com.shoaib.paymentservice.controller;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.payment.RazorpayOrderIdRequestDto;
import com.shoaib.payment.RazorpayOrderIdResponse;
import com.shoaib.paymentservice.dtos.VerifyRazorpayPaymentRequest;
import com.shoaib.paymentservice.dtos.VerifyRazorpayPaymentResponse;
import com.shoaib.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("payments")
@RequiredArgsConstructor
public class Controller {

    private final PaymentService paymentServiceImpl;

    @PostMapping("/create-order")
    RazorpayOrderIdResponse  createOrder(@RequestHeader("X-User-Id") UUID userId,
                                                                      @RequestBody @Valid RazorpayOrderIdRequestDto
                                                                              razorpayOrderRequest){
        return paymentServiceImpl.createRazorpayOrder(userId, razorpayOrderRequest);
    }

    @PostMapping("/verify")
    ResponseEntity<ApiResponse<VerifyRazorpayPaymentResponse>> verifyPayment(@RequestHeader("X-User-id") UUID userId, @RequestBody @Valid VerifyRazorpayPaymentRequest
                                                                                     verifyRazorpayPaymentRequest){
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Payment has completed",
                paymentServiceImpl.verifyRazorpayPayment(userId, verifyRazorpayPaymentRequest)));
    }
}
