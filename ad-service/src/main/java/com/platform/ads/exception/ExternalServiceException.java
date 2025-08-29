package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends BusinessException {
    public ExternalServiceException(String serviceName, String message) {
        super(String.format("External service '%s' error: %s", serviceName, message),
                HttpStatus.SERVICE_UNAVAILABLE);
    }
}
