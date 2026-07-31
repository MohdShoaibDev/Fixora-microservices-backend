package com.shoaib.orderservice.service;

import com.shoaib.kafka.dtos.PaymentSuccessKafkaEvent;
import com.shoaib.orderservice.dtos.OrderDto;
import com.shoaib.orderservice.dtos.OrderIdDto;
import com.shoaib.orderservice.dtos.OrderPageDto;
import com.shoaib.orderservice.dtos.OrderRequestDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderPageDto<List<OrderDto>> getOrders(UUID userId, Integer page);
    OrderIdDto createOrderId(UUID userId, String token, OrderRequestDto orderRequestDto);
    void orderPaymentCompleted(PaymentSuccessKafkaEvent paymentSuccessKafkaEvent);
    void markPaymentFailed(String orderID);
}
