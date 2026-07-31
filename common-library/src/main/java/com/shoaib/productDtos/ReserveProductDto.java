package com.shoaib.productDtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReserveProductDto {

    @NotNull
    private String productName;

    @NotNull
    private String productDescription;

    @NotNull
    private String productImage;

    @NotNull
    private BigDecimal price;

    public static ReserveProductDto createReserveProductDto(String productName, String productDescription,
                                                            String productImage, BigDecimal price) {
        return  new ReserveProductDto(productName,productDescription,productImage,price);
    }

}
