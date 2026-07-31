package com.shoaib.cartservice.controller;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.apiResponse.PageApiResponse;
import com.shoaib.cartservice.dtos.AddItemCartDto;
import com.shoaib.cartservice.dtos.CartResponseDto;
import com.shoaib.cartservice.service.serviceInterface.CartService;
import com.shoaib.responseDto.PageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("get-cart")
    ResponseEntity<PageApiResponse<CartResponseDto>> getCart(@RequestHeader("X-User-Id") UUID id,
                                                                                 @RequestParam(defaultValue = "1") int page) {

        PageResponseDto<CartResponseDto> serviceResponse =  cartService.getCart(id, page);
        PageApiResponse<CartResponseDto> response = PageApiResponse.<CartResponseDto>builder()
                .data(serviceResponse.getData())
                .page(serviceResponse.getPage())
                .totalProducts(serviceResponse.getTotalProducts())
                .totalPages(serviceResponse.getTotalPages())
                .message("Cart fetch successfully")
                .status(true)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("add-item")
    ResponseEntity<ApiResponse<Object>> addItem(@RequestHeader("X-User-Id") UUID id,
                                                @RequestBody @Valid AddItemCartDto addItemCartDto) {
        cartService.addItem(id, addItemCartDto);
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Cart updated successfully", null));
    }

    @PostMapping("delete-item")
    ResponseEntity<ApiResponse<Object>> deleteItem(@RequestHeader("X-User-Id") UUID id,
                                                   @RequestBody AddItemCartDto addItemCartDto) {
        cartService.deleteItem(id, addItemCartDto.getProductId());
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Cart updated successfully", null));
    }

    @PostMapping("clear-cart")
    ResponseEntity<ApiResponse<Object>> clearCart(@RequestHeader("X-User-Id") UUID id) {
        cartService.clearCart(id);
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Cart updated successfully", null));
    }

}
