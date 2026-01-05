package com.blink.urlshortener.controller;

import com.blink.urlshortener.config.BlinkProperties;
import com.blink.urlshortener.domain.ShortUrl;
import com.blink.urlshortener.dto.CreateShortUrlRequest;
import com.blink.urlshortener.dto.ShortUrlResponse;
import com.blink.urlshortener.dto.UrlStatsResponse;
import com.blink.urlshortener.service.ShortUrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/urls")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final BlinkProperties properties;

    public ShortUrlController(ShortUrlService shortUrlService, BlinkProperties properties) {
        this.shortUrlService = shortUrlService;
        this.properties = properties;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> create(@Valid @RequestBody CreateShortUrlRequest request) {
        ShortUrl shortUrl = shortUrlService.createShortUrl(
                request.getOriginalUrl(), request.getCustomAlias(), request.getExpiresInDays());
        ShortUrlResponse body = ShortUrlResponse.from(shortUrl, properties.getBaseUrl());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create(body.getShortUrl()))
                .body(body);
    }

    @GetMapping("/{code}")
    public ResponseEntity<UrlStatsResponse> stats(@PathVariable String code) {
        ShortUrl shortUrl = shortUrlService.getByCode(code);
        return ResponseEntity.ok(UrlStatsResponse.from(shortUrl, properties.getBaseUrl()));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        shortUrlService.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }
}
