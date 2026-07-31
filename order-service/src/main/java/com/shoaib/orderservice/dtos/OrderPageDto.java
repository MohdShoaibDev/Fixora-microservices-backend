package com.shoaib.orderservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageDto<T> {
    private T data;
    private int page;
    private Long totalElements;
    private int totalPages;
}
