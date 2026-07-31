package com.shoaib.orderservice.repository;

import com.shoaib.orderservice.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUserId(UUID userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);

    @Modifying
    @Query(value = """
UPDATE orders
SET payment_status = :paymentStatus,
    order_status = :orderStatus
WHERE razorpay_order_id = :razorpayOrderId
""", nativeQuery = true)
    void findByRazorpayOrderIdAndUpdatePaymentAndOrderStatus(@Param("paymentStatus") String paymentStatus,
                                         @Param("orderStatus") String orderStatus,
                                         @Param("razorpayOrderId") String razorpayOrderId);
}
