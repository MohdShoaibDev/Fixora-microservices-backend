package com.shoaib.aiservice.service;

import com.shoaib.aiservice.category.CategoryResolver;
import com.shoaib.aiservice.dto.FixoraAiResponse;
import com.shoaib.aiservice.exception.InvalidAiResponseException;
import com.shoaib.aiservice.util.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AiResponseMapperTest {
    private final RecordingResolver resolver = new RecordingResolver();
    private final AiResponseMapper mapper = new AiResponseMapper(resolver);

    @Test void acIssueMapsToAcTechnicianAndResolvesCategory() {
        UUID id = UUID.randomUUID();
        resolver.id = id;
        var result = mapper.toApiResponse(response(RequestType.SERVICE_REQUEST, true,
                FixoraCategory.AC, Urgency.MEDIUM, true, "Book an AC technician", .96));
        assertEquals(FixoraCategory.AC, result.category());
        assertEquals(AiActionType.BOOK_CATEGORY, result.action().type());
        assertEquals(id, result.categoryId());
    }

    @Test void carIssueIsUnsupportedAndCannotBook() {
        var result = mapper.toApiResponse(response(RequestType.UNSUPPORTED_SERVICE, false,
                FixoraCategory.UNSUPPORTED, Urgency.LOW, false, null, .95));
        assertEquals(AiActionType.NONE, result.action().type());
        assertNull(result.categoryId());
        assertEquals(0, resolver.calls);
    }

    @Test void unsupportedResponseCannotGenerateBookingAction() {
        var ai = response(RequestType.UNSUPPORTED_SERVICE, false, FixoraCategory.UNSUPPORTED,
                Urgency.LOW, true, "Book now", .9);
        assertThrows(InvalidAiResponseException.class, () -> mapper.toApiResponse(ai));
    }

    @Test void bookingRecommendationRequiresBookingMessage() {
        var ai = response(RequestType.SERVICE_REQUEST, true, FixoraCategory.PLUMBER,
                Urgency.MEDIUM, true, " ", .9);
        assertThrows(InvalidAiResponseException.class, () -> mapper.toApiResponse(ai));
    }

    @Test void confidenceOutsideRangeIsRejected() {
        var ai = response(RequestType.GENERAL_QUESTION, true, FixoraCategory.GENERAL_INFORMATION,
                Urgency.LOW, false, null, 1.1);
        assertThrows(InvalidAiResponseException.class, () -> mapper.toApiResponse(ai));
    }

    @Test void emergencyGeneratesContactEmergency() {
        var result = mapper.toApiResponse(response(RequestType.EMERGENCY, true,
                FixoraCategory.ELECTRICIAN, Urgency.EMERGENCY, false, null, .99));
        assertEquals(AiActionType.CONTACT_EMERGENCY, result.action().type());
        assertFalse(result.action().visible());
        assertEquals(0, resolver.calls);
    }

    @Test void categoryUuidIsNotResolvedForSupportedNonBookingResponse() {
        var result = mapper.toApiResponse(response(RequestType.SERVICE_REQUEST, true,
                FixoraCategory.CARPENTER, Urgency.LOW, false, null, .8));
        assertNull(result.categoryId());
        assertEquals(0, resolver.calls);
    }

    @Test void categoryUuidIsNotResolvedForGeneralInformationResponse() {
        var result = mapper.toApiResponse(response(RequestType.GENERAL_QUESTION, true,
                FixoraCategory.GENERAL_INFORMATION, Urgency.LOW, false, null, .9));
        assertEquals(AiActionType.SHOW_INFORMATION, result.action().type());
        assertNull(result.categoryId());
        assertEquals(0, resolver.calls);
    }

    private FixoraAiResponse response(RequestType type, boolean supported, FixoraCategory category,
            Urgency urgency, boolean booking, String bookingMessage, double confidence) {
        return new FixoraAiResponse(type, supported, category, "Title", "Summary", List.of("Cause"),
                List.of("Safe action"), "Warning", urgency, true, booking, bookingMessage, confidence);
    }

    private static final class RecordingResolver implements CategoryResolver {
        UUID id;
        int calls;
        @Override public UUID resolve(FixoraCategory category) { calls++; return id; }
    }
}
