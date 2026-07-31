package com.shoaib.productservice.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotBlank(message = "Name cannot be null")
    private String name;

    @NotBlank(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Max(value = 20000, message = "Price cannot be more than 20000")
    @Min(value = 100, message = "Price cannot be less than 100")
    private BigDecimal price;

    @NotNull(message = "Price cannot be null")
    private Integer stock;

    @NotNull(message = "Estimated duration cannot be null, time must be in minutes")
    private Integer estimatedDurationInMinutes;

    private Boolean active = true;

    @NotNull(message = "CategoryId cannot be null")
    private UUID categoryId;

    @NotNull(message = "ThumbnailUrl cannot be null")
    private String thumbnailUrl;

    @Builder.Default
    private boolean generalPurpose = false;
}
