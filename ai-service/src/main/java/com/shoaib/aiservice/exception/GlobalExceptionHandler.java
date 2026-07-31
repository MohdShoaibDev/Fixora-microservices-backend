package com.shoaib.aiservice.exception;

import com.shoaib.aiservice.client.GeminiClientException;
import com.shoaib.apiResponse.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream().findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).orElse("Invalid request");
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiResponse<Object>> typeMismatch(MethodArgumentTypeMismatchException ex) {
        return error(HttpStatus.BAD_REQUEST, "Invalid " + ex.getName());
    }

    @ExceptionHandler(GeminiClientException.class)
    ResponseEntity<ApiResponse<Object>> gemini(GeminiClientException ex) {
        log.warn("Gemini request failed: reason={}", ex.getReason(), ex);
        HttpStatus status = ex.getReason() == GeminiClientException.Reason.UNAVAILABLE
                ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.BAD_GATEWAY;
        String message = status == HttpStatus.SERVICE_UNAVAILABLE ? "AI service is temporarily unavailable" : "AI provider returned an invalid response";
        return error(status, message);
    }

    @ExceptionHandler({InvalidAiResponseException.class, CategoryResolutionException.class})
    ResponseEntity<ApiResponse<Object>> invalidAi(RuntimeException ex) {
        log.error("AI response could not be safely mapped: {}", ex.getMessage());
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "AI response could not be processed safely");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Object>> unexpected(Exception ex) {
        log.error("Unexpected AI service error", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<ApiResponse<Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
    }
}
