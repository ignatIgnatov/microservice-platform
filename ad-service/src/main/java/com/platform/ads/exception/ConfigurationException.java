package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class ConfigurationException extends BusinessException {
    public ConfigurationException(String message) {
        super("Configuration error: " + message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
