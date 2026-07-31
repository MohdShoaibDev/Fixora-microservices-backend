package com.shoaib.productservice.exception;

public class ProductReviewNotFoundException extends RuntimeException {
    public ProductReviewNotFoundException() { super("Review not found"); }
}
