package com.shoaib.productservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer estimatedDurationInMinutes;

    private UUID categoryId;

    private String thumbnailUrl;

    private Double rating;

    private Integer totalReviews;

    private Integer totalBookings;

    private List<ProductReviewResponse> productReviewResponseList;
}

