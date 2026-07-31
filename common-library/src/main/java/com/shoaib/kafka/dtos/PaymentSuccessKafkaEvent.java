package com.shoaib.kafka.dtos;

import java.util.UUID;

public record PaymentSuccessKafkaEvent(
        UUID paymentId,
        String razorpayOrderId,
        String gatewayPaymentId,
        UUID userId
) {
}
