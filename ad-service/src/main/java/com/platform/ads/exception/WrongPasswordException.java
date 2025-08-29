package com.platform.ads.exception;

import org.springframework.http.HttpStatus;

public class WrongPasswordException extends BusinessException {
    public WrongPasswordException() {
        super("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}
