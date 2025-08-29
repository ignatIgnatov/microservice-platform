package com.platform.ads.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@Order(-2)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = buildErrorResponse(ex, exchange);
        HttpStatus status = determineHttpStatus(ex);

        response.setStatusCode(status);
        logException(ex, status);

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
        if (ex instanceof BusinessException businessEx) {
            builder.message(businessEx.getMessage())
                    .status(businessEx.getStatus().value())
                    .error(getErrorMessage(businessEx.getStatus()));
            logBusinessException(businessEx);
        } else if (ex instanceof UserNotFoundException) {
            builder.message(ex.getMessage());
            log.warn("User not found: {}", ex.getMessage());
        } else if (ex instanceof UserAlreadyExistsException) {
            builder.message(ex.getMessage());
            log.warn("User already exists: {}", ex.getMessage());
        } else if (ex instanceof AdNotFoundException) {
            builder.message(ex.getMessage());
            log.warn("Ad not found: {}", ex.getMessage());
        } else if (ex instanceof AdValidationException) {
            builder.message(ex.getMessage());
            log.warn("Ad validation error: {}", ex.getMessage());
        } else if (ex instanceof CategoryMismatchException) {
            builder.message(ex.getMessage());
            log.warn("Category mismatch: {}", ex.getMessage());
        } else if (ex instanceof AdOwnershipException) {
            builder.message(ex.getMessage());
            log.warn("Ad ownership violation: {}", ex.getMessage());
        } else if (ex instanceof InvalidTokenException) {
            builder.message(ex.getMessage());
            log.warn("Invalid token: {}", ex.getMessage());
        } else if (ex instanceof WrongPasswordException) {
            builder.message(ex.getMessage());
            log.warn("Wrong password attempt: {}", ex.getMessage());
        } else if (ex instanceof AuthServiceException) {
            builder.message(ex.getMessage());
            log.error("Auth service error: {}", ex.getMessage());
        } else if (ex instanceof ExternalServiceException) {
            builder.message(ex.getMessage());
            log.error("External service error: {}", ex.getMessage());
        } else if (ex instanceof InvalidPriceException) {
            builder.message(ex.getMessage());
            log.warn("Invalid price: {}", ex.getMessage());
        } else if (ex instanceof MandatoryFieldMissingException) {
            builder.message(ex.getMessage());
            log.warn("Mandatory field missing: {}", ex.getMessage());
        } else if (ex instanceof InvalidEnumValueException) {
            builder.message(ex.getMessage());
            log.warn("Invalid enum value: {}", ex.getMessage());
        } else if (ex instanceof InvalidSearchCriteriaException) {
            builder.message(ex.getMessage());
            log.warn("Invalid search criteria: {}", ex.getMessage());
        } else if (ex instanceof SearchTimeoutException) {
            builder.message(ex.getMessage());
            log.warn("Search timeout: {}", ex.getMessage());
        } else if (ex instanceof ImageUploadException) {
            builder.message(ex.getMessage());
            log.warn("Image upload error: {}", ex.getMessage());
        } else if (ex instanceof UnsupportedFileTypeException) {
            builder.message(ex.getMessage());
            log.warn("Unsupported file type: {}", ex.getMessage());
        } else if (ex instanceof TooManyRequestsException) {
            builder.message(ex.getMessage());
            log.warn("Too many requests: {}", ex.getMessage());
        } else if (ex instanceof ConfigurationException) {
            builder.message(ex.getMessage());
            log.error("Configuration error: {}", ex.getMessage());
        } else if (ex instanceof WebExchangeBindException bindException) {
            Map<String, String> validationErrors = new HashMap<>();
            bindException.getBindingResult().getFieldErrors().forEach(error ->
                    validationErrors.put(error.getField(), error.getDefaultMessage())
            );
            builder.message("Validation failed")
                    .validationErrors(validationErrors);
            log.warn("Validation error: {}", bindException.getMessage());
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

    private void logBusinessException(BusinessException ex) {
        if (ex.getStatus().is5xxServerError()) {
            log.error("Business exception (server error): {}", ex.getMessage(), ex);
        } else if (ex.getStatus().is4xxClientError()) {
            log.warn("Business exception (client error): {}", ex.getMessage());
        } else {
            log.info("Business exception: {}", ex.getMessage());
        }
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof BusinessException businessEx) {
            return businessEx.getStatus();
        } else if (ex instanceof UserAlreadyExistsException) {
            return HttpStatus.CONFLICT;
        } else if (ex instanceof UserNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof AdNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof AdValidationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof CategoryMismatchException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof AdOwnershipException) {
            return HttpStatus.FORBIDDEN;
        } else if (ex instanceof InvalidTokenException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof WrongPasswordException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof AuthServiceException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (ex instanceof ExternalServiceException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (ex instanceof InvalidPriceException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof MandatoryFieldMissingException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof InvalidEnumValueException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof InvalidSearchCriteriaException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof SearchTimeoutException) {
            return HttpStatus.REQUEST_TIMEOUT;
        } else if (ex instanceof ImageUploadException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof UnsupportedFileTypeException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof TooManyRequestsException) {
            return HttpStatus.TOO_MANY_REQUESTS;
        } else if (ex instanceof ConfigurationException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        } else if (ex instanceof WebExchangeBindException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof ResponseStatusException responseStatusEx) {
            return HttpStatus.valueOf(responseStatusEx.getStatusCode().value());
        } else if (ex instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private void logException(Throwable ex, HttpStatus status) {
        String requestId = "REQ-" + System.currentTimeMillis(); // Simple request ID

        if (status.is5xxServerError()) {
            log.error("[{}] Server error occurred: {} - {}", requestId, status, ex.getMessage(), ex);
        } else if (status.is4xxClientError()) {
            log.warn("[{}] Client error: {} - {}", requestId, status, ex.getMessage());
        } else {
            log.info("[{}] Request processed with status: {}", requestId, status);
        }
    }

    private String getErrorMessage(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Bad Request";
            case UNAUTHORIZED -> "Unauthorized";
            case FORBIDDEN -> "Forbidden";
            case NOT_FOUND -> "Not Found";
            case CONFLICT -> "Conflict";
            case REQUEST_TIMEOUT -> "Request Timeout";
            case TOO_MANY_REQUESTS -> "Too Many Requests";
            case INTERNAL_SERVER_ERROR -> "Internal Server Error";
            case SERVICE_UNAVAILABLE -> "Service Unavailable";
            default -> "Error";
        };
    }
}