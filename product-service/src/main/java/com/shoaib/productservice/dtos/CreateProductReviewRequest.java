package com.shoaib.productservice.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductReviewRequest(
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating cannot be greater than 5") Integer rating,
        @NotBlank(message = "Comment is required")
        @Size(min = 5, max = 1000, message = "Comment must contain between 5 and 1000 characters") String comment
) {}
