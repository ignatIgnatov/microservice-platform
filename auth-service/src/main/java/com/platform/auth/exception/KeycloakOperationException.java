package com.platform.auth.exception;

import org.springframework.http.HttpStatus;

public class KeycloakOperationException extends BusinessException {
    public KeycloakOperationException(String operation, String details) {
        super("Keycloak operation '" + operation + "' failed: " + details, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
