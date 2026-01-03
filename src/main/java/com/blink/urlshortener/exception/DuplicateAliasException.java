package com.blink.urlshortener.exception;

public class DuplicateAliasException extends RuntimeException {

    public DuplicateAliasException(String alias) {
        super("Alias '" + alias + "' is already in use");
    }
}
