package com.shoaib.productservice.repository;

import com.shoaib.productservice.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>,
        JpaSpecificationExecutor<Product> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdForReviewUpdate(@Param("productId") UUID productId);
    List<Product> findByIdInAndActiveIsTrueOrderByCreatedAtDesc(List<UUID> list);

    List<Product> findByIdInAndActiveIsTrue(List<UUID> list);
    List<Product> findByIdInAndActiveIsTrue(List<UUID> list, Sort sort);


    Optional<Product> findByCategoryIdAndActiveIsTrueAndGeneralPurposeIsTrue(UUID categoryId);

    @Modifying
    @Query("""
    UPDATE Product p
    SET p.reserveStock = p.reserveStock + :quantity
    WHERE p.id = :productId
""")
    int reserveStock(
            @Param("productId") UUID productId,
            @Param("quantity") int quantity
    );

    @Modifying
    @Query("""
    UPDATE Product p
    SET p.stock = p.stock - :quantity,
        p.reserveStock = p.reserveStock - :quantity
    WHERE p.id = :productId
""")
    int confirmReservation(
            @Param("productId") UUID productId,
            @Param("quantity") int quantity
    );
}
