package com.shoaib.aiservice.client;

public class GeminiClientException extends RuntimeException {
    public enum Reason { UNAVAILABLE, BLOCKED, MALFORMED_RESPONSE, UPSTREAM_ERROR }
    private final Reason reason;

    public GeminiClientException(Reason reason, String message) { super(message); this.reason = reason; }
    public GeminiClientException(Reason reason, String message, Throwable cause) { super(message, cause); this.reason = reason; }
    public Reason getReason() { return reason; }
}
