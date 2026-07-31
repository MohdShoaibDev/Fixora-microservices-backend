package com.shoaib.aiservice.controller;

import com.shoaib.aiservice.dto.AiQuestionRequest;
import com.shoaib.aiservice.dto.FixoraAiApiResponse;
import com.shoaib.aiservice.service.AiAssistantService;
import com.shoaib.apiResponse.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiAssistantController {
    private final AiAssistantService aiAssistantService;

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<FixoraAiApiResponse>> ask(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody AiQuestionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "AI response generated successfully",
                aiAssistantService.ask(userId, request)));
    }
}
