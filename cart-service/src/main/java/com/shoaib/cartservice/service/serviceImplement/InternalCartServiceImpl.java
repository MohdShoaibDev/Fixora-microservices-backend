package com.shoaib.cartservice.service.serviceImplement;

import com.shoaib.cart.ClientCartItemRequest;
import com.shoaib.cartservice.entity.Cart;
import com.shoaib.cartservice.entity.CartItem;
import com.shoaib.cartservice.exception.CartItemNotFoundException;
import com.shoaib.cartservice.mapper.Mapper;
import com.shoaib.cartservice.repository.CartItemRepository;
import com.shoaib.cartservice.repository.CartRepository;
import com.shoaib.cartservice.service.serviceInterface.InternalCartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InternalCartServiceImpl implements InternalCartService {

    private final CartRepository cartRepository;
    private final CartItemRepository  cartItemRepository;

    @Override
    @Transactional
    public List<ClientCartItemRequest> getCartItems(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() ->
                new CartItemNotFoundException("Cart Item not found"));
        List<CartItem> cartItemList = cartItemRepository.findByCartId(cart.getId());
        return cartItemList.stream().map(Mapper::toClientCartItemRequest).toList();
    }
}
