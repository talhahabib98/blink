package com.blink.urlshortener.domain;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory representation of a shortened URL. Mutable fields (click count)
 * are backed by an AtomicLong since instances are shared across request
 * threads via the repository's map.
 */
public class ShortUrl {

    private final String code;
    private final String originalUrl;
    private final Instant createdAt;
    private final Instant expiresAt;
    private final AtomicLong clickCount = new AtomicLong(0);

    public ShortUrl(String code, String originalUrl, Instant createdAt, Instant expiresAt) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getCode() {
        return code;
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
        return clickCount.get();
    }

    public long incrementClickCount() {
        return clickCount.incrementAndGet();
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
}
