package com.blink.urlshortener.scheduler;

import com.blink.urlshortener.domain.ShortUrl;
import com.blink.urlshortener.repository.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically evicts expired entries so the in-memory store doesn't grow
 * unbounded with links nobody can resolve anymore.
 */
@Component
public class ExpiredUrlCleanupTask {

    private static final Logger log = LoggerFactory.getLogger(ExpiredUrlCleanupTask.class);

    private final ShortUrlRepository repository;

    public ExpiredUrlCleanupTask(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRateString = "${blink.cleanup.interval-ms:3600000}")
    public void evictExpired() {
        int evicted = 0;
        for (ShortUrl shortUrl : repository.findAll()) {
            if (shortUrl.isExpired()) {
                repository.deleteByCode(shortUrl.getCode());
                evicted++;
            }
        }
        if (evicted > 0) {
            log.info("Evicted {} expired short URL(s)", evicted);
        }
    }
}
