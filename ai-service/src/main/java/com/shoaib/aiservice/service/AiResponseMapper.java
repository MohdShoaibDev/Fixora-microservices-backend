package com.shoaib.aiservice.service;

import com.shoaib.aiservice.category.CategoryResolver;
import com.shoaib.aiservice.dto.*;
import com.shoaib.aiservice.exception.InvalidAiResponseException;
import com.shoaib.aiservice.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AiResponseMapper {
    private static final Pattern FORBIDDEN_VALUE = Pattern.compile("(?i)([0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}|(?:price|discount|payment|appointment slot|product id|technician id)\\s*[:=]|[$₹€£]\\s*\\d)");
    private final CategoryResolver categoryResolver;

    public FixoraAiApiResponse toApiResponse(FixoraAiResponse ai) {
        validate(ai);
        UUID categoryId = null;
        AiActionResponse action;
        if (ai.requestType() == RequestType.EMERGENCY || ai.urgency() == Urgency.EMERGENCY) {
            action = hidden(AiActionType.CONTACT_EMERGENCY);
        } else if (ai.requestType() == RequestType.CLARIFICATION_REQUIRED) {
            action = hidden(AiActionType.ASK_CLARIFICATION);
        } else if (ai.requestType() == RequestType.GENERAL_QUESTION || ai.category() == FixoraCategory.GENERAL_INFORMATION) {
            action = hidden(AiActionType.SHOW_INFORMATION);
        } else if (!ai.supportedByFixora() || ai.category() == FixoraCategory.UNSUPPORTED) {
            action = hidden(AiActionType.NONE);
        } else if (ai.bookingRecommended()) {
            categoryId = categoryResolver.resolve(ai.category());
            action = new AiActionResponse(AiActionType.BOOK_CATEGORY, true,
                    "Book " + ai.category().friendlyName(), categoryId);
        } else action = hidden(AiActionType.NONE);

        return new FixoraAiApiResponse(ai.requestType(), ai.supportedByFixora(), ai.category(), categoryId,
                ai.title(), ai.summary(), ai.possibleCauses(), ai.safeActions(), ai.warning(), ai.urgency(),
                ai.requiresProfessional(), ai.confidence(), action);
    }

    void validate(FixoraAiResponse ai) {
        if (ai == null || ai.requestType() == null || ai.category() == null || ai.urgency() == null)
            invalid("AI response is missing required fields");
        if (ai.confidence() < 0 || ai.confidence() > 1) invalid("AI confidence must be between 0 and 1");
        if (!ai.supportedByFixora() && ai.category() != FixoraCategory.UNSUPPORTED)
            invalid("Unsupported responses must use the UNSUPPORTED category");
        if ((!ai.supportedByFixora() || ai.category() == FixoraCategory.UNSUPPORTED) && ai.bookingRecommended())
            invalid("Unsupported responses cannot recommend a booking");
        if (ai.bookingRecommended() && (ai.bookingMessage() == null || ai.bookingMessage().isBlank()))
            invalid("A booking recommendation requires a booking message");
        if (ai.possibleCauses() == null || ai.safeActions() == null || ai.possibleCauses().size() > 5 || ai.safeActions().size() > 5)
            invalid("AI response arrays are missing or too large");
        List<String> text = List.of(ai.title(), ai.summary(), ai.warning(), ai.bookingMessage() == null ? "" : ai.bookingMessage());
        if (text.stream().anyMatch(s -> s == null || FORBIDDEN_VALUE.matcher(s).find()) ||
                ai.possibleCauses().stream().anyMatch(s -> s == null || FORBIDDEN_VALUE.matcher(s).find()) ||
                ai.safeActions().stream().anyMatch(s -> s == null || FORBIDDEN_VALUE.matcher(s).find()))
            invalid("AI response contains prohibited identifiers or commercial values");
    }

    private AiActionResponse hidden(AiActionType type) { return new AiActionResponse(type, false, null, null); }
    private void invalid(String message) { throw new InvalidAiResponseException(message); }
}
