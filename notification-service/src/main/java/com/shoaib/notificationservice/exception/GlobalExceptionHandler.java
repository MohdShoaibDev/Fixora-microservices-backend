package com.shoaib.notificationservice.exception;

import com.shoaib.apiResponse.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex){
        return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(EmailSendingException.class)
    public void handleRuntimeException(EmailSendingException ex){
        System.out.println(ex.getMessage());
    }

}
