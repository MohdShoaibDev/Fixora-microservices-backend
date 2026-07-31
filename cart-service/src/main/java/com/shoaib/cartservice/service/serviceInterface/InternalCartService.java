package com.shoaib.cartservice.service.serviceInterface;

import com.shoaib.cart.ClientCartItemRequest;

import java.util.List;
import java.util.UUID;

public interface InternalCartService {
    List<ClientCartItemRequest>  getCartItems(UUID cartId);
}
