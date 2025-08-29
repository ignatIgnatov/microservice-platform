package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class AuthServiceException extends BusinessException {
    public AuthServiceException(String message) {
        super("Authentication service error: " + message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public AuthServiceException(String message, Throwable cause) {
        super("Authentication service error: " + message, cause, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
