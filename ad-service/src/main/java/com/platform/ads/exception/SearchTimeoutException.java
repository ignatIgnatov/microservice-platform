package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class SearchTimeoutException extends BusinessException {
    public SearchTimeoutException() {
        super("Search request timed out", HttpStatus.REQUEST_TIMEOUT);
    }
}
