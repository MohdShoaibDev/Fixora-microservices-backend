package com.shoaib.cartservice.service.serviceInterface;


import com.shoaib.cartservice.dtos.AddItemCartDto;
import com.shoaib.cartservice.dtos.CartResponseDto;
import com.shoaib.responseDto.PageResponseDto;

import java.util.UUID;

public interface CartService {

    void addItem(UUID userId, AddItemCartDto  addItemCartDto);

    void deleteItem(UUID userId, UUID productId);

    void clearCart(UUID userId);

    PageResponseDto<CartResponseDto> getCart(UUID userId, int page);
}