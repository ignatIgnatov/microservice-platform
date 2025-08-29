package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class AdValidationException extends BusinessException {
    public AdValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
