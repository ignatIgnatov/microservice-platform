package com.platform.auth.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(-10)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // Set response headers
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        // Build error response
        ErrorResponse errorResponse = buildErrorResponse(ex, exchange);
        HttpStatus status = determineHttpStatus(ex);

        response.setStatusCode(status);
        logException(ex, status);

        // Serialize response
        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            log.error("Error serializing error response", e);
            responseBody = "{\"error\":\"Internal Server Error\",\"message\":\"Error processing request\"}";
        }

        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    private ErrorResponse buildErrorResponse(Throwable ex, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        HttpStatus status = determineHttpStatus(ex);

        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(getErrorMessage(status))
                .path(path);

        // Handle specific exceptions
        if (ex instanceof UserAlreadyExistsException) {
            builder.message(ex.getMessage());
            log.warn("User already exists: {}", ex.getMessage());
        } else if (ex instanceof UserNotFoundException) {
            builder.message(ex.getMessage());
            log.warn("User not found: {}", ex.getMessage());
        } else if (ex instanceof WrongPasswordException) {
            builder.message(ex.getMessage());
            log.warn("Wrong password attempt: {}", ex.getMessage());
        } else if (ex instanceof InvalidTokenException) {
            builder.message(ex.getMessage());
            log.warn("Invalid token: {}", ex.getMessage());
        } else if (ex instanceof BusinessException businessEx) {
            builder.message(businessEx.getMessage())
                    .status(businessEx.getStatus().value())
                    .error(getErrorMessage(businessEx.getStatus()));
            log.warn("Business exception: {}", businessEx.getMessage());
        } else if (ex instanceof KeycloakOperationException keycloakEx) {
            builder.message(keycloakEx.getMessage());
            log.error("Keycloak operation failed: {}", keycloakEx.getMessage());
        } else if (ex instanceof WebExchangeBindException bindException) {
            Map<String, String> validationErrors = new HashMap<>();
            bindException.getBindingResult().getFieldErrors().forEach(error ->
                    validationErrors.put(error.getField(), error.getDefaultMessage())
            );
            builder.message("Validation failed")
                    .validationErrors(validationErrors);
            log.warn("Validation error: {}", bindException.getMessage());
        } else if (ex instanceof ConstraintViolationException constraintEx) {
            Map<String, String> validationErrors = new HashMap<>();
            constraintEx.getConstraintViolations().forEach(violation ->
                    validationErrors.put(violation.getPropertyPath().toString(), violation.getMessage())
            );
            builder.message("Validation constraints violated")
                    .validationErrors(validationErrors);
            log.warn("Constraint violation: {}", constraintEx.getMessage());
        } else if (ex instanceof AuthenticationException) {
            builder.message("Authentication failed");
            log.warn("Authentication failed: {}", ex.getMessage());
        } else if (ex instanceof AccessDeniedException) {
            builder.message("Access denied");
            log.warn("Access denied: {}", ex.getMessage());
        } else if (ex instanceof ResponseStatusException responseStatusEx) {
            builder.message(responseStatusEx.getReason() != null ? responseStatusEx.getReason() : "Request failed")
                    .status(responseStatusEx.getStatusCode().value())
                    .error(getErrorMessage(HttpStatus.valueOf(responseStatusEx.getStatusCode().value())));
        } else if (ex instanceof IllegalArgumentException) {
            builder.message(ex.getMessage());
            log.warn("Illegal argument: {}", ex.getMessage());
        } else {
            builder.message("An unexpected error occurred");
            log.error("Unexpected error occurred", ex);
        }

        return builder.build();
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof BusinessException businessEx) {
            return businessEx.getStatus();
        } else if (ex instanceof UserAlreadyExistsException) {
            return HttpStatus.CONFLICT;
        } else if (ex instanceof UserNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof WrongPasswordException || ex instanceof InvalidTokenException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        } else if (ex instanceof WebExchangeBindException || ex instanceof ConstraintViolationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof KeycloakOperationException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof ResponseStatusException responseStatusEx) {
            return HttpStatus.valueOf(responseStatusEx.getStatusCode().value());
        } else if (ex instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private void logException(Throwable ex, HttpStatus status) {
        if (status.is5xxServerError()) {
            log.error("Server error occurred: {}", ex.getMessage(), ex);
        } else if (status.is4xxClientError()) {
            log.warn("Client error: {} - {}", status, ex.getMessage());
        } else {
            log.info("Request processed with status: {}", status);
        }
    }

    private String getErrorMessage(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Bad Request";
            case UNAUTHORIZED -> "Unauthorized";
            case FORBIDDEN -> "Forbidden";
            case NOT_FOUND -> "Not Found";
            case CONFLICT -> "Conflict";
            case INTERNAL_SERVER_ERROR -> "Internal Server Error";
            case SERVICE_UNAVAILABLE -> "Service Unavailable";
            default -> "Error";
        };
    }
}