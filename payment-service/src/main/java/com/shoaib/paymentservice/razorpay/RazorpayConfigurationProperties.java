package com.shoaib.paymentservice.razorpay;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "razorpay")
public record RazorpayConfigurationProperties (
        String keyId,
        String keySecret
){}
