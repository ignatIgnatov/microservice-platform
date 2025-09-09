package com.platform.auth.service;

import com.platform.auth.dto.CompanyInfoResponse;
import com.platform.auth.dto.LoginRequest;
import com.platform.auth.dto.RegisterCompanyRequest;
import com.platform.auth.dto.RegisterRequest;
import com.platform.auth.dto.TokenResponse;
import com.platform.auth.dto.UpdateCompanyRequest;
import com.platform.auth.dto.UpdateUserRequest;
import com.platform.auth.dto.UserInfoResponse;
import com.platform.auth.exception.BusinessException;
import com.platform.auth.exception.InvalidTokenException;
import com.platform.auth.exception.KeycloakOperationException;
import com.platform.auth.exception.UserAlreadyExistsException;
import com.platform.auth.exception.UserNotFoundException;
import com.platform.auth.exception.WrongPasswordException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

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

    @Value("${frontend.url}")
    private String frontendUrl;

    private final WebClient webClient;
    private final Keycloak keycloak;

    public AuthService(WebClient webClient, Keycloak keycloak) {
        this.webClient = webClient;
        this.keycloak = keycloak;
    }

    public Mono<Void> registerUser(RegisterRequest request) {
        return Mono.fromCallable(() -> {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new BusinessException("Passwords do not match", HttpStatus.BAD_REQUEST);
            }

            UsersResource usersResource = keycloak.realm(realm).users();
            List<UserRepresentation> existingUsers = usersResource.search(request.getEmail(), true);

            if (!existingUsers.isEmpty()) {
                throw new UserAlreadyExistsException(request.getEmail());
            }

            UserRepresentation user = getUserRepresentation(request);

            try (Response response = usersResource.create(user)) {
                int status = response.getStatus();

                if (status == 201) {
                    // ✅ extract new userId
                    setRole(response, usersResource);

                    return null;
                } else if (status == 409) {
                    throw new UserAlreadyExistsException(request.getEmail());
                } else {
                    String body = response.readEntity(String.class);
                    throw new KeycloakOperationException("createUser", "Error: " + body);
                }
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Void> registerCompany(RegisterCompanyRequest request) {
        return Mono.fromCallable(() -> {
                    if (!request.getPassword().equals(request.getConfirmPassword())) {
                        throw new BusinessException("Passwords do not match", HttpStatus.BAD_REQUEST);
                    }

                    UsersResource usersResource = keycloak.realm(realm).users();
                    List<UserRepresentation> existingUsers = usersResource.search(request.getEmail(), true);

                    if (!existingUsers.isEmpty()) {
                        log.error("Company user already exists: {}", request.getEmail());
                        throw new UserAlreadyExistsException(request.getEmail());
                    }

                    UserRepresentation user = new UserRepresentation();
                    user.setEnabled(true);
                    user.setUsername(request.getEmail());
                    user.setEmail(request.getEmail());
                    user.setFirstName(request.getCompanyName());
                    user.setLastName(request.getStoreName());
                    user.setEmailVerified(true);

                    Map<String, List<String>> attributes = new HashMap<>();
                    attributes.put("company_name", Collections.singletonList(request.getCompanyName()));
                    attributes.put("store_name", Collections.singletonList(request.getStoreName()));
                    attributes.put("business_registration_number", Collections.singletonList(request.getBusinessRegistrationNumber()));
                    attributes.put("city", Collections.singletonList(request.getCity()));
                    attributes.put("address", Collections.singletonList(request.getAddress()));
                    attributes.put("user_type", Collections.singletonList("COMPANY"));
                    attributes.put("phone", Collections.singletonList(request.getPhone()));

                    user.setAttributes(attributes);

                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(request.getPassword());
                    credential.setTemporary(false);
                    user.setCredentials(Collections.singletonList(credential));

                    try (Response response = usersResource.create(user)) {
                        int status = response.getStatus();
                        log.info("Keycloak create company user status: {}", status);

                        if (status == 201) {
                            log.info("Company user {} registered successfully", request.getEmail());
                            setRole(response, usersResource);
                            return null;
                        } else if (status == 409) {
                            throw new UserAlreadyExistsException(request.getEmail());
                        } else if (status >= 400 && status < 500) {
                            String body = response.readEntity(String.class);
                            log.error("Failed to create company user. Status: {}, Body: {}", status, body);
                            throw new BusinessException("Invalid company data: " + body, HttpStatus.BAD_REQUEST);
                        } else {
                            String body = response.readEntity(String.class);
                            log.error("Keycloak server error. Status: {}, Body: {}", status, body);
                            throw new KeycloakOperationException("createCompanyUser", "Server error: " + body);
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
                        "&response_type=code&redirect_uri=" + frontendUrl + "/auth/callback";
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
                        "&response_type=code&redirect_uri=" + frontendUrl + "/auth/callback";
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

                Map<String, List<String>> attrs = user.getAttributes();
                if (attrs != null) {
                    if (attrs.containsKey("name")) {
                        String name = attrs.get("name").get(0);
                        user.setFirstName(name);
                    }
                    if (attrs.containsKey("phone")) {
                        String phone = attrs.get("phone").get(0);
                        attrs.put("phone", List.of(phone));
                    }
                }

                return user;

            } catch (Exception e) {
                log.error("Error searching for user by email {}: {}", email, e.getMessage(), e);
                if (e instanceof UserNotFoundException) {
                    throw e;
                }
                throw new KeycloakOperationException("searchUser", "Failed to search user: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Map<String, Object>> getCompanyStatistics(String email) {
        return findUserByEmail(email)
                .map(user -> {
                    Map<String, Object> statistics = new HashMap<>();

                    statistics.put("email", user.getEmail());
                    statistics.put("accountStatus", user.isEnabled() ? "ACTIVE" : "INACTIVE");
                    statistics.put("emailVerified", user.isEmailVerified());

                    if (user.getCreatedTimestamp() != null) {
                        Instant registrationInstant = Instant.ofEpochMilli(user.getCreatedTimestamp());
                        statistics.put("registrationDate", registrationInstant.toString());
                    } else {
                        statistics.put("registrationDate", null);
                    }

                    int completeness = calculateProfileCompleteness(user);
                    statistics.put("profileCompleteness", completeness);

                    Map<String, List<String>> attributes = user.getAttributes();
                    if (attributes != null) {
                        statistics.put("companyName", getAttributeValue(attributes, "company_name"));
                        statistics.put("storeName", getAttributeValue(attributes, "store_name"));
                        statistics.put("userType", getAttributeValue(attributes, "user_type"));
                    }

                    statistics.put("loginCount", 0);
                    statistics.put("lastLoginDate", null);

                    return statistics;
                });
    }

    public Mono<Void> updateCompanyProfile(String email, UpdateCompanyRequest request) {
        return findUserByEmail(email)
                .flatMap(user -> Mono.fromCallable(() -> {
                    try {
                        validateUpdateCompanyRequest(request);

                        UserRepresentation userToUpdate = new UserRepresentation();
                        userToUpdate.setId(user.getId());
                        userToUpdate.setUsername(user.getUsername());
                        userToUpdate.setEmail(user.getEmail());
                        userToUpdate.setEmailVerified(user.isEmailVerified());
                        userToUpdate.setEnabled(user.isEnabled());

                        Map<String, List<String>> attributes = user.getAttributes() != null ?
                                new HashMap<>(user.getAttributes()) : new HashMap<>();

                        updateAttributeIfProvided(attributes, "company_name", request.getCompanyName());
                        updateAttributeIfProvided(attributes, "store_name", request.getStoreName());
                        updateAttributeIfProvided(attributes, "business_registration_number", request.getBusinessRegistrationNumber());
                        updateAttributeIfProvided(attributes, "city", request.getCity());
                        updateAttributeIfProvided(attributes, "address", request.getAddress());

                        if (request.getPhone() != null) {
                            attributes.put("phone_number", Collections.singletonList(request.getPhone()));
                            attributes.put("phone", Collections.singletonList(request.getPhone()));
                        }
                        updateAttributeIfProvided(attributes, "contact_email", request.getContactEmail());

                        attributes.put("last_updated", Collections.singletonList(String.valueOf(System.currentTimeMillis())));

                        userToUpdate.setAttributes(attributes);

                        keycloak.realm(realm).users().get(user.getId()).update(userToUpdate);

                        log.info("Company profile updated successfully for user: {}", email);
                        return null;

                    } catch (Exception e) {
                        log.error("Error updating company profile for user {}: {}", email, e.getMessage(), e);
                        throw new KeycloakOperationException("updateCompanyProfile", "Failed to update company profile: " + e.getMessage());
                    }
                }))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<?> getProfile(Jwt jwt) {
        if (jwt == null) {
            throw new UserNotFoundException("JWT not found");
        }
        return findUserByEmail(jwt.getClaimAsString("email"))
                .map(user -> {
                    Map<String, List<String>> attributes = user.getAttributes();
                    if (user.getAttributes() == null) {
                        throw new BusinessException("No attributes found");
                    }

                    if (Objects.equals(getAttributeValue(attributes, "user_type"), "COMPANY")) {
                        return CompanyInfoResponse.builder()
                                .id(jwt.getId())
                                .companyName(getAttributeValue(attributes, "company_name"))
                                .email(user.getEmail())
                                .storeName(getAttributeValue(attributes, "store_name"))
                                .businessRegistrationNumber(getAttributeValue(attributes, "business_registration_number"))
                                .city(getAttributeValue(attributes, "city"))
                                .address(getAttributeValue(attributes, "address"))
                                .phone(getAttributeValue(attributes, "phone"))
                                .userType(getAttributeValue(attributes, "user_type"))
                                .roles(getRoles(jwt))
                                .build();
                    } else {
                        return UserInfoResponse.builder()
                                .id(jwt.getId())
                                .email(jwt.getClaimAsString("email"))
                                .name(jwt.getClaimAsString("name"))
                                .phone(jwt.getClaimAsString("phone"))
                                .userType(getAttributeValue(attributes, "user_type"))
                                .roles(getRoles(jwt))
                                .build();
                    }
                });
    }

    public Mono<Void> updateUserProfile(String email, UpdateUserRequest request) {
        return findUserByEmail(email)
                .flatMap(user -> Mono.fromCallable(() -> {
                    try {
                        validateUpdateUserRequest(request);

                        UserRepresentation userToUpdate = new UserRepresentation();
                        userToUpdate.setId(user.getId());
                        userToUpdate.setUsername(user.getUsername());
                        userToUpdate.setEmailVerified(user.isEmailVerified());
                        userToUpdate.setEnabled(user.isEnabled());

                        String newEmail = request.getEmail();
                        if (newEmail != null && !newEmail.trim().isEmpty() && !newEmail.equals(user.getEmail())) {
                            if (isEmailAlreadyInUse(newEmail, user.getId())) {
                                throw new UserAlreadyExistsException(newEmail);
                            }
                            userToUpdate.setEmail(newEmail.trim());
                            userToUpdate.setUsername(newEmail.trim());
                        } else {
                            userToUpdate.setEmail(user.getEmail());
                            userToUpdate.setUsername(user.getUsername());
                        }

                        Map<String, List<String>> attributes = user.getAttributes() != null ?
                                new HashMap<>(user.getAttributes()) : new HashMap<>();

                        if (request.getName() != null && !request.getName().trim().isEmpty()) {
                            String newName = request.getName().trim();
                            userToUpdate.setFirstName(newName);
                            attributes.put("name", Collections.singletonList(newName));
                        } else {
                            userToUpdate.setFirstName(user.getFirstName());
                        }

                        updateAttributeIfProvided(attributes, "phone", request.getPhone());

                        attributes.put("last_updated", Collections.singletonList(String.valueOf(System.currentTimeMillis())));

                        userToUpdate.setAttributes(attributes);

                        keycloak.realm(realm).users().get(user.getId()).update(userToUpdate);

                        log.info("User profile updated successfully for user: {}", email);
                        return null;

                    } catch (Exception e) {
                        log.error("Error updating user profile for user {}: {}", email, e.getMessage(), e);
                        if (e instanceof UserAlreadyExistsException || e instanceof BusinessException) {
                            throw e;
                        }
                        throw new KeycloakOperationException("updateUserProfile", "Failed to update user profile: " + e.getMessage());
                    }
                }))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private void updateAttributeIfProvided(Map<String, List<String>> attributes, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            attributes.put(key, Collections.singletonList(value.trim()));
        }
    }

    private void validateUpdateCompanyRequest(UpdateCompanyRequest request) {
        if (!request.hasAnyUpdates()) {
            throw new BusinessException("At least one field must be provided for update", HttpStatus.BAD_REQUEST);
        }

        if (request.getBusinessRegistrationNumber() != null) {
            if (!isValidBulstatFormat(request.getBusinessRegistrationNumber())) {
                throw new BusinessException("Invalid business registration number format", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private boolean isValidBulstatFormat(String bulstat) {
        if (bulstat == null || bulstat.trim().isEmpty()) {
            return false;
        }

        String cleanBulstat = bulstat.trim();
        java.util.regex.Pattern bulstatPattern = java.util.regex.Pattern.compile("^[0-9]{9}([0-9]{4})?$");
        return bulstatPattern.matcher(cleanBulstat).matches();
    }

    private boolean isEmailAlreadyInUse(String email, String currentUserId) {
        try {
            UsersResource usersResource = keycloak.realm(realm).users();
            List<UserRepresentation> existingUsers = usersResource.search(email, true);

            return existingUsers.stream()
                    .anyMatch(user -> !user.getId().equals(currentUserId));
        } catch (Exception e) {
            log.error("Error checking if email is in use: {}", e.getMessage());
            return true;
        }
    }

    private void validateUpdateUserRequest(UpdateUserRequest request) {
        if (!request.hasAnyUpdates()) {
            throw new BusinessException("At least one field must be provided for update", HttpStatus.BAD_REQUEST);
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim();
            Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
            if (!emailPattern.matcher(email).matches()) {
                throw new BusinessException("Invalid email format", HttpStatus.BAD_REQUEST);
            }
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String phone = request.getPhone().trim();
            Pattern phonePattern = Pattern.compile("^[+]?[0-9\\s\\-\\(\\)]{7,20}$");
            if (!phonePattern.matcher(phone).matches()) {
                throw new BusinessException("Invalid phone number format", HttpStatus.BAD_REQUEST);
            }
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String name = request.getName().trim();
            if (name.length() < 2) {
                throw new BusinessException("Name must be at least 2 characters long", HttpStatus.BAD_REQUEST);
            }
            if (name.length() > 100) {
                throw new BusinessException("Name must not exceed 100 characters", HttpStatus.BAD_REQUEST);
            }
        }
    }

    public Mono<Void> changePassword(String email, String currentPassword, String newPassword) {
        return Mono.fromCallable(() -> {
            try {
                if (newPassword == null || newPassword.length() < 8) {
                    throw new BusinessException("New password must be at least 8 characters long", HttpStatus.BAD_REQUEST);
                }

                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail(email);
                loginRequest.setPassword(currentPassword);

                authenticateUser(loginRequest).block();

                UserRepresentation user = findUserByEmail(email).block();
                if (user == null) {
                    throw new UserNotFoundException(email);
                }

                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(newPassword);
                credential.setTemporary(false);

                keycloak.realm(realm).users().get(user.getId()).resetPassword(credential);

                log.info("Password changed successfully for user: {}", email);
                return null;

            } catch (Exception e) {
                log.error("Error changing password for user {}: {}", email, e.getMessage());
                if (e instanceof BusinessException || e instanceof UserNotFoundException || e instanceof WrongPasswordException) {
                    throw e;
                }
                throw new KeycloakOperationException("changePassword", "Failed to change password: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private List<String> getRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            return (List<String>) realmAccess.get("roles");
        }
        return List.of();
    }

    private String getAttributeValue(Map<String, List<String>> attributes, String key) {
        List<String> values = attributes.get(key);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    private boolean hasAttribute(Map<String, List<String>> attributes, String key) {
        List<String> values = attributes.get(key);
        return values != null && !values.isEmpty() &&
                !values.get(0).trim().isEmpty();
    }

    private int calculateProfileCompleteness(UserRepresentation user) {
        int totalFields = 10;
        int completedFields = 0;

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) completedFields++;
        if (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) completedFields++;
        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) completedFields++;

        Map<String, List<String>> attributes = user.getAttributes();
        if (attributes != null) {
            if (hasAttribute(attributes, "company_name")) completedFields++;
            if (hasAttribute(attributes, "store_name")) completedFields++;
            if (hasAttribute(attributes, "business_registration_number")) completedFields++;
            if (hasAttribute(attributes, "city")) completedFields++;
            if (hasAttribute(attributes, "address")) completedFields++;
            if (hasAttribute(attributes, "phone_number")) completedFields++;
            if (hasAttribute(attributes, "user_type")) completedFields++;
        }

        return (int) Math.round((double) completedFields / totalFields * 100);
    }

    private static UserRepresentation getUserRepresentation(RegisterRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getName());
        user.setEmailVerified(true);

        user.setAttributes(Map.of(
                "name", List.of(request.getName()),
                "phone", List.of(request.getPhone()),
                "user_type", List.of("USER")
        ));


        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));
        return user;
    }

    private void setRole(Response response, UsersResource usersResource) {
        try {
            String userId = CreatedResponseUtil.getCreatedId(response);
            log.info("Setting role for user ID: {}", userId);

            int count = (int) usersResource.list()
                    .stream()
                    .filter(u -> u.getServiceAccountClientId() == null)
                    .count();

            String roleName = (count == 1) ? "admin" : "user";
            log.info("Assigning role: {} (user count: {})", roleName, count);

            try {
                var realmRoles = keycloak.realm(realm).roles().list();
                log.info("Available roles: {}", realmRoles.stream().map(RoleRepresentation::getName).toList());
            } catch (Exception e) {
                log.error("Cannot access realm roles: {}", e.getMessage());
                throw new KeycloakOperationException("realmAccess", "Cannot access realm: " + e.getMessage());
            }

            RoleRepresentation role;
            try {
                role = keycloak.realm(realm)
                        .roles()
                        .get(roleName)
                        .toRepresentation();
                log.info("Retrieved role: {}", role.getName());
            } catch (Exception e) {
                log.error("Cannot get role '{}': {}", roleName, e.getMessage());
                throw new KeycloakOperationException("getRole", "Role '" + roleName + "' not found: " + e.getMessage());
            }

            UserResource userResource = usersResource.get(userId);
            userResource.roles()
                    .realmLevel()
                    .add(Collections.singletonList(role));

            log.info("Role '{}' assigned successfully to user: {}", roleName, userId);

        } catch (Exception e) {
            log.error("Error in setRole: {}", e.getMessage(), e);
        }
    }
}