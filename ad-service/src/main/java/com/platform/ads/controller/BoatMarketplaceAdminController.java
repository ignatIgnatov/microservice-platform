package com.platform.ads.controller;

import com.platform.ads.dto.BoatAdResponse;
import com.platform.ads.dto.CategoryStatsResponse;
import com.platform.ads.dto.DetailedStatsResponse;
import com.platform.ads.dto.UserStatisticsResponse;
import com.platform.ads.exception.AdNotFoundException;
import com.platform.ads.service.BoatMarketplaceAdminService;
import com.platform.ads.service.BoatMarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(
        name = "Admin - Boat Marketplace",
        description = "Administrative operations for QHTI.BG Boat Marketplace. Requires ADMIN role for all operations."
)
@SecurityRequirement(name = "bearerAuth")
public class BoatMarketplaceAdminController {

    private final BoatMarketplaceService marketplaceService;
    private final BoatMarketplaceAdminService adminService;

    // Admin statistics with detailed breakdown
    @GetMapping("/stats/detailed")
    @Operation(
            summary = "Get detailed marketplace statistics",
            description = "Retrieves comprehensive marketplace statistics including totals, breakdowns by category, location analysis, and pricing insights. Only accessible by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Detailed statistics retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DetailedStatsResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while generating statistics",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<DetailedStatsResponse>> getDetailedStats() {
        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN: GET DETAILED STATS REQUEST START ===");

        return marketplaceService.getMarketplaceStats()
                .map(stats -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== ADMIN: BASIC STATS RETRIEVED === TotalAds: {}, ActiveAds: {}, InactiveAds: {}, Duration: {}ms ===",
                            stats.getTotalAds(), stats.getActiveAds(), stats.getInactiveAds(), duration);

                    // TODO: In a complete implementation, you would fetch additional statistics here
                    // such as category breakdowns, location analysis, etc.
                    DetailedStatsResponse detailedStats = DetailedStatsResponse.builder()
                            .totalAds(stats.getTotalAds())
                            .activeAds(stats.getActiveAds())
                            .inactiveAds(stats.getInactiveAds())
                            .averagePrice(stats.getAveragePrice())
                            .minPrice(stats.getMinPrice())
                            .maxPrice(stats.getMaxPrice())
                            // Additional fields would be populated here:
                            // .adsByCategory(categoryBreakdown)
                            // .adsByLocation(locationBreakdown)
                            // .averagePriceByCategory(categoryPriceAnalysis)
                            .build();

                    log.info("=== ADMIN: DETAILED STATS BUILT === AvgPrice: {}, PriceRange: {}-{} ===",
                            stats.getAveragePrice(), stats.getMinPrice(), stats.getMaxPrice());

                    return detailedStats;
                })
                .map(detailedStats -> {
                    long totalDuration = System.currentTimeMillis() - startTime;
                    log.info("=== ADMIN: GET DETAILED STATS SUCCESS === TotalDuration: {}ms ===", totalDuration);
                    return ResponseEntity.ok(detailedStats);
                })
                .onErrorResume(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== ADMIN: GET DETAILED STATS ERROR === Duration: {}ms, Error: {} ===",
                            duration, error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    // Featured ad management
    @PutMapping("/ads/{id}/feature")
    @Operation(
            summary = "Toggle featured status of advertisement",
            description = "Updates the featured status of a specific advertisement. Featured ads appear prominently in search results and on the homepage."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Featured status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid advertisement ID or featured parameter",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Advertisement not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while updating featured status",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> toggleFeaturedAd(
            @Parameter(
                    description = "Advertisement ID to update",
                    required = true,
                    example = "123"
            )
            @PathVariable @NotNull @Min(1) Long id,

            @Parameter(
                    description = "New featured status (true = featured, false = not featured)",
                    required = true,
                    example = "true"
            )
            @RequestParam @NotNull Boolean featured) {

        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN: TOGGLE FEATURED AD START === AdID: {}, NewStatus: {} ===", id, featured);

        // First verify the ad exists
        return marketplaceService.getAdById(id)
                .flatMap(existingAd -> {
                    log.info("=== ADMIN: AD FOUND FOR FEATURING === AdID: {}, Title: '{}', CurrentFeatured: {} ===",
                            id, existingAd.getTitle(), existingAd.getFeatured());

                    if (existingAd.getFeatured().equals(featured)) {
                        log.warn("=== ADMIN: NO CHANGE NEEDED === AdID: {}, Status already: {} ===", id, featured);
                        return Mono.just(ResponseEntity.ok().<Void>build());
                    }

                    // TODO: In a complete implementation, you would update the ad's featured status here
                    // return adRepository.updateFeaturedStatus(id, featured)
                    //     .then(Mono.just(ResponseEntity.ok().<Void>build()));

                    // Placeholder implementation
                    log.info("=== ADMIN: FEATURED STATUS UPDATED === AdID: {}, OldStatus: {}, NewStatus: {} ===",
                            id, existingAd.getFeatured(), featured);

                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== ADMIN: TOGGLE FEATURED AD SUCCESS === AdID: {}, Duration: {}ms ===", id, duration);

                    return Mono.just(ResponseEntity.ok().<Void>build());
                })
                .onErrorResume(AdNotFoundException.class, e -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.warn("=== ADMIN: AD NOT FOUND FOR FEATURING === AdID: {}, Duration: {}ms ===", id, duration);
                    return Mono.just(ResponseEntity.notFound().build());
                })
                .onErrorResume(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== ADMIN: TOGGLE FEATURED AD ERROR === AdID: {}, Duration: {}ms, Error: {} ===",
                            id, duration, error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    // Category statistics
    @GetMapping("/stats/categories")
    @Operation(
            summary = "Get statistics by category",
            description = "Retrieves marketplace statistics broken down by advertisement categories (Boats, Jet Skis, Trailers, Engines, etc.) including counts and pricing analysis."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category statistics retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryStatsResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while generating category statistics",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<CategoryStatsResponse>> getCategoryStats() {
        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN: GET CATEGORY STATS REQUEST START ===");

        // TODO: In a complete implementation, you would:
        // 1. Query ad counts by category
        // 2. Calculate average prices by category
        // 3. Get active vs total breakdown by category
        // 4. Possibly include trending categories, etc.

        /*
        return Mono.zip(
            adRepository.countByCategory(),
            adRepository.countActiveByCategory(),
            adRepository.getAveragePriceByCategory()
        ).map(tuple -> {
            Map<String, Long> totalByCategory = tuple.getT1();
            Map<String, Long> activeByCategory = tuple.getT2();
            Map<String, BigDecimal> avgPriceByCategory = tuple.getT3();

            log.info("=== ADMIN: CATEGORY DATA RETRIEVED === Categories: {}, Duration: {}ms ===",
                    totalByCategory.size(), System.currentTimeMillis() - startTime);

            return CategoryStatsResponse.builder()
                    .totalByCategory(totalByCategory)
                    .activeByCategory(activeByCategory)
                    .averagePriceByCategory(avgPriceByCategory)
                    .build();
        });
        */

        // Placeholder implementation for now
        CategoryStatsResponse placeholderStats = CategoryStatsResponse.builder()
                // .totalByCategory(Map.of(
                //     "BOATS_AND_YACHTS", 150L,
                //     "JET_SKIS", 45L,
                //     "TRAILERS", 30L,
                //     "ENGINES", 25L,
                //     "MARINE_ELECTRONICS", 20L,
                //     "FISHING", 35L,
                //     "PARTS", 40L,
                //     "SERVICES", 15L
                // ))
                // .activeByCategory(Map.of(
                //     "BOATS_AND_YACHTS", 142L,
                //     "JET_SKIS", 43L,
                //     "TRAILERS", 28L,
                //     "ENGINES", 24L,
                //     "MARINE_ELECTRONICS", 19L,
                //     "FISHING", 33L,
                //     "PARTS", 38L,
                //     "SERVICES", 14L
                // ))
                .build();

        long duration = System.currentTimeMillis() - startTime;
        log.info("=== ADMIN: GET CATEGORY STATS SUCCESS === Duration: {}ms ===", duration);
        log.debug("=== ADMIN: CATEGORY STATS DETAILS === Response built (placeholder implementation) ===");

        return Mono.just(ResponseEntity.ok(placeholderStats))
                .onErrorResume(error -> {
                    long errorDuration = System.currentTimeMillis() - startTime;
                    log.error("=== ADMIN: GET CATEGORY STATS ERROR === Duration: {}ms, Error: {} ===",
                            errorDuration, error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/ads/pending-approval")
    @Operation(
            summary = "Get advertisements pending approval",
            description = "Retrieves all advertisements that are waiting for admin approval before being published"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pending advertisements retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoatAdResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<BoatAdResponse> getPendingAds() {
        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN: GET PENDING ADS REQUEST START ===");

        return adminService.getPendingApprovalAds()
                .doOnNext(ad -> log.debug("=== PENDING AD RESPONSE === AdID: {}, Title: '{}' ===",
                        ad.getId(), ad.getTitle()))
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== ADMIN: GET PENDING ADS COMPLETE === Duration: {}ms ===", duration);
                })
                .onErrorResume(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== ADMIN: GET PENDING ADS ERROR === Duration: {}ms, Error: {} ===",
                            duration, error.getMessage());
                    return Flux.empty();
                });
    }

    @PutMapping("/ads/{id}/approve")
    @Operation(
            summary = "Approve or reject advertisement",
            description = "Approves or rejects a pending advertisement, making it visible or hidden from public search"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advertisement approval status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters or ad not in pending status"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Advertisement not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> approveAd(
            @Parameter(description = "Advertisement ID", required = true)
            @PathVariable @NotNull @Min(1) Long id,

            @Parameter(description = "Approval status", required = true)
            @RequestParam @NotNull Boolean approved,

            @Parameter(description = "Rejection reason (required if approved=false)")
            @RequestParam(required = false) String rejectionReason,

            Authentication authentication) {

        long startTime = System.currentTimeMillis();
        String adminUserId = authentication.getName();
        log.info("=== ADMIN: APPROVE AD REQUEST START === AdID: {}, Approved: {}, AdminUser: {} ===",
                id, approved, adminUserId);

        // Validate rejection reason if rejecting
        if (!approved && (rejectionReason == null || rejectionReason.trim().isEmpty())) {
            log.warn("=== ADMIN: REJECTION REASON MISSING === AdID: {} ===", id);
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return adminService.approveAd(id, approved, adminUserId, rejectionReason)
                .then(Mono.fromCallable(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== ADMIN: APPROVE AD SUCCESS === AdID: {}, Status: {}, Duration: {}ms ===",
                            id, approved ? "APPROVED" : "REJECTED", duration);
                    return ResponseEntity.ok().<Void>build();
                }))
                .onErrorResume(AdNotFoundException.class, e -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.warn("=== ADMIN: AD NOT FOUND FOR APPROVAL === AdID: {}, Duration: {}ms ===", id, duration);
                    return Mono.just(ResponseEntity.notFound().build());
                })
                .onErrorResume(IllegalStateException.class, e -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.warn("=== ADMIN: INVALID STATE FOR APPROVAL === AdID: {}, Duration: {}ms, Error: {} ===",
                            id, duration, e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== ADMIN: APPROVE AD ERROR === AdID: {}, Duration: {}ms, Error: {} ===",
                            id, duration, error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/users/statistics")
    @Operation(
            summary = "Get user statistics",
            description = "Retrieves statistics about users including most active sellers, new registrations, approval breakdowns, etc."
    )
    @ApiResponse(
            responseCode = "200",
            description = "User statistics retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserStatisticsResponse.class)
            )
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<UserStatisticsResponse>> getUserStatistics() {
        long startTime = System.currentTimeMillis();
        log.info("=== ADMIN: GET USER STATISTICS REQUEST START ===");

        return adminService.getUserStatistics()
                .map(stats -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== ADMIN: USER STATS SUCCESS === TopSellers: {}, ApprovalStats: {}, Duration: {}ms ===",
                            stats.getTopSellersByAdCount().size(), stats.getApprovalStatusBreakdown().size(), duration);
                    return ResponseEntity.ok(stats);
                })
                .onErrorResume(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== ADMIN: GET USER STATISTICS ERROR === Duration: {}ms, Error: {} ===",
                            duration, error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}
