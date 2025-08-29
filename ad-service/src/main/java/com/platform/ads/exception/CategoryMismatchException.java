package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class CategoryMismatchException extends BusinessException {
    public CategoryMismatchException(String expectedCategory, String actualCategory) {
        super(String.format("Category mismatch. Expected: %s, but got: %s", expectedCategory, actualCategory),
                HttpStatus.BAD_REQUEST);
    }
}
