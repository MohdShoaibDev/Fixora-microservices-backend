package com.shoaib.orderservice.client;

import com.shoaib.cart.ClientCartItemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "cart-client",
        url = "${services.cart-service.url}")
public interface CartClient {
    @GetMapping("internal/cart/get-cart")
    List<ClientCartItemRequest> getCartItem(@RequestHeader("X-User-Id") UUID userId);
}
