package com.shoaib.productservice.repository;

import com.shoaib.productservice.entity.RecentViewProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecentViewProductRepository extends JpaRepository<RecentViewProduct, UUID> {
    Optional<RecentViewProduct> findByUserIdAndProductId(UUID userId, UUID productId);
    List<RecentViewProduct> findTop20ByUserIdOrderByCreatedAtAsc(UUID userId);
    RecentViewProduct findFirstByUserIdOrderByCreatedAtDesc(UUID userId);
    List<RecentViewProduct> findByUserId(UUID userId);
}
