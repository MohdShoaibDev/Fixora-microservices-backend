package com.shoaib.cartservice.exception;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.cart.ClientCartItemRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex){
        return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public List<ClientCartItemRequest> handleRuntimeException(CartItemNotFoundException ex){
        return new ArrayList<>();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        false,
                        "Validation failed",
                        errors
                )
        );
    }

}
