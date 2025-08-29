package com.platform.ads.service;

import com.platform.ads.dto.BoatAdResponse;
import com.platform.ads.dto.CategoryStatsResponse;
import com.platform.ads.dto.UserStatisticsResponse;
import com.platform.ads.exception.AdNotFoundException;
import com.platform.ads.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoatMarketplaceAdminService {

    private final AdRepository adRepository;
    private final BoatMarketplaceService marketplaceService;

    // ===========================
    // PENDING APPROVAL MANAGEMENT
    // ===========================

    public Flux<BoatAdResponse> getPendingApprovalAds() {
        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN SERVICE: GET PENDING APPROVAL ADS START ===");

        return adRepository.findByApprovalStatus("PENDING")
                .doOnNext(ad -> log.debug("=== PENDING AD FOUND === AdID: {}, Title: '{}', Created: {} ===",
                        ad.getId(), ad.getTitle(), ad.getCreatedAt()))
                .flatMap(ad -> {
                    log.debug("=== MAPPING PENDING AD === AdID: {} ===", ad.getId());
                    return marketplaceService.mapToResponse(ad);
                })
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== ADMIN SERVICE: GET PENDING APPROVAL ADS COMPLETE === Duration: {}ms ===", duration);
                });
    }

    public Mono<Void> approveAd(Long adId, Boolean approved, String adminUserId, String rejectionReason) {
        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN SERVICE: APPROVE AD START === AdID: {}, Approved: {}, AdminUser: {} ===",
                adId, approved, adminUserId);

        return adRepository.findById(adId)
                .switchIfEmpty(Mono.error(new AdNotFoundException(adId)))
                .flatMap(ad -> {
                    if (!"PENDING".equals(ad.getApprovalStatus())) {
                        log.warn("=== ADMIN SERVICE: AD NOT PENDING === AdID: {}, CurrentStatus: {} ===",
                                adId, ad.getApprovalStatus());
                        return Mono.error(new IllegalStateException("Advertisement is not pending approval"));
                    }

                    String newStatus = approved ? "APPROVED" : "REJECTED";
                    LocalDateTime approvedAt = LocalDateTime.now();

                    log.info("=== ADMIN SERVICE: UPDATING APPROVAL === AdID: {}, OldStatus: {}, NewStatus: {} ===",
                            adId, ad.getApprovalStatus(), newStatus);

                    return adRepository.updateApprovalStatus(adId, newStatus, adminUserId, approvedAt, rejectionReason)
                            .doOnSuccess(result -> {
                                long duration = System.currentTimeMillis() - startTime;
                                log.info("=== ADMIN SERVICE: APPROVE AD SUCCESS === AdID: {}, Status: {}, Duration: {}ms ===",
                                        adId, newStatus, duration);
                            });
                })
                .onErrorResume(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== ADMIN SERVICE: APPROVE AD ERROR === AdID: {}, Duration: {}ms, Error: {} ===",
                            adId, duration, error.getMessage(), error);
                    return Mono.error(error);
                });
    }

    // ===========================
    // FEATURED AD MANAGEMENT
    // ===========================

    public Mono<Void> toggleFeaturedStatus(Long adId, Boolean featured) {
        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN SERVICE: TOGGLE FEATURED START === AdID: {}, Featured: {} ===", adId, featured);

        return adRepository.findById(adId)
                .switchIfEmpty(Mono.error(new AdNotFoundException(adId)))
                .flatMap(ad -> {
                    if (ad.getFeatured().equals(featured)) {
                        log.info("=== ADMIN SERVICE: FEATURED STATUS UNCHANGED === AdID: {}, Status: {} ===",
                                adId, featured);
                        return Mono.empty();
                    }

                    log.info("=== ADMIN SERVICE: UPDATING FEATURED STATUS === AdID: {}, OldStatus: {}, NewStatus: {} ===",
                            adId, ad.getFeatured(), featured);

                    return adRepository.updateFeaturedStatus(adId, featured)
                            .doOnSuccess(result -> {
                                long duration = System.currentTimeMillis() - startTime;
                                log.info("=== ADMIN SERVICE: TOGGLE FEATURED SUCCESS === AdID: {}, Duration: {}ms ===",
                                        adId, duration);
                            });
                })
                .onErrorResume(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== ADMIN SERVICE: TOGGLE FEATURED ERROR === AdID: {}, Duration: {}ms, Error: {} ===",
                            adId, duration, error.getMessage(), error);
                    return Mono.error(error);
                });
    }

    // ===========================
    // STATISTICS GENERATION
    // ===========================

    public Mono<CategoryStatsResponse> getCategoryStatistics() {
        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN SERVICE: GET CATEGORY STATS START ===");

        return Mono.zip(
                getCategoryTotals(),
                getCategoryActiveTotals(),
                getCategoryAverages()
        ).map(tuple -> {
            Map<String, Long> totalByCategory = tuple.getT1();
            Map<String, Long> activeByCategory = tuple.getT2();
            Map<String, java.math.BigDecimal> avgPriceByCategory = tuple.getT3();

            long duration = System.currentTimeMillis() - startTime;
            log.info("=== ADMIN SERVICE: CATEGORY STATS AGGREGATED === Categories: {}, Duration: {}ms ===",
                    totalByCategory.size(), duration);

            return CategoryStatsResponse.builder()
                    .totalByCategory(totalByCategory)
                    .activeByCategory(activeByCategory)
                    .averagePriceByCategory(avgPriceByCategory)
                    .build();
        });
    }

    private Mono<Map<String, Long>> getCategoryTotals() {
        return adRepository.countByCategory()
                .collectMap(
                        result -> (String) result.get("category"),
                        result -> ((Number) result.get("count")).longValue()
                )
                .doOnNext(totals -> log.debug("=== CATEGORY TOTALS === {}", totals));
    }

    private Mono<Map<String, Long>> getCategoryActiveTotals() {
        return adRepository.countActiveByCategory()
                .collectMap(
                        result -> (String) result.get("category"),
                        result -> ((Number) result.get("count")).longValue()
                )
                .doOnNext(activeTotals -> log.debug("=== CATEGORY ACTIVE TOTALS === {}", activeTotals));
    }

    private Mono<Map<String, java.math.BigDecimal>> getCategoryAverages() {
        return adRepository.getAveragePriceByCategory()
                .collectMap(
                        result -> (String) result.get("category"),
                        result -> (java.math.BigDecimal) result.get("avg_price")
                )
                .doOnNext(avgPrices -> log.debug("=== CATEGORY AVERAGE PRICES === {}", avgPrices));
    }

    public Mono<UserStatisticsResponse> getUserStatistics() {
        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN SERVICE: GET USER STATS START ===");

        return Mono.zip(
                getTopSellerStats(),
                getRecentRegistrationStats(),
                getApprovalStatusStats()
        ).map(tuple -> {
            Map<String, Long> topSellers = tuple.getT1();
            Map<String, Long> recentRegistrations = tuple.getT2();
            Map<String, Long> approvalStats = tuple.getT3();

            long duration = System.currentTimeMillis() - startTime;
            log.info("=== ADMIN SERVICE: USER STATS COMPLETE === TopSellers: {}, Duration: {}ms ===",
                    topSellers.size(), duration);

            return UserStatisticsResponse.builder()
                    .topSellersByAdCount(topSellers)
                    .adCountsByDate(recentRegistrations)
                    .approvalStatusBreakdown(approvalStats)
                    .build();
        });
    }

    private Mono<Map<String, Long>> getTopSellerStats() {
        return adRepository.getTopSellersByAdCount(10)
                .collectMap(
                        result -> (String) result.get("user_email"),
                        result -> ((Number) result.get("ad_count")).longValue()
                )
                .doOnNext(sellers -> log.debug("=== TOP SELLERS === {}", sellers));
    }

    private Mono<Map<String, Long>> getRecentRegistrationStats() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return adRepository.getAdCountsByDate(thirtyDaysAgo)
                .collectMap(
                        result -> result.get("date").toString(),
                        result -> ((Number) result.get("count")).longValue()
                )
                .doOnNext(registrations -> log.debug("=== RECENT REGISTRATIONS === {}", registrations));
    }

    private Mono<Map<String, Long>> getApprovalStatusStats() {
        return adRepository.countByApprovalStatus()
                .collectMap(
                        result -> (String) result.get("approval_status"),
                        result -> ((Number) result.get("count")).longValue()
                )
                .doOnNext(approvals -> log.debug("=== APPROVAL STATUS BREAKDOWN === {}", approvals));
    }
}
