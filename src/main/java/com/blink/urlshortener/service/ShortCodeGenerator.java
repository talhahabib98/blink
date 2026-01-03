package com.blink.urlshortener.service;

/**
 * Generates short, URL-safe codes used as the path segment for a shortened
 * link.
 */
public interface ShortCodeGenerator {

    String generate(int length);
}
