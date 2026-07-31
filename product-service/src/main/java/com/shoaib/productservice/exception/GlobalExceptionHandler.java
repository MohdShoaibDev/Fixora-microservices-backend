package com.shoaib.productservice.exception;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.productDtos.ReserveProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleProductNotFound(ProductNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProductReviewNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleReviewNotFound(ProductReviewNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({ProductInactiveException.class, InvalidPaginationException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(RuntimeException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DuplicateProductReviewException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateReview(RuntimeException ex) {
        return error(HttpStatus.CONFLICT, "You have already reviewed this product");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Validation failed", errors));
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResponse<Object>> handleMalformedRequest(Exception ex) {
        return error(HttpStatus.BAD_REQUEST, "Request contains missing or invalid values");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex){
        return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(ProductStockException.class)
    public HashMap<UUID, ReserveProductDto> handleProductStockException(ProductStockException ex){
        return new HashMap<>();
    }

    private ResponseEntity<ApiResponse<Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
    }
}
