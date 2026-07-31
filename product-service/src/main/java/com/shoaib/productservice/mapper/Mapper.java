package com.shoaib.productservice.mapper;

import com.shoaib.productDtos.ProductClientDto;
import com.shoaib.productservice.dtos.ProductCategoryRequestDto;
import com.shoaib.productservice.dtos.ProductCategoryResponseDto;
import com.shoaib.productservice.dtos.ProductRequestDto;
import com.shoaib.productservice.dtos.ProductResponseDto;
import com.shoaib.productservice.entity.Product;
import com.shoaib.productservice.entity.ProductCategory;

public final class Mapper {

    private Mapper(){}

    public static ProductResponseDto mapProductToProductResponseDto(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .estimatedDurationInMinutes(product.getEstimatedDurationInMinutes())
                .categoryId(product.getCategoryId())
                .thumbnailUrl(product.getThumbnailUrl())
                .rating(product.getTotalRating())
                .totalReviews(product.getTotalReviews())
                .totalBookings(product.getTotalBookings())
                .build();
    }

    public static ProductClientDto mapProductToProductClientDto(Product product) {
        if (product == null) {
            return null;
        }

        return ProductClientDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .productPrice(product.getPrice())
                .productImageUrl(product.getThumbnailUrl())
                .quantity(product.getStock())
                .build();
    }

    public static ProductCategory mapProductCategoryDtoToProductCategory(ProductCategoryRequestDto productCategoryDto) {
        return ProductCategory.builder()
                .name(productCategoryDto.getName().toLowerCase())
                .icon("")
                .build();
    }

    public static ProductCategoryResponseDto mapProductCategoryToProductCategoryResponseDto(ProductCategory productCategory) {
        return ProductCategoryResponseDto.builder()
                .id(productCategory.getId())
                .icon(productCategory.getIcon())
                .name(productCategory.getName())
                .build();
    }

    public static Product mapProductRequestDtoToProduct(ProductRequestDto productRequestDto) {
        return Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .stock(productRequestDto.getStock())
                .estimatedDurationInMinutes(productRequestDto.getEstimatedDurationInMinutes())
                .active(true)
                .categoryId(productRequestDto.getCategoryId())
                .thumbnailUrl(productRequestDto.getThumbnailUrl())
                .build();
    }
}
