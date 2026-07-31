package com.shoaib.authservice.service.authService;

import com.shoaib.authservice.dto.*;

import java.util.UUID;

public interface AuthService {
    void registerUser(UserRegisterDto registerRequestDto);
    AuthApiResponseDto<UserResponseDto> login(UserLoginDto userLoginDto);
    void logout(String token,String refreshToken);
    void forgotPassword(ResentOtpRequestDto resentOtpRequestDto);
    AuthApiResponseDto<Object> verifyForgotPasswordOtp(VerifyOtpRequestDto verifyOtpRequestDto);
    void forgotPasswordReset(PasswordResetDto passwordResetDto, String token);
}
