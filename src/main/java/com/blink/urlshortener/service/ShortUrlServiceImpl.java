package com.blink.urlshortener.service;

import com.blink.urlshortener.config.BlinkProperties;
import com.blink.urlshortener.domain.ShortUrl;
import com.blink.urlshortener.exception.DuplicateAliasException;
import com.blink.urlshortener.exception.InvalidUrlException;
import com.blink.urlshortener.exception.ShortUrlExpiredException;
import com.blink.urlshortener.exception.ShortUrlNotFoundException;
import com.blink.urlshortener.repository.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.regex.Pattern;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private static final Logger log = LoggerFactory.getLogger(ShortUrlServiceImpl.class);
    private static final int MAX_GENERATION_ATTEMPTS = 5;
    private static final Pattern ALIAS_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,32}$");

    private final ShortUrlRepository repository;
    private final ShortCodeGenerator codeGenerator;
    private final BlinkProperties properties;

    public ShortUrlServiceImpl(ShortUrlRepository repository,
                                ShortCodeGenerator codeGenerator,
                                BlinkProperties properties) {
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.properties = properties;
    }

    @Override
    public ShortUrl createShortUrl(String originalUrl, String customAlias, Long expiresInDays) {
        String normalizedUrl = validateAndNormalizeUrl(originalUrl);
        Instant now = Instant.now();
        long ttlDays = expiresInDays != null ? expiresInDays : properties.getDefaultExpiryDays();
        Instant expiresAt = ttlDays > 0 ? now.plus(ttlDays, ChronoUnit.DAYS) : null;

        if (customAlias != null && !customAlias.isBlank()) {
            String alias = customAlias.trim();
            if (!ALIAS_PATTERN.matcher(alias).matches()) {
                throw new InvalidUrlException(
                        "Alias must be 3-32 characters long and contain only letters, digits, '-' or '_'");
            }
            ShortUrl shortUrl = new ShortUrl(alias, normalizedUrl, now, expiresAt);
            if (!repository.save(shortUrl)) {
                throw new DuplicateAliasException(alias);
            }
            log.info("Created short URL with custom alias '{}' -> {}", alias, normalizedUrl);
            return shortUrl;
        }

        return createWithGeneratedCode(normalizedUrl, now, expiresAt);
    }

    private ShortUrl createWithGeneratedCode(String normalizedUrl, Instant now, Instant expiresAt) {
        int length = properties.getShortCode().getLength();
        for (int attempt = 1; attempt <= MAX_GENERATION_ATTEMPTS; attempt++) {
            String code = codeGenerator.generate(length);
            ShortUrl shortUrl = new ShortUrl(code, normalizedUrl, now, expiresAt);
            if (repository.save(shortUrl)) {
                log.info("Created short URL '{}' -> {}", code, normalizedUrl);
                return shortUrl;
            }
            log.warn("Short code collision on attempt {} for code '{}'", attempt, code);
        }
        throw new IllegalStateException(
                "Failed to generate a unique short code after " + MAX_GENERATION_ATTEMPTS + " attempts");
    }

    @Override
    public ShortUrl getByCode(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new ShortUrlNotFoundException(code));
    }

    @Override
    public ShortUrl resolveAndRecordClick(String code) {
        ShortUrl shortUrl = getByCode(code);
        if (shortUrl.isExpired()) {
            throw new ShortUrlExpiredException(code);
        }
        shortUrl.incrementClickCount();
        return shortUrl;
    }

    @Override
    public void deleteByCode(String code) {
        if (!repository.existsByCode(code)) {
            throw new ShortUrlNotFoundException(code);
        }
        repository.deleteByCode(code);
        log.info("Deleted short URL '{}'", code);
    }

    @Override
    public Collection<ShortUrl> listAll() {
        return repository.findAll();
    }

    private String validateAndNormalizeUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new InvalidUrlException("URL must not be blank");
        }
        String trimmed = originalUrl.trim();
        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new InvalidUrlException("URL must use the http or https scheme");
            }
            if (uri.getHost() == null) {
                throw new InvalidUrlException("URL must include a host");
            }
            return uri.toString();
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("Malformed URL: " + e.getMessage());
        }
    }
}
