package com.shoaib.apiResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageApiResponse<T> {
    private boolean status;
    private String message;
    private T data;
    private int page;
    private long totalProducts;
    private int totalPages;
}

