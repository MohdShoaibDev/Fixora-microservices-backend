package com.shoaib.productservice.service.recentView;

import com.shoaib.productservice.dtos.ProductResponseDto;

import java.util.List;
import java.util.UUID;

public interface RecentViewProductService {
    void recentViewProduct(UUID userId, UUID productId);
    List<ProductResponseDto> getRecentView(UUID uuid);
}
