package com.shoaib.paymentservice.entity;

import com.shoaib.paymentservice.util.enums.PaymentGateway;
import com.shoaib.paymentservice.util.enums.PaymentMethod;
import com.shoaib.paymentservice.util.enums.PaymentStatus;
import com.shoaib.util.enums.Currency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(
                        name = "idx_payment_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_payment_order_id",
                        columnList = "order_id"
                ),
                @Index(
                        name = "idx_payment_gateway_order_id",
                        columnList = "gateway_order_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_gateway_order_id",
                        columnNames = "gateway_order_id"
                ),
                @UniqueConstraint(
                        name = "uk_payment_gateway_payment_id",
                        columnNames = "gateway_payment_id"
                )
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "order_id",
            nullable = false
    )
    private UUID orderId;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(
            name = "currency",
            nullable = false,
            length = 3
    )
    private Currency currency = Currency.INR;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(
            name = "payment_gateway",
            nullable = false,
            length = 30
    )
    private PaymentGateway paymentGateway = PaymentGateway.RAZORPAY;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private PaymentMethod paymentMethod = PaymentMethod.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private PaymentStatus status = PaymentStatus.CREATED;

    @Column(
            name = "gateway_order_id",
            length = 150
    )
    private String gatewayOrderId;

    @Column(
            name = "gateway_payment_id",
            length = 150
    )
    private String gatewayPaymentId;

    @Column(
            name = "gateway_signature",
            length = 500
    )
    private String gatewaySignature;

    @Column(
            name = "failure_code",
            length = 100
    )
    private String failureCode;

    @Column(
            name = "failure_message",
            length = 1000
    )
    private String failureMessage;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Builder.Default
    @Column(
            name = "refunded_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @Version
    @Column(nullable = false)
    private Long version;

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

    public static Payment create(
            UUID userId,
            UUID orderId,
            BigDecimal amount,
            Currency currency
    ) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }

        if (orderId == null) {
            throw new IllegalArgumentException(
                    "Order ID is required"
            );
        }

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        return Payment.builder()
                .userId(userId)
                .orderId(orderId)
                .amount(amount)
                .currency(
                        currency == null
                                ? Currency.INR
                                : currency
                )
                .paymentGateway(PaymentGateway.RAZORPAY)
                .paymentMethod(PaymentMethod.UNKNOWN)
                .status(PaymentStatus.CREATED)
                .refundedAmount(BigDecimal.ZERO)
                .build();
    }

    public void assignGatewayOrderId(String gatewayOrderId) {
        if (gatewayOrderId == null || gatewayOrderId.isBlank()) {
            throw new IllegalArgumentException(
                    "Gateway order ID cannot be empty"
            );
        }

        if (this.gatewayOrderId != null
                && !this.gatewayOrderId.equals(gatewayOrderId)) {
            throw new IllegalStateException(
                    "Gateway order ID is already assigned"
            );
        }

        this.gatewayOrderId = gatewayOrderId;
    }

    public void markSuccessful(
            String gatewayPaymentId,
            String gatewaySignature,
            PaymentMethod paymentMethod
    ) {
        if (gatewayPaymentId == null
                || gatewayPaymentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Gateway payment ID cannot be empty"
            );
        }

        if (gatewaySignature == null
                || gatewaySignature.isBlank()) {
            throw new IllegalArgumentException(
                    "Gateway signature cannot be empty"
            );
        }

        if (paymentMethod == null
                || paymentMethod == PaymentMethod.UNKNOWN) {
            throw new IllegalArgumentException(
                    "Valid payment method is required"
            );
        }

        if (this.status == PaymentStatus.PAID) {
            if (!gatewayPaymentId.equals(this.gatewayPaymentId)) {
                throw new IllegalStateException(
                        "Payment is already completed with another payment ID"
                );
            }

            return;
        }

        if (this.status == PaymentStatus.REFUNDED
                || this.status == PaymentStatus.PARTIALLY_REFUNDED) {
            throw new IllegalStateException(
                    "Refunded payment cannot be marked as paid"
            );
        }

        this.gatewayPaymentId = gatewayPaymentId;
        this.gatewaySignature = gatewaySignature;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();

        this.failureCode = null;
        this.failureMessage = null;
        this.failedAt = null;
    }

    public void markFailed(
            String failureCode,
            String failureMessage
    ) {
        if (this.status == PaymentStatus.PAID
                || this.status == PaymentStatus.PARTIALLY_REFUNDED
                || this.status == PaymentStatus.REFUNDED) {
            throw new IllegalStateException(
                    "Completed payment cannot be marked as failed"
            );
        }

        this.status = PaymentStatus.FAILED;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
        this.failedAt = LocalDateTime.now();
    }

    public void markRefunded(BigDecimal refundAmount) {
        if (this.status != PaymentStatus.PAID
                && this.status != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new IllegalStateException(
                    "Only paid payments can be refunded"
            );
        }

        if (refundAmount == null
                || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Refund amount must be greater than zero"
            );
        }

        BigDecimal currentRefundedAmount =
                this.refundedAmount == null
                        ? BigDecimal.ZERO
                        : this.refundedAmount;

        BigDecimal updatedRefundedAmount =
                currentRefundedAmount.add(refundAmount);

        if (updatedRefundedAmount.compareTo(this.amount) > 0) {
            throw new IllegalArgumentException(
                    "Refunded amount cannot exceed payment amount"
            );
        }

        this.refundedAmount = updatedRefundedAmount;

        if (updatedRefundedAmount.compareTo(this.amount) == 0) {
            this.status = PaymentStatus.REFUNDED;
            this.refundedAt = LocalDateTime.now();
        } else {
            this.status = PaymentStatus.PARTIALLY_REFUNDED;
            this.refundedAt = null;
        }
    }
}