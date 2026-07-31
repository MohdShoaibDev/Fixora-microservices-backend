package com.shoaib.orderservice.dtos;

import java.util.UUID;

public record OrderRequestDto(
        UUID addressId
) {

}
