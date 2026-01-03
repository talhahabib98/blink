package com.blink.urlshortener.exception;

public class ShortUrlExpiredException extends RuntimeException {

    public ShortUrlExpiredException(String code) {
        super("Short URL '" + code + "' has expired");
    }
}
