package com.shoaib.cartservice.service.serviceImplement;


import com.shoaib.cartservice.client.ProductClient;
import com.shoaib.cartservice.dtos.AddItemCartDto;
import com.shoaib.cartservice.dtos.CartItemResponseDto;
import com.shoaib.cartservice.dtos.CartResponseDto;
import com.shoaib.cartservice.entity.Cart;
import com.shoaib.cartservice.entity.CartItem;
import com.shoaib.cartservice.mapper.Mapper;
import com.shoaib.cartservice.repository.CartItemRepository;
import com.shoaib.cartservice.repository.CartRepository;
import com.shoaib.cartservice.service.serviceInterface.CartService;
import com.shoaib.productDtos.ProductClientDto;
import com.shoaib.productDtos.ProductIdListDto;
import com.shoaib.responseDto.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient  productClient;

    @Override
    @Transactional
    public void addItem(UUID userId, AddItemCartDto addItemCartDto) {
        cartRepository.createCartIfAbsent(userId, addItemCartDto.getBookingDate(), addItemCartDto.getBookingTime());

        Cart cart = cartRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Failed to create or retrieve cart for user: "
                                        + userId
                        )
                );
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),addItemCartDto.getProductId())
                        .orElseGet(() -> CartItem.builder()
                                .cartId(cart.getId())
                                .productId(addItemCartDto.getProductId())
                                .build());
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void deleteItem(UUID userId, UUID productId) {

        Cart cart = cartRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found for user: " + userId)
                );

        int deleted =
                cartItemRepository.deleteByCartIdAndProductId(
                        cart.getId(),
                        productId
                );

        if (deleted == 0) {
            throw new RuntimeException("Cart item not found for product: " + productId);
        }
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        if (userId == null) {
            throw new RuntimeException("User ID cannot be null");
        }

        Cart cart = cartRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found for user: " + userId)
                );

        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<CartResponseDto> getCart(UUID userId, int page) {
        if (userId == null) {
            throw new RuntimeException("User ID cannot be null");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart is empty")
                );

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CartItem> cartItems =
                cartItemRepository.findAllByCartId(
                        cart.getId(),
                        pageable
                );

        if(!cartItems.hasContent()) {
            return PageResponseDto.<CartResponseDto>builder()
                    .data(null)
                    .page(cartItems.getNumber() + 1)
                    .totalProducts(cartItems.getTotalElements())
                    .totalPages(cartItems.getTotalPages())
                    .build();
        }

        HashMap<UUID, ProductClientDto> map = getProductsDetails(cartItems.stream().map(CartItem::getProductId).toList());
        List<CartItemResponseDto> itemDtos = cartItems.getContent().stream()
                .map(cartItem -> Mapper.toCartItemResponseDto(cartItem,map.get(cartItem.getProductId())))
                .toList();

        var data = CartResponseDto.builder()
                .id(cart.getId())
                .items(itemDtos)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();

        return PageResponseDto.<CartResponseDto>builder()
                .data(data)
                .page(cartItems.getNumber() + 1)
                .totalProducts(cartItems.getTotalElements())
                .totalPages(cartItems.getTotalPages())
                .build();
    }

    private HashMap<UUID, ProductClientDto> getProductsDetails(List<UUID> productIds) {
        List<ProductClientDto> list = productClient.getProducts(new ProductIdListDto(productIds));
        HashMap<UUID, ProductClientDto> map = new HashMap<>();
        list.forEach(productClientDto -> {
            map.put(productClientDto.getProductId(), productClientDto);
        });
        return map;
    }
}