package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class ImageUploadException extends BusinessException {
    public ImageUploadException(String message) {
        super("Image upload failed: " + message, HttpStatus.BAD_REQUEST);
    }
}
