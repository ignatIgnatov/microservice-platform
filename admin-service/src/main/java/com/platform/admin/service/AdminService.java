package com.platform.admin.service;

import com.platform.admin.dto.UserDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private final WebClient webClient = WebClient.builder().build();

    public List<UserDto> getAllUsers() {
        Keycloak keycloak = getKeycloakAdminClient();
        List<UserRepresentation> users = keycloak.realm(realm).users().list();

        return users.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(String userId) {
        Keycloak keycloak = getKeycloakAdminClient();
        UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();
        return convertToUserDto(user);
    }

    public void enableUser(String userId) {
        Keycloak keycloak = getKeycloakAdminClient();
        UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();
        user.setEnabled(true);
        keycloak.realm(realm).users().get(userId).update(user);
    }

    public void disableUser(String userId) {
        Keycloak keycloak = getKeycloakAdminClient();
        UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();
        user.setEnabled(false);
        keycloak.realm(realm).users().get(userId).update(user);
    }

    public void deleteUser(String userId) {
        Keycloak keycloak = getKeycloakAdminClient();
        keycloak.realm(realm).users().get(userId).remove();
    }

    public Object getAllAds() {
        // Call ad-service to get all ads
        return webClient.get()
                .uri("http://ad-service:8083/ads")
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public void updateAdStatus(Long adId, String status) {
        // This would require implementing an admin endpoint in ad-service
        // For now, this is a placeholder
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", status);

        webClient.put()
                .uri("http://ad-service:8083/ads/" + adId + "/admin/status")
                .bodyValue(statusUpdate)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public Object getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Get user count
        Keycloak keycloak = getKeycloakAdminClient();
        int userCount = keycloak.realm(realm).users().count();
        stats.put("totalUsers", userCount);

        // Get ads stats (placeholder - would need actual implementation)
        stats.put("totalAds", 0);
        stats.put("activeAds", 0);
        stats.put("soldAds", 0);

        return stats;
    }

    private UserDto convertToUserDto(UserRepresentation user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedTimestamp(user.getCreatedTimestamp() != null ?
                user.getCreatedTimestamp().toString() : null);
        return dto;
    }

    private Keycloak getKeycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();
    }
}