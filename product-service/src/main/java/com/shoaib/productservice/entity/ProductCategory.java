package com.shoaib.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_category")
@Getter
@Setter
@Builder
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

}
