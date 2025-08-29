package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class MandatoryFieldMissingException extends BusinessException {
    public MandatoryFieldMissingException(String fieldName, String category) {
        super(String.format("Mandatory field '%s' is missing for category '%s'", fieldName, category),
                HttpStatus.BAD_REQUEST);
    }
}
