package com.shoaib.paymentservice.exception;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.payment.RazorpayOrderIdResponse;
import com.shoaib.util.enums.Currency;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex){
        return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(RazorpayOrderIdCreationException.class)
    public RazorpayOrderIdResponse handleRazorpayOrderIdCreationException(RazorpayOrderIdCreationException ex){
        return new RazorpayOrderIdResponse(null,null, Currency.UNKNOWN,null, LocalDateTime.now());
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
