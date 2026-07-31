package com.shoaib.orderservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productDescription;
    private String productImage;
    private Integer quantity;
    private BigDecimal price;
    @JsonProperty("isReviewed")
    private boolean reviewed;
    private UUID reviewId;
    private Integer review;
    private String comment;
    private LocalDateTime  createdAt;
    private LocalDateTime updatedAt;
}
