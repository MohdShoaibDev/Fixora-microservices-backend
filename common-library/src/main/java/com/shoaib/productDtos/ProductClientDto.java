package com.shoaib.productDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductClientDto {
    private UUID productId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private String productImageUrl;
    private int quantity;
    private boolean available;
}
