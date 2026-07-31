package com.shoaib.aiservice.service;

import com.shoaib.aiservice.dto.AiQuestionRequest;
import com.shoaib.aiservice.dto.FixoraAiApiResponse;
import java.util.UUID;

public interface AiAssistantService {
    FixoraAiApiResponse ask(UUID userId, AiQuestionRequest request);
}
