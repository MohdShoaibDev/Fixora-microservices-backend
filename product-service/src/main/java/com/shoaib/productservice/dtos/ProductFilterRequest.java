package com.shoaib.productservice.dtos;

import com.shoaib.productservice.utility.SortDirection;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {
    private UUID categoryId;

    @PositiveOrZero(message = "Page number cannot be negative")
    private Integer page;

    private SortDirection sortBy;

    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot be greater than 5")
    private Double minRating;
}
