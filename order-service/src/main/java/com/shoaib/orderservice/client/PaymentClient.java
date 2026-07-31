package com.shoaib.orderservice.client;

import com.shoaib.payment.RazorpayOrderIdRequestDto;
import com.shoaib.payment.RazorpayOrderIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "payment-client",
url = "${services.payment-service.url}")
public interface PaymentClient {
    @PostMapping("payments/create-order")
    RazorpayOrderIdResponse createOrderId(@RequestHeader("X-User-Id") UUID userId,
                                          @RequestHeader("Authorization") String token,
                                          @RequestBody RazorpayOrderIdRequestDto  razorpayOrderRequestDto);
}
