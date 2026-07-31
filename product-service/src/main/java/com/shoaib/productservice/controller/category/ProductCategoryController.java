package com.shoaib.productservice.controller.category;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.productservice.dtos.ProductCategoryRequestDto;
import com.shoaib.productservice.service.productCategory.ProductCategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("products/")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryServiceImpl productServiceImpl;

    @PostMapping("add-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> addCategory(@RequestBody ProductCategoryRequestDto categoryDto) {
        productServiceImpl.addCategory(categoryDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category added successfully", null));
    }

    @DeleteMapping("delete-category/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable UUID categoryId) {
        productServiceImpl.deleteCategory(categoryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category deleted successfully", null));
    }

}
