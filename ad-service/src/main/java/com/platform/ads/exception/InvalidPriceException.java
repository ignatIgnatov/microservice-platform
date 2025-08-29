package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class InvalidPriceException extends BusinessException {
    public InvalidPriceException(String message) {
        super("Invalid price: " + message, HttpStatus.BAD_REQUEST);
    }
}
