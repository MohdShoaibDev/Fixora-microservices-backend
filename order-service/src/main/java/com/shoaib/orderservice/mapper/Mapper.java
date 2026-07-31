package com.shoaib.orderservice.mapper;

import com.shoaib.orderservice.dtos.OrderDto;
import com.shoaib.orderservice.dtos.OrderItemDto;
import com.shoaib.orderservice.entity.Order;
import com.shoaib.orderservice.entity.OrderItem;

import java.util.List;

public final class Mapper {

    private Mapper() {
    }

    public static OrderItemDto mapOrderItemIntoOrderItemDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .productDescription(orderItem.getProductDescription())
                .productImage(orderItem.getProductImage())
                .price(orderItem.getPrice())
                .reviewed(false)
                .createdAt(orderItem.getCreatedAt())
                .updatedAt(orderItem.getUpdatedAt())
                .build();
    }

    public static OrderDto mapOrderIntoOrderDto(Order order, List<OrderItem> orderItem) {
        if (order == null) {
            return null;
        }
        return OrderDto.builder()
                .id(order.getId())
                .addressId(order.getAddressId())
                .orderStatus(order.getOrderStatus())
                .paymentId(order.getRazorpayPaymentId())
                .currency(order.getCurrency())
                .paymentStatus(order.getPaymentStatus())
                .orderItemList(orderItem.stream().map(Mapper::mapOrderItemIntoOrderItemDto).toList())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
