package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class InvalidEnumValueException extends BusinessException {
    public InvalidEnumValueException(String fieldName, String value, String validValues) {
        super(String.format("Invalid value '%s' for field '%s'. Valid values are: %s", value, fieldName, validValues),
                HttpStatus.BAD_REQUEST);
    }
}
