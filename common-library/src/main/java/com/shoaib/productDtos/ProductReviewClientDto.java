package com.shoaib.productDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewClientDto {
    private UUID reviewId;
    private Boolean isReviewed;
    private Integer review;
    private String comment;
}
