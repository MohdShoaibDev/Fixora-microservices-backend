package com.shoaib.aiservice.client;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class GeminiClientTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final GeminiClient client = new GeminiClient(null, null, mapper);

    @Test void emptyCandidatesThrowsClientException() throws Exception {
        var ex = assertThrows(GeminiClientException.class,
                () -> client.extract(mapper.readTree("{\"candidates\":[]}")));
        assertEquals(GeminiClientException.Reason.MALFORMED_RESPONSE, ex.getReason());
    }

    @Test void malformedGeneratedJsonThrowsClientException() throws Exception {
        var response = mapper.readTree("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"not-json\"}]}}]}");
        var ex = assertThrows(GeminiClientException.class, () -> client.extract(response));
        assertEquals(GeminiClientException.Reason.MALFORMED_RESPONSE, ex.getReason());
    }
}
