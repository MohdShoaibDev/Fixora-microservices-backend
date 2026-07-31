package com.shoaib.aiservice.service;

import com.shoaib.aiservice.client.GeminiClient;
import com.shoaib.aiservice.dto.AiQuestionRequest;
import com.shoaib.aiservice.dto.FixoraAiApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiAssistantServiceImpl implements AiAssistantService {
    private final GeminiClient geminiClient;
    private final AiResponseMapper responseMapper;

    @Override public FixoraAiApiResponse ask(UUID userId, AiQuestionRequest request) {
        return responseMapper.toApiResponse(geminiClient.ask(request.normalizedQuestion()));
    }
}
