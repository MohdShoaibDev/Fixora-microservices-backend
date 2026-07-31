package com.shoaib.aiservice.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(@NotBlank String baseUrl, @NotBlank String apiKey,
        @NotBlank String model, @NotNull Duration connectTimeout, @NotNull Duration readTimeout,
        @Min(1) int maxOutputTokens, @DecimalMin("0.0") @DecimalMax("2.0") double temperature) {}
