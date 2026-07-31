package com.shoaib.authservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Enter a valid 10-digit mobile number"
    )
    @NotNull
    private String number;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
    private String fullname;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;
}