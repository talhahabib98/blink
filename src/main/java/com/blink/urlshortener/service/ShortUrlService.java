package com.blink.urlshortener.service;

import com.blink.urlshortener.domain.ShortUrl;

import java.util.Collection;

public interface ShortUrlService {

    /**
     * Creates a new short URL. If {@code customAlias} is provided it is used
     * verbatim (and must be free); otherwise a random code is generated.
     *
     * @param originalUrl   the destination URL to redirect to
     * @param customAlias   optional caller-chosen code, or null/blank for a generated one
     * @param expiresInDays optional lifetime in days, or null to use the configured default
     */
    ShortUrl createShortUrl(String originalUrl, String customAlias, Long expiresInDays);

    /**
     * Looks up a short URL by code without affecting its click count.
     */
    ShortUrl getByCode(String code);

    /**
     * Resolves a code to its destination URL and records a click. Used on
     * the redirect path.
     */
    ShortUrl resolveAndRecordClick(String code);

    void deleteByCode(String code);

    Collection<ShortUrl> listAll();
}
