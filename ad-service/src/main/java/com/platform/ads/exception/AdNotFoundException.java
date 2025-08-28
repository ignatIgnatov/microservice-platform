package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class AdNotFoundException extends BusinessException {
    public AdNotFoundException(Long id) {
        super("Ad not found with ID: " + id, HttpStatus.NOT_FOUND);
    }
}
