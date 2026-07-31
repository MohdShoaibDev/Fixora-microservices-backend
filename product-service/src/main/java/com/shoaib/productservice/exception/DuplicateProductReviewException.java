package com.shoaib.productservice.exception;

public class DuplicateProductReviewException extends RuntimeException {
    public DuplicateProductReviewException() { super("You have already reviewed this product"); }
    public DuplicateProductReviewException(Throwable cause) { super("You have already reviewed this product", cause); }
}
