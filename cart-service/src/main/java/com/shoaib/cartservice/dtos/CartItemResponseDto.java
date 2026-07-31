package com.shoaib.cartservice.dtos;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CartItemResponseDto(
        UUID id,
        UUID productId,
        String name,
        BigDecimal price,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}