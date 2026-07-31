package com.shoaib.productservice.controller.product.nonPublicController;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.productservice.dtos.ProductResponseDto;
import com.shoaib.productservice.service.recentView.RecentViewProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class RecentViewProductController {

    private final RecentViewProductServiceImpl recentViewProductImpl;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("recent-view")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getRecentView(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Recent view product fetch successfully",
                recentViewProductImpl.getRecentView(userId)));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("recent-view/{productId}")
    public void addRecentViewProduct(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID productId) {
        recentViewProductImpl.recentViewProduct(userId, productId);
    }

}
