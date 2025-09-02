package com.platform.ads.service;

import com.platform.ads.dto.BoatAdResponse;
import com.platform.ads.dto.UpdateAdRequest;
import com.platform.ads.dto.UserAdsStatisticsResponse;
import com.platform.ads.dto.enums.MainBoatCategory;
import com.platform.ads.entity.Ad;
import com.platform.ads.exception.AdNotFoundException;
import com.platform.ads.repository.AdImageRepository;
import com.platform.ads.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdsService {

    private final AdRepository adRepository;
    private final AdImageRepository imageRepository;
    private final ImageService imageService;
    private final BoatMarketplaceService marketplaceService;

    // ===========================
    // USER'S ADS MANAGEMENT
    // ===========================

    public Flux<BoatAdResponse> getUserAds(String userId) {
        long startTime = System.currentTimeMillis();
        log.info("=== GET USER ADS START === UserId: {} ===", userId);

        return adRepository.findByUserId(userId)
                .doOnNext(ad -> log.debug("=== USER AD FOUND === AdID: {}, Title: '{}', Active: {} ===",
                        ad.getId(), ad.getTitle(), ad.getActive()))
                .flatMap(ad -> {
                    log.debug("=== MAPPING USER AD === AdID: {} ===", ad.getId());
                    return marketplaceService.mapToResponse(ad);
                })
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== GET USER ADS COMPLETE === UserId: {}, Duration: {}ms ===", userId, duration);
                });
    }

    public Flux<BoatAdResponse> getUserActiveAds(String userId) {
        long startTime = System.currentTimeMillis();
        log.info("=== GET USER ACTIVE ADS === UserId: {} ===", userId);

        return adRepository.findByUserIdAndActive(userId, true)
                .flatMap(marketplaceService::mapToResponse)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== USER ACTIVE ADS COMPLETE === UserId: {}, Duration: {}ms ===", userId, duration);
                });
    }

    public Flux<BoatAdResponse> getUserAdsByCategory(String userId, MainBoatCategory category) {
        long startTime = System.currentTimeMillis();
        log.info("=== GET USER ADS BY CATEGORY === UserId: {}, Category: {} ===", userId, category);

        return adRepository.findByUserIdAndCategory(userId, category.name())
                .flatMap(marketplaceService::mapToResponse)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== USER CATEGORY ADS COMPLETE === UserId: {}, Category: {}, Duration: {}ms ===",
                            userId, category, duration);
                });
    }

    public Flux<BoatAdResponse> getUserAdsWithFilters(String userId, Boolean active, MainBoatCategory category, String sortBy) {
        long startTime = System.currentTimeMillis();
        log.info("=== GET USER ADS WITH FILTERS === UserId: {}, Active: {}, Category: {}, SortBy: {} ===",
                userId, active, category, sortBy);

        return adRepository.findUserAdsWithFilters(
                        userId,
                        active,
                        category != null ? category.name() : null,
                        sortBy
                )
                .flatMap(marketplaceService::mapToResponse)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== USER FILTERED ADS COMPLETE === UserId: {}, Duration: {}ms ===", userId, duration);
                });
    }

    public Mono<UserAdsStatisticsResponse> getUserStatistics(String userId) {
        long startTime = System.currentTimeMillis();
        log.info("=== GET USER STATISTICS === UserId: {} ===", userId);

        return Mono.zip(
                adRepository.countByUserId(userId),
                adRepository.countActiveByUserId(userId),
                adRepository.findMostViewedByUserId(userId)
                        .map(Ad::getViewsCount)
                        .defaultIfEmpty(0),
                getTotalViewsForUser(userId)
        ).map(tuple -> {
            long totalAds = tuple.getT1();
            long activeAds = tuple.getT2();
            int mostViewedCount = tuple.getT3();
            long totalViews = tuple.getT4();

            long duration = System.currentTimeMillis() - startTime;
            log.info("=== USER STATS SUCCESS === UserId: {}, TotalAds: {}, ActiveAds: {}, TotalViews: {}, Duration: {}ms ===",
                    userId, totalAds, activeAds, totalViews, duration);

            return UserAdsStatisticsResponse.builder()
                    .userId(userId)
                    .totalAds(totalAds)
                    .activeAds(activeAds)
                    .inactiveAds(totalAds - activeAds)
                    .totalViews(totalViews)
                    .mostViewedAdViews(mostViewedCount)
                    .averageViewsPerAd(totalAds > 0 ? (double) totalViews / totalAds : 0.0)
                    .build();
        });
    }

    private Mono<Long> getTotalViewsForUser(String userId) {
        return adRepository.findByUserId(userId)
                .map(ad -> ad.getViewsCount() != null ? ad.getViewsCount().longValue() : 0L)
                .reduce(0L, Long::sum);
    }

    // ===========================
    // AD MANAGEMENT ACTIONS
    // ===========================

    public Mono<Void> deactivateUserAd(String userId, Long adId) {
        long startTime = System.currentTimeMillis();
        log.info("=== DEACTIVATE USER AD === UserId: {}, AdID: {} ===", userId, adId);

        return adRepository.findById(adId)
                .filter(ad -> userId.equals(ad.getUserId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Ad not found or not owned by user")))
                .flatMap(ad -> {
                    ad.setActive(false);
                    ad.setUpdatedAt(java.time.LocalDateTime.now());
                    return adRepository.save(ad);
                })
                .then()
                .doOnSuccess(result -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== DEACTIVATE AD SUCCESS === UserId: {}, AdID: {}, Duration: {}ms ===",
                            userId, adId, duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== DEACTIVATE AD ERROR === UserId: {}, AdID: {}, Duration: {}ms, Error: {} ===",
                            userId, adId, duration, error.getMessage());
                });
    }

    public Mono<Void> reactivateUserAd(String userId, Long adId) {
        long startTime = System.currentTimeMillis();
        log.info("=== REACTIVATE USER AD === UserId: {}, AdID: {} ===", userId, adId);

        return adRepository.findById(adId)
                .filter(ad -> userId.equals(ad.getUserId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Ad not found or not owned by user")))
                .flatMap(ad -> {
                    ad.setActive(true);
                    ad.setUpdatedAt(java.time.LocalDateTime.now());
                    return adRepository.save(ad);
                })
                .then()
                .doOnSuccess(result -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== REACTIVATE AD SUCCESS === UserId: {}, AdID: {}, Duration: {}ms ===",
                            userId, adId, duration);
                });
    }

    // ===========================
    // EDIT AD FUNCTIONALITY
    // ===========================

    @Transactional
    public Mono<BoatAdResponse> updateAd(String userId, Long adId, UpdateAdRequest updateRequest) {
        long startTime = System.currentTimeMillis();
        log.info("=== UPDATE AD START === UserId: {}, AdID: {}, Title: '{}' ===",
                userId, adId, updateRequest.getTitle());

        return validateAdOwnership(userId, adId)
                .flatMap(ad -> {
                    // Validate ad can be edited
                    if (Boolean.TRUE.equals(ad.getArchived())) {
                        log.warn("=== CANNOT EDIT ARCHIVED AD === AdID: {} ===", adId);
                        return Mono.error(new IllegalStateException("Cannot edit archived advertisement"));
                    }

                    log.info("=== UPDATING AD BASIC INFO === AdID: {}, OldTitle: '{}', NewTitle: '{}' ===",
                            adId, ad.getTitle(), updateRequest.getTitle());

                    LocalDateTime now = LocalDateTime.now();
                    return adRepository.updateAdBasicInfo(
                            adId, userId,
                            updateRequest.getTitle(),
                            updateRequest.getDescription(),
                            updateRequest.getQuickDescription(),
                            updateRequest.getPrice() != null ? updateRequest.getPrice().getAmount() : null,
                            updateRequest.getPrice() != null ? updateRequest.getPrice().getType().name() : null,
                            updateRequest.getPrice() != null ? updateRequest.getPrice().getIncludingVat() : null,
                            updateRequest.getLocation(),
                            now,
                            now
                    ).then(adRepository.findById(adId));
                })
                .flatMap(marketplaceService::mapToResponse)
                .doOnSuccess(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== UPDATE AD SUCCESS === UserId: {}, AdID: {}, Duration: {}ms ===",
                            userId, adId, duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== UPDATE AD ERROR === UserId: {}, AdID: {}, Duration: {}ms, Error: {} ===",
                            userId, adId, duration, error.getMessage());
                });
    }

    // ===========================
    // ENHANCED ARCHIVE WITH IMAGE HANDLING
    // ===========================

    @Transactional
    public Mono<Void> archiveAd(String userId, Long adId) {
        long startTime = System.currentTimeMillis();
        log.info("=== ARCHIVE AD START === UserId: {}, AdID: {} ===", userId, adId);

        return validateAdOwnership(userId, adId)
                .flatMap(ad -> {
                    if (Boolean.TRUE.equals(ad.getArchived())) {
                        log.warn("=== AD ALREADY ARCHIVED === AdID: {} ===", adId);
                        return Mono.error(new IllegalStateException("Advertisement is already archived"));
                    }

                    LocalDateTime now = LocalDateTime.now();
                    log.info("=== ARCHIVING AD (IMAGES REMAIN) === AdID: {}, Title: '{}' ===", adId, ad.getTitle());

                    // Note: When archiving, we keep images in S3 and database
                    // They're just not visible to public because ad is archived
                    return adRepository.archiveAd(adId, userId, now, now);
                })
                .doOnSuccess(result -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== ARCHIVE AD SUCCESS === UserId: {}, AdID: {}, Duration: {}ms ===",
                            userId, adId, duration);
                    log.info("=== NOTE: ARCHIVED AD IMAGES PRESERVED === AdID: {} ===", adId);
                });
    }

    @Transactional
    public Mono<Void> unarchiveAd(String userId, Long adId) {
        long startTime = System.currentTimeMillis();
        log.info("=== UNARCHIVE AD START === UserId: {}, AdID: {} ===", userId, adId);

        return validateAdOwnership(userId, adId)
                .flatMap(ad -> {
                    if (!Boolean.TRUE.equals(ad.getArchived())) {
                        log.warn("=== AD NOT ARCHIVED === AdID: {} ===", adId);
                        return Mono.error(new IllegalStateException("Advertisement is not archived"));
                    }

                    log.info("=== UNARCHIVING AD === AdID: {}, Title: '{}' ===", adId, ad.getTitle());
                    return adRepository.unarchiveAd(adId, userId, LocalDateTime.now());
                })
                .doOnSuccess(result -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== UNARCHIVE AD SUCCESS === UserId: {}, AdID: {}, Duration: {}ms ===",
                            userId, adId, duration);
                });
    }

    // ===========================
    // ENHANCED DELETE WITH COMPLETE CLEANUP
    // ===========================

    @Transactional
    public Mono<Void> deleteAd(String userId, Long adId) {
        long startTime = System.currentTimeMillis();
        log.info("=== DELETE AD WITH IMAGE CLEANUP START === UserId: {}, AdID: {} ===", userId, adId);

        return validateAdOwnership(userId, adId)
                .flatMap(ad -> {
                    // Business rule: Only allow deletion of ads with low interaction
                    if (ad.getViewsCount() != null && ad.getViewsCount() > 10) {
                        log.warn("=== HIGH INTERACTION AD DELETION BLOCKED === AdID: {}, Views: {} ===",
                                adId, ad.getViewsCount());
                        return Mono.error(new IllegalStateException(
                                "Cannot delete advertisement with high interaction. Consider archiving instead."));
                    }

                    log.info("=== STARTING COMPLETE AD DELETION === AdID: {}, Title: '{}', Views: {} ===",
                            adId, ad.getTitle(), ad.getViewsCount());

                    // Step 1: Delete all images from S3 and database
                    return deleteAllAdImagesComplete(adId)
                            // Step 2: Delete the ad (cascade will handle remaining DB relationships)
                            .then(adRepository.deleteByIdAndUserId(adId, userId))
                            .doOnSuccess(result -> {
                                long duration = System.currentTimeMillis() - startTime;
                                log.info("=== DELETE AD COMPLETE SUCCESS === UserId: {}, AdID: {}, Duration: {}ms ===",
                                        userId, adId, duration);
                            });
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== DELETE AD WITH CLEANUP ERROR === UserId: {}, AdID: {}, Duration: {}ms, Error: {} ===",
                            userId, adId, duration, error.getMessage(), error);
                });
    }

    /**
     * Deletes all images for an ad from both S3 and database
     */
    private Mono<Void> deleteAllAdImagesComplete(Long adId) {
        log.info("=== DELETING ALL AD IMAGES === AdID: {} ===", adId);

        return imageRepository.findByAdIdOrderByDisplayOrder(adId)
                .doOnNext(image -> log.debug("=== FOUND IMAGE TO DELETE === ImageID: {}, S3Key: '{}' ===",
                        image.getId(), image.getS3Key()))
                .flatMap(image -> {
                    // Delete from S3 first, then database record will be deleted by CASCADE
                    return imageService.deleteFromS3(image.getS3Key())
                            .doOnSuccess(result -> log.debug("=== S3 IMAGE DELETED === S3Key: '{}' ===", image.getS3Key()))
                            .onErrorResume(s3Error -> {
                                // Log S3 error but don't fail the entire deletion
                                log.error("=== S3 DELETE FAILED === S3Key: '{}', Error: {} ===",
                                        image.getS3Key(), s3Error.getMessage());
                                return Mono.empty(); // Continue with other deletions
                            });
                })
                .then()
                .doOnSuccess(result -> log.info("=== ALL AD IMAGES S3 CLEANUP COMPLETE === AdID: {} ===", adId));
    }

    // ===========================
    // ARCHIVE MANAGEMENT
    // ===========================

    public Flux<BoatAdResponse> getArchivedAds(String userId) {
        long startTime = System.currentTimeMillis();
        log.info("=== GET ARCHIVED ADS === UserId: {} ===", userId);

        return adRepository.findArchivedByUserId(userId)
                .doOnNext(ad -> log.debug("=== ARCHIVED AD FOUND === AdID: {}, Title: '{}', ArchivedAt: {} ===",
                        ad.getId(), ad.getTitle(), ad.getArchivedAt()))
                .flatMap(marketplaceService::mapToResponse)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== GET ARCHIVED ADS COMPLETE === UserId: {}, Duration: {}ms ===", userId, duration);
                });
    }

    public Flux<BoatAdResponse> getActiveAds(String userId) {
        long startTime = System.currentTimeMillis();
        log.info("=== GET ACTIVE ADS === UserId: {} ===", userId);

        return adRepository.findNonArchivedByUserId(userId)
                .filter(ad -> Boolean.TRUE.equals(ad.getActive()))
                .flatMap(marketplaceService::mapToResponse)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== GET ACTIVE ADS COMPLETE === UserId: {}, Duration: {}ms ===", userId, duration);
                });
    }

    // ===========================
    // HELPER METHODS
    // ===========================

    private Mono<Ad> validateAdOwnership(String userId, Long adId) {
        return adRepository.findById(adId)
                .switchIfEmpty(Mono.error(new AdNotFoundException(adId)))
                .flatMap(ad -> {
                    if (!userId.equals(ad.getUserId())) {
                        log.warn("=== AD OWNERSHIP VIOLATION === UserId: {}, AdID: {}, ActualOwner: {} ===",
                                userId, adId, ad.getUserId());
                        return Mono.error(new IllegalArgumentException("Advertisement not owned by user"));
                    }
                    log.debug("=== AD OWNERSHIP VALIDATED === UserId: {}, AdID: {} ===", userId, adId);
                    return Mono.just(ad);
                });
    }
}
