package com.shoaib.cartservice.repository;

import com.shoaib.cartservice.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    Page<CartItem> findAllByCartId(UUID cartId, Pageable pageable);

    Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

    List<CartItem> findByCartId(UUID cartId);


    int deleteByCartIdAndProductId(UUID cartId, UUID productId);


    @Modifying
    @Query("""
            DELETE FROM CartItem cartItem
            WHERE cartItem.cartId = :cartId
            """)
    int deleteAllByCartId(@Param("cartId") UUID cartId);
}