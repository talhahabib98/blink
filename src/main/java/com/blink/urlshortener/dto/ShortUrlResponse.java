package com.blink.urlshortener.dto;

import com.blink.urlshortener.domain.ShortUrl;

import java.time.Instant;

public class ShortUrlResponse {

    private final String code;
    private final String shortUrl;
    private final String originalUrl;
    private final Instant createdAt;
    private final Instant expiresAt;

    public ShortUrlResponse(String code, String shortUrl, String originalUrl, Instant createdAt, Instant expiresAt) {
        this.code = code;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public static ShortUrlResponse from(ShortUrl shortUrl, String baseUrl) {
        return new ShortUrlResponse(
                shortUrl.getCode(),
                baseUrl + "/" + shortUrl.getCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt());
    }

    public String getCode() {
        return code;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
