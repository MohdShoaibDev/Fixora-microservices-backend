package com.shoaib.cartservice.repository;

import com.shoaib.cartservice.entity.Cart;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT cart
            FROM Cart cart
            WHERE cart.userId = :userId
            """)
    Optional<Cart> findByUserIdForUpdate(
            @Param("userId") UUID userId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = """
                    INSERT INTO carts (
                        id,
                        user_id,
                        booking_date,
                        booking_time,
                        created_at,
                        updated_at
                    )
                    VALUES (
                        gen_random_uuid(),
                        :userId,
                        :bookingDate,
                        :bookingTime,
                        CURRENT_TIMESTAMP,
                        CURRENT_TIMESTAMP
                    )
                    ON CONFLICT (user_id)
                    DO NOTHING
                    """,
            nativeQuery = true
    )
    int createCartIfAbsent(@Param("userId") UUID userId, @Param("bookingDate") LocalDate bookingDate,
                           @Param("bookingTime") LocalTime bookingTime);
}