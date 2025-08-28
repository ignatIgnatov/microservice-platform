package com.platform.ads.service;

import com.platform.ads.dto.*;
import com.platform.ads.model.Ad;
import com.platform.ads.repository.AdRepository;
import com.platform.ads.exception.UserNotFoundException;
import com.platform.ads.exception.AdNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AdService {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    private final AdRepository adRepository;
    private final WebClient webClient;

    public AdService(AdRepository adRepository, WebClient webClient) {
        this.adRepository = adRepository;
        this.webClient = webClient;
    }

    // Call auth-service to validate user
    public Mono<UserValidationResponse> validateUser(String email, String token) {
        return webClient.get()
                .uri(authServiceUrl + "/auth/validate-user?email=" + email)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    log.error("Auth service returned error status: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Auth service error: " + response.statusCode()));
                })
                .bodyToMono(UserValidationResponse.class)
                .timeout(Duration.ofSeconds(10)) // Add timeout
                .doOnError(WebClientResponseException.class, e -> {
                    log.error("WebClient error: Status {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
                })
                .onErrorResume(e -> {
                    log.error("Failed to validate user {}: {}", email, e.getMessage());
                    return Mono.error(new RuntimeException("User validation failed: " + e.getMessage()));
                });
    }

    // Check if user exists by calling auth-service
    public Mono<Boolean> userExistsByEmail(String email, String token) {
        return validateUser(email, token)
                .map(UserValidationResponse::isExists)
                .onErrorReturn(false);
    }

    // Create ad with user validation via auth-service
    public Mono<AdResponse> createAd(AdRequest request, String token) {
        return validateUser(request.getUserEmail(), token)
                .flatMap(userInfo -> {
                    if (!userInfo.isExists()) {
                        return Mono.error(new UserNotFoundException(request.getUserEmail()));
                    }

                    // Create the ad with user info from auth-service
                    Ad ad = Ad.builder()
                            .title(request.getTitle())
                            .description(request.getDescription())
                            .price(request.getPrice())
                            .category(request.getCategory())
                            .location(request.getLocation())
                            .userEmail(request.getUserEmail())
                            .userId(userInfo.getUserId())  // Get user ID from auth-service
                            .userFirstName(userInfo.getFirstName()) // Store user info for easier queries
                            .userLastName(userInfo.getLastName())
                            .createdAt(LocalDateTime.now())
                            .active(true)
                            .build();

                    return adRepository.save(ad);
                })
                .map(this::mapToResponse);
    }

    // Get ad by ID
    public Mono<AdResponse> getAdById(Long id) {
        return adRepository.findById(id)
                .map(this::mapToResponse);
    }

    // Get ads by user email (with validation)
    public Flux<AdResponse> getAdsByUserEmail(String userEmail, String token) {
        return userExistsByEmail(userEmail, token)
                .flatMapMany(exists -> {
                    if (!exists) {
                        return Flux.error(new UserNotFoundException(userEmail));
                    }
                    return adRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
                })
                .map(this::mapToResponse);
    }

    // Advanced search with multiple filters
    public Flux<AdResponse> searchAds(String query, String category, String location,
                                      BigDecimal minPrice, BigDecimal maxPrice) {
        return adRepository.searchAds(query, category, location, minPrice, maxPrice, true)
                .map(this::mapToResponse);
    }

    // Advanced search with request object
    public Flux<AdResponse> advancedSearchAds(AdSearchRequest request) {
        return adRepository.searchAds(
                request.getQuery(),
                request.getCategory(),
                request.getLocation(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getActive() != null ? request.getActive() : true
        ).map(this::mapToResponse);
    }

    // Get ads by category
    public Flux<AdResponse> getAdsByCategory(String category) {
        return adRepository.findByCategoryIgnoreCaseAndActiveOrderByCreatedAtDesc(category, true)
                .map(this::mapToResponse);
    }

    // Get user info with ads count
    public Mono<UserAdSummary> getUserAdSummary(String email, String token) {
        return validateUser(email, token)
                .flatMap(userInfo -> {
                    if (!userInfo.isExists()) {
                        return Mono.error(new UserNotFoundException(email));
                    }

                    return adRepository.countByUserEmail(email)
                            .map(count -> UserAdSummary.builder()
                                    .userId(userInfo.getUserId())
                                    .email(userInfo.getEmail())
                                    .firstName(userInfo.getFirstName())
                                    .lastName(userInfo.getLastName())
                                    .totalAds(count)
                                    .build());
                });
    }

    // Validate ad ownership
    public Mono<Boolean> isAdOwnedByUser(Long adId, String userEmail) {
        return adRepository.findById(adId)
                .switchIfEmpty(Mono.error(new AdNotFoundException(adId)))
                .map(ad -> ad.getUserEmail().equals(userEmail));
    }

    // Update ad with detailed request
    public Mono<AdResponse> updateAd(Long adId, AdUpdateRequest request, String currentUserEmail) {
        return isAdOwnedByUser(adId, currentUserEmail)
                .flatMap(isOwner -> {
                    if (!isOwner) {
                        return Mono.error(new RuntimeException("You can only update your own ads"));
                    }

                    return adRepository.findById(adId)
                            .switchIfEmpty(Mono.error(new AdNotFoundException(adId)))
                            .flatMap(ad -> {
                                ad.setTitle(request.getTitle());
                                ad.setDescription(request.getDescription());
                                ad.setPrice(request.getPrice());
                                ad.setCategory(request.getCategory());
                                ad.setLocation(request.getLocation());
                                if (request.getActive() != null) {
                                    ad.setActive(request.getActive());
                                }
                                ad.setUpdatedAt(LocalDateTime.now());

                                return adRepository.save(ad);
                            });
                })
                .map(this::mapToResponse);
    }

    // Update ad status only
    public Mono<AdResponse> updateAdStatus(Long adId, Boolean active, String currentUserEmail) {
        return isAdOwnedByUser(adId, currentUserEmail)
                .flatMap(isOwner -> {
                    if (!isOwner) {
                        return Mono.error(new RuntimeException("You can only update your own ads"));
                    }

                    return adRepository.findById(adId)
                            .switchIfEmpty(Mono.error(new AdNotFoundException(adId)))
                            .flatMap(ad -> {
                                ad.setActive(active);
                                ad.setUpdatedAt(LocalDateTime.now());
                                return adRepository.save(ad);
                            });
                })
                .map(this::mapToResponse);
    }

    // Delete ad with user validation
    public Mono<Void> deleteAd(Long adId, String currentUserEmail) {
        return isAdOwnedByUser(adId, currentUserEmail)
                .flatMap(isOwner -> {
                    if (!isOwner) {
                        return Mono.error(new RuntimeException("You can only delete your own ads"));
                    }

                    return adRepository.deleteById(adId);
                });
    }

    // Get all ads (public endpoint)
    public Flux<AdResponse> getAllAds() {
        return adRepository.findByActiveOrderByCreatedAtDesc(true)
                .map(this::mapToResponse);
    }

    // Get detailed user statistics
    public Mono<UserAdStatsResponse> getUserAdStats(String userEmail, String token) {
        return validateUser(userEmail, token)
                .flatMap(userInfo -> {
                    if (!userInfo.isExists()) {
                        return Mono.error(new UserNotFoundException(userEmail));
                    }

                    return Mono.zip(
                            adRepository.countByUserEmail(userEmail),
                            adRepository.countActiveAdsByUser(userEmail),
                            adRepository.countInactiveAdsByUser(userEmail),
                            adRepository.getTotalValueByUser(userEmail).defaultIfEmpty(BigDecimal.ZERO),
                            adRepository.getAveragePriceByUser(userEmail).defaultIfEmpty(BigDecimal.ZERO)
                    ).map(tuple -> UserAdStatsResponse.builder()
                            .userId(userInfo.getUserId())
                            .userEmail(userInfo.getEmail())
                            .userFullName(userInfo.getFirstName() + " " + userInfo.getLastName())
                            .totalAds(tuple.getT1())
                            .activeAds(tuple.getT2())
                            .inactiveAds(tuple.getT3())
                            .totalValue(tuple.getT4())
                            .averagePrice(tuple.getT5())
                            .build());
                });
    }

    // Get general statistics
    public Mono<AdSummaryResponse> getGeneralStats() {
        return Mono.zip(
                adRepository.count(),
                adRepository.countActiveAds(),
                adRepository.countInactiveAds(),
                adRepository.getAveragePrice().defaultIfEmpty(BigDecimal.ZERO),
                adRepository.getMinPrice().defaultIfEmpty(BigDecimal.ZERO),
                adRepository.getMaxPrice().defaultIfEmpty(BigDecimal.ZERO)
        ).map(tuple -> AdSummaryResponse.builder()
                .totalAds(tuple.getT1())
                .activeAds(tuple.getT2())
                .inactiveAds(tuple.getT3())
                .averagePrice(tuple.getT4())
                .minPrice(tuple.getT5())
                .maxPrice(tuple.getT6())
                .build());
    }

    // Get categories with statistics
    public Flux<CategoryResponse> getCategories() {
        return adRepository.getCategoryStatistics()
                .map(row -> CategoryResponse.builder()
                        .name((String) row[0])
                        .adCount((Long) row[1])
                        .averagePrice(BigDecimal.ZERO) // You can add this calculation
                        .build());
    }

    // Get recent ads
    public Flux<AdResponse> getRecentAds(int limit) {
        return adRepository.findByActiveOrderByCreatedAtDesc(true)
                .take(limit)
                .map(this::mapToResponse);
    }

    // Get popular ads (placeholder - you can implement your own logic)
    public Flux<AdResponse> getPopularAds(int limit) {
        // For now, just return recent ads. You can implement views/likes logic later
        return getRecentAds(limit);
    }

    private AdResponse mapToResponse(Ad ad) {
        return AdResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .category(ad.getCategory())
                .location(ad.getLocation())
                .userEmail(ad.getUserEmail())
                .userId(ad.getUserId())
                .userFirstName(ad.getUserFirstName())
                .userLastName(ad.getUserLastName())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .active(ad.getActive())
                .build();
    }

    // Inner DTOs
    public static class UserValidationResponse {
        private boolean exists;
        private String userId;
        private String email;
        private String firstName;
        private String lastName;

        // Constructors
        public UserValidationResponse() {}

        // Getters and setters
        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    public static class UserAdSummary {
        private String userId;
        private String email;
        private String firstName;
        private String lastName;
        private Long totalAds;

        public static UserAdSummaryBuilder builder() {
            return new UserAdSummaryBuilder();
        }

        public static class UserAdSummaryBuilder {
            private String userId;
            private String email;
            private String firstName;
            private String lastName;
            private Long totalAds;

            public UserAdSummaryBuilder userId(String userId) {
                this.userId = userId;
                return this;
            }

            public UserAdSummaryBuilder email(String email) {
                this.email = email;
                return this;
            }

            public UserAdSummaryBuilder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }

            public UserAdSummaryBuilder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }

            public UserAdSummaryBuilder totalAds(Long totalAds) {
                this.totalAds = totalAds;
                return this;
            }

            public UserAdSummary build() {
                UserAdSummary summary = new UserAdSummary();
                summary.userId = this.userId;
                summary.email = this.email;
                summary.firstName = this.firstName;
                summary.lastName = this.lastName;
                summary.totalAds = this.totalAds;
                return summary;
            }
        }

        public String getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public Long getTotalAds() { return totalAds; }
    }
}