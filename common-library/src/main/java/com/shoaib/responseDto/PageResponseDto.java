package com.shoaib.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    T data;
    int page;
    long totalProducts;
    int totalPages;
}
