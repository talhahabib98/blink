package com.blink.urlshortener.dto;

public class CreateShortUrlRequest {

    private String originalUrl;
    private String customAlias;
    private Long expiresInDays;

    public CreateShortUrlRequest() {
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public Long getExpiresInDays() {
        return expiresInDays;
    }

    public void setExpiresInDays(Long expiresInDays) {
        this.expiresInDays = expiresInDays;
    }
}
