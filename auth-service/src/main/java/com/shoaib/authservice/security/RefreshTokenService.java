package com.shoaib.authservice.security;

import com.shoaib.authservice.entity.RefreshToken;
import com.shoaib.authservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Duration REFRESH_TOKEN_EXPIRATION =
            Duration.ofDays(30);

    private static final int REFRESH_TOKEN_BYTES = 32;

    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom =
            new SecureRandom();

    @Transactional
    public String createRefreshToken(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }

        String rawRefreshToken =
                generateSecureToken();

        String tokenHash =
                hashToken(rawRefreshToken);

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plus(REFRESH_TOKEN_EXPIRATION);

        RefreshToken refreshToken =
                RefreshToken.create(
                        userId,
                        tokenHash,
                        expiresAt
                );

        refreshTokenRepository.save(refreshToken);

        return rawRefreshToken;
    }

    @Transactional
    public String createRefreshToken(UUID userId, Duration duration) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }

        String rawRefreshToken =
                generateSecureToken();

        String tokenHash =
                hashToken(rawRefreshToken);

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plus(duration);

        RefreshToken refreshToken =
                RefreshToken.create(
                        userId,
                        tokenHash,
                        expiresAt
                );

        refreshTokenRepository.save(refreshToken);

        return rawRefreshToken;
    }

    @Transactional
    public UUID validateAndConsumeRefreshToken(
            String rawRefreshToken
    ) {
        validateRawToken(rawRefreshToken);

        String tokenHash =
                hashToken(rawRefreshToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHashForUpdate(tokenHash)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token is invalid"
                                )
                        );

        if (refreshToken.isExpired()) {
            throw new RuntimeException(
                    "Refresh token has expired"
            );
        }

        if (refreshToken.isRevoked()) {
            throw new RuntimeException(
                    "Refresh token has been revoked"
            );
        }

        if (refreshToken.isConsumed()) {
            throw new RuntimeException(
                    "Refresh token has already been used"
            );
        }

        refreshToken.consume();

        refreshTokenRepository.save(refreshToken);

        return refreshToken.getUserId();
    }

    @Transactional
    public void revokeRefreshToken(
            String rawRefreshToken
    ) {
        if (
                rawRefreshToken == null
                        || rawRefreshToken.isBlank()
        ) {
            return;
        }

        String tokenHash =
                hashToken(rawRefreshToken);

        refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .ifPresent(refreshToken -> {
                    refreshToken.revoke();
                    refreshTokenRepository.save(refreshToken);
                });
    }

    @Transactional
    public void revokeAllUserRefreshTokens(
            UUID userId
    ) {
        if (userId == null) {
            return;
        }

        refreshTokenRepository
                .revokeAllActiveTokensByUserId(
                        userId,
                        LocalDateTime.now()
                );
    }

    @Transactional
    public int deleteExpiredTokens() {
        return refreshTokenRepository.deleteExpiredTokens(
                LocalDateTime.now()
        );
    }

    private void validateRawToken(
            String rawRefreshToken
    ) {
        if (
                rawRefreshToken == null
                        || rawRefreshToken.isBlank()
        ) {
            throw new RuntimeException(
                    "Refresh token is required"
            );
        }
    }

    private String generateSecureToken() {
        byte[] randomBytes =
                new byte[REFRESH_TOKEN_BYTES];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private String hashToken(
            String rawToken
    ) {
        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    messageDigest.digest(
                            rawToken.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat.of()
                    .formatHex(hash);

        } catch (
                NoSuchAlgorithmException exception
        ) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable",
                    exception
            );
        }
    }
}