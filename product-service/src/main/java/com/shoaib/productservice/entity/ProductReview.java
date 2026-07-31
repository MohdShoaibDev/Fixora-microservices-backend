package com.shoaib.productservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_reviews", indexes = {
        @Index(name = "idx_product_review_product_id", columnList = "product_id"),
        @Index(name = "idx_product_review_user_id", columnList = "user_id"),
        @Index(name = "idx_product_review_product_created", columnList = "product_id, created_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_review_product_user", columnNames = {"product_id", "user_id"})
})
@Builder
@Getter
@Check(constraints = "rating >= 1 AND rating <= 5")
public class ProductReview {

    protected ProductReview() {}

    private ProductReview(UUID id, UUID productId, UUID userId, String userName, Integer rating, String comment,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_name", nullable = false, length = 150)
    @ColumnDefault("'Unknown User'")
    private String userName;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ProductReview create(UUID productId, UUID userId, String userName, Integer rating, String comment) {
        return ProductReview.builder().productId(productId).userId(userId).userName(userName.trim()).rating(rating)
                .comment(comment.trim()).build();
    }

    public void update(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment.trim();
    }
}
