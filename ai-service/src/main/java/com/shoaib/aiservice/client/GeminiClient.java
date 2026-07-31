package com.shoaib.aiservice.client;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.shoaib.aiservice.config.GeminiProperties;
import com.shoaib.aiservice.dto.FixoraAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiClient {
    private static final String SYSTEM_INSTRUCTION = """
            You are Fixora's home-service diagnostic assistant. Classify the question and select exactly one allowed category; never invent categories. Automobile, medical, legal, financial, and unrelated requests are unsupported. Provide only safe, low-risk user actions. Never recommend touching live wires, opening electrical panels or refrigerant lines, or disassembling gas equipment. Fire, smoke, sparking, gas smell, electric shock, flooding near electricity, or immediate danger is EMERGENCY. Unsupported requests must use supportedByFixora=false, category=UNSUPPORTED, and bookingRecommended=false. Return JSON only, no markdown or outside text. Keep arrays concise and confidence between 0 and 1. Never output UUIDs, prices, product or technician IDs, appointment slots, discounts, or payment values.
            """;

    private final RestClient geminiRestClient;
    private final GeminiProperties properties;
    private final ObjectMapper objectMapper;

    public FixoraAiResponse ask(String question) {
        try {
            JsonNode response = geminiRestClient.post()
                    .uri("/v1beta/models/{model}:generateContent", properties.model())
                    .body(requestBody(question)).retrieve().body(JsonNode.class);
            return extract(response);
        } catch (RestClientResponseException ex) {
            throw new GeminiClientException(GeminiClientException.Reason.UPSTREAM_ERROR,
                    "Gemini rejected the request with status " + ex.getStatusCode().value(), ex);
        } catch (RestClientException ex) {
            throw new GeminiClientException(GeminiClientException.Reason.UNAVAILABLE,
                    "Gemini is currently unavailable", ex);
        }
    }

    FixoraAiResponse extract(JsonNode response) {
        if (response == null) malformed("Gemini returned an empty response");
        String blockReason = response.path("promptFeedback").path("blockReason").asText();
        if (!blockReason.isBlank())
            throw new GeminiClientException(GeminiClientException.Reason.BLOCKED, "The request was blocked by Gemini safety controls");
        JsonNode candidates = response.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) malformed("Gemini returned no candidates");
        JsonNode parts = candidates.get(0).path("content").path("parts");
        if (!parts.isArray() || parts.isEmpty()) malformed("Gemini returned no content parts");
        String text = parts.get(0).path("text").asText();
        if (text.isBlank()) malformed("Gemini returned blank generated content");
        try { return objectMapper.readValue(text, FixoraAiResponse.class); }
        catch (JacksonException ex) { throw new GeminiClientException(GeminiClientException.Reason.MALFORMED_RESPONSE, "Gemini returned malformed structured output", ex); }
    }

    private void malformed(String message) { throw new GeminiClientException(GeminiClientException.Reason.MALFORMED_RESPONSE, message); }

    private Map<String, Object> requestBody(String question) {
        return Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", SYSTEM_INSTRUCTION))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", question)))),
                "generationConfig", Map.of("responseMimeType", "application/json", "responseJsonSchema", schema(),
                        "maxOutputTokens", properties.maxOutputTokens(), "temperature", properties.temperature()));
    }

    private Map<String, Object> schema() {
        var properties = new LinkedHashMap<String, Object>();
        properties.put("requestType", enumSchema("SERVICE_REQUEST", "GENERAL_QUESTION", "CLARIFICATION_REQUIRED", "UNSUPPORTED_SERVICE", "EMERGENCY"));
        properties.put("supportedByFixora", Map.of("type", "boolean"));
        properties.put("category", enumSchema("PLUMBER", "ELECTRICIAN", "CARPENTER", "PAINTER", "CLEANER", "WATER_PURIFIER", "AC", "WOMEN_SALON", "MEN_SALON", "GENERAL_INFORMATION", "UNSUPPORTED"));
        properties.put("title", Map.of("type", "string")); properties.put("summary", Map.of("type", "string"));
        properties.put("possibleCauses", stringArray()); properties.put("safeActions", stringArray());
        properties.put("warning", Map.of("type", "string"));
        properties.put("urgency", enumSchema("LOW", "MEDIUM", "HIGH", "EMERGENCY"));
        properties.put("requiresProfessional", Map.of("type", "boolean"));
        properties.put("bookingRecommended", Map.of("type", "boolean"));
        properties.put("bookingMessage", Map.of("type", List.of("string", "null")));
        properties.put("confidence", Map.of("type", "number", "minimum", 0.0, "maximum", 1.0));
        List<String> order = List.copyOf(properties.keySet());
        var schema = new LinkedHashMap<String, Object>();
        schema.put("type", "object"); schema.put("additionalProperties", false); schema.put("properties", properties);
        schema.put("required", order); schema.put("propertyOrdering", order);
        return schema;
    }

    private Map<String, Object> enumSchema(String... values) { return Map.of("type", "string", "enum", List.of(values)); }
    private Map<String, Object> stringArray() { return Map.of("type", "array", "items", Map.of("type", "string"), "maxItems", 5); }
}
