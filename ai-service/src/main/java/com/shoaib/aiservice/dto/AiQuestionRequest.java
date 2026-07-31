package com.shoaib.aiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiQuestionRequest(@NotBlank @Size(min = 3, max = 2000) String question) {
    public String normalizedQuestion() { return question.strip().replaceAll("\\s+", " "); }
}
