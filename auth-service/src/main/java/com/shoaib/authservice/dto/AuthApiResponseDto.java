package com.shoaib.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthApiResponseDto<T> {
    private Boolean status;
    private String message;
    private T data;
    private String token;
    private String refreshToken;
}
