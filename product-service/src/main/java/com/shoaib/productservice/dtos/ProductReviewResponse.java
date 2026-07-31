package com.shoaib.productservice.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductReviewResponse(UUID id, UUID productId, UUID userId, String userName, Integer rating,
                                    String comment, LocalDateTime createdAt, LocalDateTime updatedAt) {}
