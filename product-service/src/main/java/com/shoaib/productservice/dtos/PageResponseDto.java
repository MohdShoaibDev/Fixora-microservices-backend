package com.shoaib.productservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto {
    List<ProductResponseDto> data;
    int page;
    long totalElements;
    int totalPages;
}
