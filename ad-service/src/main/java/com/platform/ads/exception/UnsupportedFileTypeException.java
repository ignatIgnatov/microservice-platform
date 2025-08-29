package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class UnsupportedFileTypeException extends BusinessException {
    public UnsupportedFileTypeException(String fileType) {
        super("Unsupported file type: " + fileType, HttpStatus.BAD_REQUEST);
    }
}
