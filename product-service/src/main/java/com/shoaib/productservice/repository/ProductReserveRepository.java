package com.shoaib.productservice.repository;

import com.shoaib.productservice.entity.ReserveProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductReserveRepository extends JpaRepository<ReserveProduct, UUID> {
}
