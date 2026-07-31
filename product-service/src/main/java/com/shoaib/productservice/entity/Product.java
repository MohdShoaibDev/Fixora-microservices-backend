package com.shoaib.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_category_active_booking", columnList = "categoryId, active"),
                @Index(name = "idx_product_category_active_rating", columnList = "categoryId, active, totalRating"),
                @Index(name = "idx_product_category_active_generalPurpose", columnList = "categoryId, active, generalPurpose"),
                @Index(name = "idx_product_active_price", columnList = "active, price"),
                @Index(name = "idx_product_active_rating", columnList = "active, totalRating"),
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer estimatedDurationInMinutes;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = true)
    private String thumbnailUrl;

    @Column(nullable = true)
    private Integer stock;

    @Column(nullable = true)
    @Builder.Default
    private Integer reserveStock = 0;

    @Builder.Default
    @Column(nullable = false)
    private Double totalRating = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalReviews = 0;

    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("0")
    private Long ratingSum = 0L;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalBookings = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean generalPurpose = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void addReviewRating(int newRating) {
        if (ratingSum == null) ratingSum = 0L;
        if (totalReviews == null) totalReviews = 0;
        ratingSum += newRating;
        totalReviews += 1;
        totalRating = (double) ratingSum / totalReviews;
    }

    public void updateReviewRating(int oldRating, int newRating) {
        if (ratingSum == null) ratingSum = 0L;
        if (totalReviews == null || totalReviews <= 0) {
            throw new IllegalStateException("Product review count is invalid");
        }
        ratingSum = Math.max(0L, ratingSum - oldRating + newRating);
        totalRating = (double) ratingSum / totalReviews;
    }

    public void removeReviewRating(int deletedRating) {
        if (ratingSum == null) ratingSum = 0L;
        if (totalReviews == null || totalReviews <= 1) {
            ratingSum = 0L;
            totalReviews = 0;
            totalRating = 0.0;
            return;
        }
        ratingSum = Math.max(0L, ratingSum - deletedRating);
        totalReviews -= 1;
        totalRating = (double) ratingSum / totalReviews;
    }
}
