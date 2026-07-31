package com.shoaib.productservice.repository;

import com.shoaib.productservice.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);
    Optional<ProductReview> findByIdAndUserId(UUID reviewId, UUID userId);
    Optional<ProductReview> findByUserIdAndProductId(UUID userId, UUID productId);
    List<ProductReview> findByUserIdAndProductIdIn(UUID userId, Collection<UUID> productIds);
    Page<ProductReview> findByUserIdAndProductId(UUID userId, UUID productId, Pageable pageable);
    Page<ProductReview> findByProductId(UUID productId, Pageable pageable);
}
