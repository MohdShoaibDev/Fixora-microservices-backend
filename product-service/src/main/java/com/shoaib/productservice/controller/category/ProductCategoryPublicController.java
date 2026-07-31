package com.shoaib.productservice.controller.category;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.productservice.service.productCategory.ProductCategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("public/products")
public class ProductCategoryPublicController {

    private final ProductCategoryServiceImpl productCategoryServiceImpl;

    @GetMapping("product-categories")
    public ResponseEntity<ApiResponse<Object>> getCategories() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Category added successfully",
                productCategoryServiceImpl.getCategories()));
    }
}
