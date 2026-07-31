package com.shoaib.cartservice.mapper;

import com.shoaib.cart.ClientCartItemRequest;
import com.shoaib.cartservice.dtos.CartItemResponseDto;
import com.shoaib.cartservice.entity.CartItem;
import com.shoaib.productDtos.ProductClientDto;

public class Mapper {
    private Mapper(){}

    public static CartItemResponseDto toCartItemResponseDto(CartItem cartItem, ProductClientDto  productClientDto){
        return CartItemResponseDto.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProductId())
                .name(productClientDto.getProductName())
                .description(productClientDto.getProductDescription())
                .imageUrl(productClientDto.getProductImageUrl())
                .price(productClientDto.getProductPrice())
                .createdAt(cartItem.getCreatedAt())
                .updatedAt(cartItem.getUpdatedAt())
                .build();
    }

    public static ClientCartItemRequest toClientCartItemRequest(CartItem cartItem){
        return ClientCartItemRequest.builder()
                .productId(cartItem.getProductId())
                .build();
    }
}
