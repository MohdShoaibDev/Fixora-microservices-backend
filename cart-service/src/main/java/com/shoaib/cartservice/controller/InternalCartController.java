package com.shoaib.cartservice.controller;

import com.shoaib.cart.ClientCartItemRequest;
import com.shoaib.cartservice.service.serviceInterface.CartService;
import com.shoaib.cartservice.service.serviceInterface.InternalCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("internal")
@RequiredArgsConstructor
public class InternalCartController {

    private final InternalCartService internalCartService;

    @GetMapping("/cart/get-cart")
    public List<ClientCartItemRequest> getCartItems(@RequestHeader("X-User-Id") UUID userId) {
        return internalCartService.getCartItems(userId);
    }

}
