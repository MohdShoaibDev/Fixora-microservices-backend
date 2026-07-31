package com.shoaib.productservice.entity;

import com.shoaib.productservice.utility.ReservationStatus;
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
        name = "product_reservations",
        indexes = {
                @Index(
                        name = "idx_product_reservation_order_id",
                        columnList = "order_id"
                ),
                @Index(
                        name = "idx_product_reservation_product_id",
                        columnList = "product_id"
                ),
                @Index(
                        name = "idx_product_reservation_status_expires_at",
                        columnList = "status, expires_at"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_reservation_order_product",
                        columnNames = {
                                "order_id",
                                "product_id"
                        }
                )
        }
)
public class ReserveProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.RESERVED;

    @Column(name = "expires_at", nullable = false)
    @Builder.Default
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ReserveProduct create(
            UUID orderId,
            UUID productId,
            Integer quantity
    ) {
        return ReserveProduct.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .status(ReservationStatus.RESERVED)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
    }
}