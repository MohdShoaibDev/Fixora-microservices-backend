package com.shoaib.cartservice.client;

import com.shoaib.productDtos.ProductClientDto;
import com.shoaib.productDtos.ProductIdListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-client",
url = "${services.product-service.url}")
public interface ProductClient {

    @GetMapping("public/internal/products")
    List<ProductClientDto> getProducts(@RequestBody ProductIdListDto productIdListDto);

}
