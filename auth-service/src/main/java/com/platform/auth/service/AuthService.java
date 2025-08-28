package com.platform.auth.service;

import com.platform.auth.dto.LoginRequest;
import com.platform.auth.dto.RegisterRequest;
import com.platform.auth.dto.TokenResponse;
import com.platform.auth.exception.BusinessException;
import com.platform.auth.exception.InvalidTokenException;
import com.platform.auth.exception.KeycloakOperationException;
import com.platform.auth.exception.UserAlreadyExistsException;
import com.platform.auth.exception.UserNotFoundException;
import com.platform.auth.exception.WrongPasswordException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final WebClient webClient;
    private final Keycloak keycloak;

    public AuthService(WebClient webClient, Keycloak keycloak) {
        this.webClient = webClient;
        this.keycloak = keycloak;
    }

    public Mono<Void> registerUser(RegisterRequest request) {
        return Mono.fromCallable(() -> {
                    // First check if user already exists
                    UsersResource usersResource = keycloak.realm(realm).users();
                    List<UserRepresentation> existingUsers = usersResource.search(request.getEmail(), true);

                    if (!existingUsers.isEmpty()) {
                        log.error("User already exists: {}", request.getEmail());
                        throw new UserAlreadyExistsException(request.getEmail());
                    }

                    // Create new user
                    UserRepresentation user = new UserRepresentation();
                    user.setEnabled(true);
                    user.setUsername(request.getEmail());
                    user.setEmail(request.getEmail());
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setEmailVerified(true);

                    // Set credentials
                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(request.getPassword());
                    credential.setTemporary(false);
                    user.setCredentials(Collections.singletonList(credential));

                    // Create user in Keycloak
                    try (Response response = usersResource.create(user)) {
                        int status = response.getStatus();
                        log.info("Keycloak create user status: {}", status);

                        if (status == 201) {
                            log.info("User {} registered successfully", request.getEmail());
                            return null;
                        } else if (status == 409) {
                            throw new UserAlreadyExistsException(request.getEmail());
                        } else if (status >= 400 && status < 500) {
                            String body = response.readEntity(String.class);
                            log.error("Failed to create user. Status: {}, Body: {}", status, body);
                            throw new BusinessException("Invalid user data: " + body, HttpStatus.BAD_REQUEST);
                        } else {
                            String body = response.readEntity(String.class);
                            log.error("Keycloak server error. Status: {}, Body: {}", status, body);
                            throw new KeycloakOperationException("createUser", "Server error: " + body);
                        }
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<TokenResponse> authenticateUser(LoginRequest request) {
        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", request.getEmail());
        body.add("password", request.getPassword());

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(Map.class)
                .map(tokenData -> {
                    if (tokenData == null || tokenData.get("access_token") == null) {
                        throw new KeycloakOperationException("authenticate", "Invalid response from Keycloak");
                    }

                    log.info("Login successful for user: {}", request.getEmail());

                    return new TokenResponse(
                            (String) tokenData.get("access_token"),
                            (String) tokenData.get("refresh_token"),
                            (Integer) tokenData.get("expires_in")
                    );
                })
                .onErrorMap(WebClientResponseException.class, e -> {
                    String responseBody = e.getResponseBodyAsString();
                    log.warn("Authentication failed for user {}: {}", request.getEmail(), responseBody);

                    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        return new WrongPasswordException();
                    } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return new BusinessException("Invalid authentication request", HttpStatus.BAD_REQUEST);
                    } else if (e.getStatusCode().is5xxServerError()) {
                        log.error("Keycloak server error during authentication: {}", e.getMessage());
                        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Keycloak service unavailable");
                    } else {
                        return new BusinessException("Authentication failed: " + e.getMessage());
                    }
                })
                .onErrorMap(Exception.class, e -> {
                    if (e instanceof BadCredentialsException ||
                            e instanceof BusinessException ||
                            e instanceof ResponseStatusException) {
                        return e;
                    }
                    log.error("Unexpected authentication error for user {}: {}", request.getEmail(), e.getMessage(), e);
                    return new KeycloakOperationException("authenticate", "Unexpected error: " + e.getMessage());
                });
    }

    public Mono<TokenResponse> refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return Mono.error(new InvalidTokenException("Refresh token cannot be empty"));
        }

        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(Map.class)
                .map(tokenData -> {
                    if (tokenData == null || tokenData.get("access_token") == null) {
                        throw new KeycloakOperationException("refreshToken", "Invalid response from Keycloak");
                    }

                    return new TokenResponse(
                            (String) tokenData.get("access_token"),
                            (String) tokenData.get("refresh_token"),
                            (Integer) tokenData.get("expires_in")
                    );
                })
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.warn("Token refresh failed: {}", e.getMessage());
                    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return new InvalidTokenException("Refresh token is invalid or expired");
                    } else if (e.getStatusCode().is5xxServerError()) {
                        log.error("Keycloak server error during token refresh: {}", e.getMessage());
                        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Keycloak service unavailable");
                    } else {
                        return new BusinessException("Token refresh failed: " + e.getMessage());
                    }
                });
    }

    public Mono<Void> logout(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return Mono.error(new InvalidTokenException("Refresh token cannot be empty"));
        }

        String logoutUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        return webClient.post()
                .uri(logoutUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(String.class)
                .then()
                .doOnSuccess(unused -> log.info("User logged out successfully"))
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.warn("Logout failed: {}", e.getMessage());
                    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return new InvalidTokenException("Invalid refresh token for logout");
                    } else if (e.getStatusCode().is5xxServerError()) {
                        log.error("Keycloak server error during logout: {}", e.getMessage());
                        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Authentication service unavailable");
                    } else {
                        return new BusinessException("Logout failed: " + e.getMessage());
                    }
                });
    }

    public Mono<String> getGoogleLoginUrl() {
        return Mono.fromSupplier(() -> {
            try {
                return keycloakServerUrl + "/realms/" + realm + "/broker/google/login?client_id=" + clientId +
                        "&response_type=code&redirect_uri=http://localhost:3000/auth/callback";
            } catch (Exception e) {
                log.error("Error generating Google login URL: {}", e.getMessage());
                throw new BusinessException("Failed to generate Google login URL", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    public Mono<String> getFacebookLoginUrl() {
        return Mono.fromSupplier(() -> {
            try {
                return keycloakServerUrl + "/realms/" + realm + "/broker/facebook/login?client_id=" + clientId +
                        "&response_type=code&redirect_uri=http://localhost:3000/auth/callback";
            } catch (Exception e) {
                log.error("Error generating Facebook login URL: {}", e.getMessage());
                throw new BusinessException("Failed to generate Facebook login URL", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    public Mono<UserRepresentation> findUserByEmail(String email) {
        return Mono.fromCallable(() -> {
                    try {
                        log.debug("Searching for user with email: {}", email);
                        UsersResource usersResource = keycloak.realm(realm).users();
                        List<UserRepresentation> users = usersResource.search(email, true); // exact match

                        if (users.isEmpty()) {
                            log.warn("User not found with email: {}", email);
                            throw new UserNotFoundException(email);
                        }

                        UserRepresentation user = users.get(0);
                        log.info("Found user: {} with ID: {}", user.getEmail(), user.getId());
                        return user;

                    } catch (Exception e) {
                        log.error("Error searching for user by email {}: {}", email, e.getMessage());
                        if (e instanceof UserNotFoundException) {
                            throw e;
                        }
                        throw new KeycloakOperationException("searchUser", "Failed to search user: " + e.getMessage());
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}