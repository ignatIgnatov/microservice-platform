package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class AdNotActiveException extends BusinessException {
    public AdNotActiveException(Long id) {
        super("Ad with ID " + id + " is not active", HttpStatus.BAD_REQUEST);
    }
}
