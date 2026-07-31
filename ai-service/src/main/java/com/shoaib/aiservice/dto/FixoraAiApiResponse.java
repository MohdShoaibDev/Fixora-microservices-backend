package com.shoaib.aiservice.dto;

import com.shoaib.aiservice.util.FixoraCategory;
import com.shoaib.aiservice.util.RequestType;
import com.shoaib.aiservice.util.Urgency;
import java.util.List;
import java.util.UUID;

public record FixoraAiApiResponse(RequestType requestType, boolean supportedByFixora,
        FixoraCategory category,
        UUID categoryId,
        String title,
        String summary,
        List<String> possibleCauses,
        List<String> safeActions,
        String warning,
        Urgency urgency,
        boolean requiresProfessional,
        double confidence,
        AiActionResponse action) {}

