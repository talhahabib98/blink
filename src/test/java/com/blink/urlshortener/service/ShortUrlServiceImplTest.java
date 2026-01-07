package com.blink.urlshortener.service;

import com.blink.urlshortener.config.BlinkProperties;
import com.blink.urlshortener.domain.ShortUrl;
import com.blink.urlshortener.exception.DuplicateAliasException;
import com.blink.urlshortener.exception.InvalidUrlException;
import com.blink.urlshortener.exception.ShortUrlExpiredException;
import com.blink.urlshortener.exception.ShortUrlNotFoundException;
import com.blink.urlshortener.repository.InMemoryShortUrlRepository;
import com.blink.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShortUrlServiceImplTest {

    private ShortUrlRepository repository;
    private ShortUrlServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryShortUrlRepository();
        BlinkProperties properties = new BlinkProperties();
        properties.getShortCode().setLength(7);
        properties.setDefaultExpiryDays(365);
        service = new ShortUrlServiceImpl(repository, new Base62RandomCodeGenerator(), properties);
    }

    @Test
    void createsShortUrlWithGeneratedCode() {
        ShortUrl shortUrl = service.createShortUrl("https://example.com/page", null, null);

        assertThat(shortUrl.getCode()).hasSize(7);
        assertThat(shortUrl.getOriginalUrl()).isEqualTo("https://example.com/page");
        assertThat(shortUrl.getExpiresAt()).isAfter(Instant.now());
    }

    @Test
    void createsShortUrlWithCustomAlias() {
        ShortUrl shortUrl = service.createShortUrl("https://example.com/page", "my-alias", null);

        assertThat(shortUrl.getCode()).isEqualTo("my-alias");
    }

    @Test
    void rejectsDuplicateCustomAlias() {
        service.createShortUrl("https://example.com/one", "taken", null);

        assertThatThrownBy(() -> service.createShortUrl("https://example.com/two", "taken", null))
                .isInstanceOf(DuplicateAliasException.class);
    }

    @Test
    void rejectsMalformedAlias() {
        assertThatThrownBy(() -> service.createShortUrl("https://example.com", "a", null))
                .isInstanceOf(InvalidUrlException.class);
    }

    @Test
    void rejectsBlankUrl() {
        assertThatThrownBy(() -> service.createShortUrl("  ", null, null))
                .isInstanceOf(InvalidUrlException.class);
    }

    @Test
    void rejectsNonHttpScheme() {
        assertThatThrownBy(() -> service.createShortUrl("ftp://example.com/file", null, null))
                .isInstanceOf(InvalidUrlException.class);
    }

    @Test
    void resolveIncrementsClickCount() {
        ShortUrl shortUrl = service.createShortUrl("https://example.com", "click-test", null);

        service.resolveAndRecordClick("click-test");
        service.resolveAndRecordClick("click-test");

        assertThat(shortUrl.getClickCount()).isEqualTo(2);
    }

    @Test
    void resolveThrowsWhenExpired() {
        ShortUrl expired = new ShortUrl("expired1", "https://example.com",
                Instant.now().minus(2, ChronoUnit.DAYS), Instant.now().minus(1, ChronoUnit.DAYS));
        repository.save(expired);

        assertThatThrownBy(() -> service.resolveAndRecordClick("expired1"))
                .isInstanceOf(ShortUrlExpiredException.class);
    }

    @Test
    void getByCodeThrowsWhenMissing() {
        assertThatThrownBy(() -> service.getByCode("missing"))
                .isInstanceOf(ShortUrlNotFoundException.class);
    }

    @Test
    void deleteRemovesEntry() {
        service.createShortUrl("https://example.com", "to-delete", null);

        service.deleteByCode("to-delete");

        assertThatThrownBy(() -> service.getByCode("to-delete"))
                .isInstanceOf(ShortUrlNotFoundException.class);
    }

    @Test
    void deleteThrowsWhenMissing() {
        assertThatThrownBy(() -> service.deleteByCode("missing"))
                .isInstanceOf(ShortUrlNotFoundException.class);
    }
}
