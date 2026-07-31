package com.shoaib.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "recent_view_product",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_recent_user_product",
                        columnNames = {"userId", "productId"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_recent_user_created",
                        columnList = "userId, createdAt"
                )
        }
)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentViewProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
