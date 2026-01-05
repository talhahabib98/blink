package com.blink.urlshortener.dto;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {

    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final String message;
    private final List<String> details;

    public ErrorResponse(int status, String error, String message, List<String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, List.of());
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }
}
