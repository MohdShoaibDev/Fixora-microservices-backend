package com.shoaib.cartservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "cart_items",
        indexes = {
                @Index(
                        name = "idx_cart_item_cart_id",
                        columnList = "cart_id"
                ),
                @Index(
                        name = "idx_cart_item_product_id",
                        columnList = "product_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_item_cart_product",
                        columnNames = {
                                "cart_id",
                                "product_id"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "cart_id",
            nullable = false,
            updatable = false
    )
    private UUID cartId;

    @Column(
            name = "product_id",
            nullable = false,
            updatable = false
    )
    private UUID productId;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;
}