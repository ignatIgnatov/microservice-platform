package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class InvalidSearchCriteriaException extends BusinessException {
    public InvalidSearchCriteriaException(String message) {
        super("Invalid search criteria: " + message, HttpStatus.BAD_REQUEST);
    }
}
