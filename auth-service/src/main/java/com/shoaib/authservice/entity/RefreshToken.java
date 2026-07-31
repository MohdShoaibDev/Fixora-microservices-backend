package com.shoaib.authservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(
                        name = "idx_refresh_token_hash",
                        columnList = "token_hash"
                ),
                @Index(
                        name = "idx_refresh_token_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_refresh_token_expires_at",
                        columnList = "expires_at"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refresh_token_hash",
                        columnNames = "token_hash"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {

    @Id
    private UUID id;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "token_hash",
            nullable = false,
            length = 64
    )
    private String tokenHash;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(
            name = "consumed_at"
    )
    private LocalDateTime consumedAt;

    @Column(
            name = "revoked_at"
    )
    private LocalDateTime revokedAt;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public static RefreshToken create(
            UUID userId,
            String tokenHash,
            LocalDateTime expiresAt
    ) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.id = UUID.randomUUID();
        refreshToken.userId = userId;
        refreshToken.tokenHash = tokenHash;
        refreshToken.expiresAt = expiresAt;

        return refreshToken;
    }

    public boolean isExpired() {
        return !expiresAt.isAfter(LocalDateTime.now());
    }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isUsable() {
        return !isExpired()
                && !isConsumed()
                && !isRevoked();
    }

    public void consume() {
        if (!isUsable()) {
            throw new IllegalStateException(
                    "Refresh token cannot be consumed"
            );
        }

        this.consumedAt = LocalDateTime.now();
    }

    public void revoke() {
        if (this.revokedAt == null) {
            this.revokedAt = LocalDateTime.now();
        }
    }
}
