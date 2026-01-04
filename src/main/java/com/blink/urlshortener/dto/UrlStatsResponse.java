package com.blink.urlshortener.dto;

import com.blink.urlshortener.domain.ShortUrl;

import java.time.Instant;

public class UrlStatsResponse {

    private final String code;
    private final String shortUrl;
    private final String originalUrl;
    private final Instant createdAt;
    private final Instant expiresAt;
    private final long clickCount;
    private final boolean expired;

    public UrlStatsResponse(String code, String shortUrl, String originalUrl, Instant createdAt,
                             Instant expiresAt, long clickCount, boolean expired) {
        this.code = code;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.clickCount = clickCount;
        this.expired = expired;
    }

    public static UrlStatsResponse from(ShortUrl shortUrl, String baseUrl) {
        return new UrlStatsResponse(
                shortUrl.getCode(),
                baseUrl + "/" + shortUrl.getCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt(),
                shortUrl.getClickCount(),
                shortUrl.isExpired());
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

    public long getClickCount() {
        return clickCount;
    }

    public boolean isExpired() {
        return expired;
    }
}
