package com.blink.urlshortener.repository;

import com.blink.urlshortener.domain.ShortUrl;

import java.util.Collection;
import java.util.Optional;

/**
 * Storage abstraction for short URLs. Kept as an interface so the in-memory
 * implementation can later be swapped for a persistent store without
 * touching the service layer.
 */
public interface ShortUrlRepository {

    /**
     * Persists a new short URL. Returns false without overwriting if the
     * code is already taken, so callers can retry with a different code.
     */
    boolean save(ShortUrl shortUrl);

    Optional<ShortUrl> findByCode(String code);

    boolean existsByCode(String code);

    void deleteByCode(String code);

    Collection<ShortUrl> findAll();

    long count();
}
