package com.shoaib.authservice.repository;

import com.shoaib.authservice.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(
            String tokenHash
    );

    Optional<RefreshToken> findByUserId(
            UUID tokenHash
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select token
            from RefreshToken token
            where token.tokenHash = :tokenHash
            """)
    Optional<RefreshToken> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash
    );

    @Modifying
    @Query("""
            update RefreshToken token
            set token.revokedAt = :revokedAt
            where token.userId = :userId
              and token.revokedAt is null
              and token.consumedAt is null
            """)
    int revokeAllActiveTokensByUserId(
            @Param("userId") UUID userId,
            @Param("revokedAt") LocalDateTime revokedAt
    );

    @Modifying
    @Query("""
            delete from RefreshToken token
            where token.expiresAt < :currentTime
            """)
    int deleteExpiredTokens(
            @Param("currentTime") LocalDateTime currentTime
    );

    @Modifying
    @Query("""
        DELETE FROM RefreshToken rt
        WHERE rt.expiresAt <= :now
           OR rt.consumedAt IS NOT NULL
           OR rt.revokedAt IS NOT NULL
        """)
    int deleteExpiredConsumedAndRevokedTokens(
            @Param("now") LocalDateTime now
    );
}
