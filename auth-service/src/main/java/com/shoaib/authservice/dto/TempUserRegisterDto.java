package com.shoaib.authservice.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempUserRegisterDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UserRegisterDto userRegisterDto;
    private String otp;
}
