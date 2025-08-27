package com.platform.auth.service;

import com.platform.auth.dto.LoginRequest;
import com.platform.auth.dto.RegisterRequest;
import com.platform.auth.dto.TokenResponse;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final Keycloak keycloak;

    public AuthService(RestTemplate restTemplate, Keycloak keycloak) {
        this.restTemplate = restTemplate;
        this.keycloak = keycloak;
    }

    public void registerUser(RegisterRequest request) {
        try {
            // First check if user already exists
            UsersResource usersResource = keycloak.realm(realm).users();
            List<UserRepresentation> existingUsers = usersResource.search(request.getEmail(), true);

            if (!existingUsers.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
            }

            // Create new user
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(request.getEmail());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmailVerified(true); // Set to false if you want email verification

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
                } else {
                    String body = response.readEntity(String.class);
                    log.error("Failed to create user. Status: {}, Body: {}", status, body);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create user: " + body);
                }
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
    }

    public TokenResponse authenticateUser(LoginRequest request) {
        try {
            String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("username", request.getEmail());
            body.add("password", request.getPassword());

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, Map.class);

            log.debug("Keycloak response status: {}", response.getStatusCode());

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Login failed with status: {}", response.getStatusCode());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
            }

            Map<String, Object> tokenData = response.getBody();
            log.info("Login successful for user: {}", request.getEmail());

            return new TokenResponse(
                    (String) tokenData.get("access_token"),
                    (String) tokenData.get("refresh_token"),
                    (Integer) tokenData.get("expires_in")
            );
        } catch (Exception e) {
            log.error("Authentication error for user {}: {}", request.getEmail(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, Map.class);
        Map<String, Object> tokenData = response.getBody();

        return new TokenResponse(
                (String) tokenData.get("access_token"),
                (String) tokenData.get("refresh_token"),
                (Integer) tokenData.get("expires_in")
        );
    }

    public void logout(String refreshToken) {
        String logoutUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.exchange(logoutUrl, HttpMethod.POST, requestEntity, String.class);
    }

    public String getGoogleLoginUrl() {
        return keycloakServerUrl + "/realms/" + realm + "/broker/google/login?client_id=" + clientId +
                "&response_type=code&redirect_uri=http://localhost:3000/auth/callback";
    }

    public String getFacebookLoginUrl() {
        return keycloakServerUrl + "/realms/" + realm + "/broker/facebook/login?client_id=" + clientId +
                "&response_type=code&redirect_uri=http://localhost:3000/auth/callback";
    }
}