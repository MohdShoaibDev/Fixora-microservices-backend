package com.shoaib.aiservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shoaib.aiservice.util.FixoraCategory;
import com.shoaib.aiservice.util.RequestType;
import com.shoaib.aiservice.util.Urgency;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = false)
public record FixoraAiResponse(RequestType requestType, boolean supportedByFixora,
        FixoraCategory category, String title, String summary, List<String> possibleCauses,
        List<String> safeActions, String warning, Urgency urgency, boolean requiresProfessional,
        boolean bookingRecommended, String bookingMessage, double confidence) {}
