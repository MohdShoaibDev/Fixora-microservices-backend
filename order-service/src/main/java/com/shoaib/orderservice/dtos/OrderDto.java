package com.shoaib.orderservice.dtos;

import com.shoaib.orderservice.util.enums.OrderStatus;
import com.shoaib.orderservice.util.enums.PaymentStatus;
import com.shoaib.util.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDto {
    private UUID id;
    private UUID addressId;
    private OrderStatus orderStatus;
    private UUID paymentId;
    private Currency currency;
    private PaymentStatus paymentStatus;
    private List<OrderItemDto> orderItemList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

