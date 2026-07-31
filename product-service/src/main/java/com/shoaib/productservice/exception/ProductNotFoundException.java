package com.shoaib.productservice.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() { super("Product not found"); }
}
