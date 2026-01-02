package com.blink.urlshortener.repository;

import com.blink.urlshortener.domain.ShortUrl;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory store backed by a ConcurrentHashMap. A single
 * instance is shared across all request-handling threads, so writes use
 * putIfAbsent/remove to stay atomic without an external lock.
 */
@Repository
public class InMemoryShortUrlRepository implements ShortUrlRepository {

    private final Map<String, ShortUrl> store = new ConcurrentHashMap<>();

    @Override
    public boolean save(ShortUrl shortUrl) {
        return store.putIfAbsent(shortUrl.getCode(), shortUrl) == null;
    }

    @Override
    public Optional<ShortUrl> findByCode(String code) {
        return Optional.ofNullable(store.get(code));
    }

    @Override
    public boolean existsByCode(String code) {
        return store.containsKey(code);
    }

    @Override
    public void deleteByCode(String code) {
        store.remove(code);
    }

    @Override
    public Collection<ShortUrl> findAll() {
        return store.values();
    }

    @Override
    public long count() {
        return store.size();
    }
}
