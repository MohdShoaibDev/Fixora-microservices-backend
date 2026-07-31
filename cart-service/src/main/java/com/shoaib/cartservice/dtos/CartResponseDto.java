package com.shoaib.cartservice.dtos;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record CartResponseDto(
        UUID id,
        List<CartItemResponseDto> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
