package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class AdOwnershipException extends BusinessException {
    public AdOwnershipException() {
        super("You can only modify your own ads", HttpStatus.FORBIDDEN);
    }

    public AdOwnershipException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
