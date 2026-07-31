package com.shoaib.orderservice.entity;

import com.shoaib.orderservice.util.enums.OrderStatus;
import com.shoaib.orderservice.util.enums.PaymentStatus;
import com.shoaib.util.enums.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(
                        name = "idx_orders_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_orders_razorpay_order_id",
                        columnList = "razorpay_order_id"
                )
        }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID addressId = null;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING;

    private String razorpayOrderId;

    @Column
    @Builder.Default
    private UUID razorpayPaymentId = null;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Currency currency = Currency.UNKNOWN;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static Order create(
            UUID userId,
            Currency currency,
            UUID addressId
    ) {
        return Order.builder()
                .userId(userId)
                .currency(currency)
                .addressId(addressId)
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }
}