package com.shoaib.productservice.exception;

public class ProductInactiveException extends RuntimeException {
    public ProductInactiveException() { super("Product is inactive"); }
}
