package com.blink.urlshortener.repository;

import com.blink.urlshortener.domain.ShortUrl;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryShortUrlRepositoryTest {

    private final InMemoryShortUrlRepository repository = new InMemoryShortUrlRepository();

    @Test
    void savesAndRetrievesByCode() {
        ShortUrl shortUrl = new ShortUrl("abc1234", "https://example.com", Instant.now(), null);

        boolean saved = repository.save(shortUrl);

        assertThat(saved).isTrue();
        assertThat(repository.findByCode("abc1234")).contains(shortUrl);
    }

    @Test
    void doesNotOverwriteExistingCode() {
        ShortUrl first = new ShortUrl("dup0001", "https://first.example.com", Instant.now(), null);
        ShortUrl second = new ShortUrl("dup0001", "https://second.example.com", Instant.now(), null);
        repository.save(first);

        boolean savedSecond = repository.save(second);

        assertThat(savedSecond).isFalse();
        assertThat(repository.findByCode("dup0001")).contains(first);
    }

    @Test
    void deletesByCode() {
        ShortUrl shortUrl = new ShortUrl("del0001", "https://example.com", Instant.now(), null);
        repository.save(shortUrl);

        repository.deleteByCode("del0001");

        assertThat(repository.findByCode("del0001")).isEmpty();
    }

    @Test
    void findByCodeReturnsEmptyForUnknownCode() {
        assertThat(repository.findByCode("missing")).isEmpty();
    }
}
