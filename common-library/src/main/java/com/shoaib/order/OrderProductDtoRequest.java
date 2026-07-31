package com.shoaib.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDtoRequest<T> {
    private UUID orderId;
    private T data;
}
