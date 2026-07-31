package com.shoaib.productservice.controller.product.nonPublicController;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.productDtos.ProductClientDto;
import com.shoaib.productservice.dtos.ProductRequestDto;
import com.shoaib.productservice.service.productService.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/")
public class ProductController {

    private final ProductServiceImpl productServiceImpl;

    @PostMapping("add-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> addProduct(@RequestBody @Valid ProductRequestDto productRequestDto){
        productServiceImpl.addProduct(productRequestDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product created successfully", null));
    }

}
