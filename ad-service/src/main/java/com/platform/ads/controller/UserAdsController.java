package com.platform.ads.controller;

import com.platform.ads.dto.BoatAdResponse;
import com.platform.ads.dto.UpdateAdRequest;
import com.platform.ads.dto.UserAdsStatisticsResponse;
import com.platform.ads.dto.enums.MainBoatCategory;
import com.platform.ads.service.UserAdsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/ads/users")
@RequiredArgsConstructor
@Tag(name = "User Ads Management", description = "Manage user's own advertisements")
@SecurityRequirement(name = "bearerAuth")
public class UserAdsController {

    private final UserAdsService userAdsService;

    @GetMapping("/my-ads")
    @Operation(
            summary = "Get all user's advertisements",
            description = "Retrieves all advertisements created by the authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "User's ads retrieved successfully")
    public Flux<BoatAdResponse> getMyAds(Authentication authentication) {
        return userAdsService.getActiveAds(authentication.getName());
    }

    @GetMapping("/my-ads/active")
    @Operation(
            summary = "Get user's active advertisements",
            description = "Retrieves only active advertisements created by the authenticated user"
    )
    public Flux<BoatAdResponse> getMyActiveAds(Authentication authentication) {
        String userId = authentication.getName();
        log.info("=== GET MY ACTIVE ADS REQUEST === UserId: {} ===", userId);

        return userAdsService.getUserActiveAds(userId);
    }

    @GetMapping("/my-ads/category/{category}")
    @Operation(
            summary = "Get user's ads by category",
            description = "Retrieves user's advertisements filtered by category"
    )
    public Flux<BoatAdResponse> getMyAdsByCategory(
            @PathVariable @NotNull MainBoatCategory category,
            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== GET MY ADS BY CATEGORY === UserId: {}, Category: {} ===", userId, category);

        return userAdsService.getUserAdsByCategory(userId, category);
    }

    @GetMapping("/my-ads/filtered")
    @Operation(
            summary = "Get user's ads with filters",
            description = "Retrieves user's advertisements with optional filtering and sorting"
    )
    public Flux<BoatAdResponse> getMyAdsFiltered(
            @Parameter(description = "Filter by active status")
            @RequestParam(required = false) Boolean active,

            @Parameter(description = "Filter by category")
            @RequestParam(required = false) MainBoatCategory category,

            @Parameter(description = "Sort order")
            @RequestParam(required = false) String sortBy,

            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== GET MY FILTERED ADS === UserId: {}, Active: {}, Category: {}, SortBy: {} ===",
                userId, active, category, sortBy);

        return userAdsService.getUserAdsWithFilters(userId, active, category, sortBy);
    }

    @GetMapping("/my-stats")
    @Operation(
            summary = "Get user's advertisement statistics",
            description = "Retrieves statistics about user's advertisements including views, counts, etc."
    )
    public Mono<ResponseEntity<UserAdsStatisticsResponse>> getMyStatistics(Authentication authentication) {
        String userId = authentication.getName();
        log.info("=== GET MY STATS REQUEST === UserId: {} ===", userId);

        return userAdsService.getUserStatistics(userId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/my-ads/{adId}/deactivate")
    @Operation(
            summary = "Deactivate user's advertisement",
            description = "Deactivates a specific advertisement owned by the user"
    )
    public Mono<ResponseEntity<Void>> deactivateMyAd(
            @PathVariable @NotNull @Min(1) Long adId,
            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== DEACTIVATE MY AD REQUEST === UserId: {}, AdID: {} ===", userId, adId);

        return userAdsService.deactivateUserAd(userId, adId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @PutMapping("/my-ads/{adId}/reactivate")
    @Operation(
            summary = "Reactivate user's advertisement",
            description = "Reactivates a specific advertisement owned by the user"
    )
    public Mono<ResponseEntity<Void>> reactivateMyAd(
            @PathVariable @NotNull @Min(1) Long adId,
            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== REACTIVATE MY AD REQUEST === UserId: {}, AdID: {} ===", userId, adId);

        return userAdsService.reactivateUserAd(userId, adId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    // ===========================
    // EDIT AD ENDPOINTS
    // ===========================

    @PutMapping("/my-ads/{adId}")
    @Operation(
            summary = "Update user's advertisement",
            description = "Updates basic information of an advertisement owned by the authenticated user. Cannot edit archived ads."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advertisement updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update data or ad cannot be edited"),
            @ApiResponse(responseCode = "403", description = "User does not own this advertisement"),
            @ApiResponse(responseCode = "404", description = "Advertisement not found")
    })
    public Mono<ResponseEntity<BoatAdResponse>> updateMyAd(
            @Parameter(description = "Advertisement ID to update", required = true)
            @PathVariable @NotNull @Min(1) Long adId,

            @Parameter(description = "Updated advertisement data", required = true)
            @Valid @RequestBody UpdateAdRequest updateRequest,

            Authentication authentication) {

        String userId = authentication.getName();
        long startTime = System.currentTimeMillis();
        log.info("=== UPDATE MY AD REQUEST === UserId: {}, AdID: {}, NewTitle: '{}' ===",
                userId, adId, updateRequest.getTitle());

        return userAdsService.updateAd(userId, adId, updateRequest)
                .map(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== UPDATE MY AD SUCCESS === UserId: {}, AdID: {}, Duration: {}ms ===",
                            userId, adId, duration);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(IllegalStateException.class, e -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.warn("=== UPDATE MY AD INVALID STATE === UserId: {}, AdID: {}, Duration: {}ms, Error: {} ===",
                            userId, adId, duration, e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.warn("=== UPDATE MY AD FORBIDDEN === UserId: {}, AdID: {}, Duration: {}ms ===",
                            userId, adId, duration);
                    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                });
    }

    // ===========================
    // ARCHIVE AD ENDPOINTS
    // ===========================

    @PutMapping("/my-ads/{adId}/archive")
    @Operation(
            summary = "Archive user's advertisement",
            description = "Archives an advertisement, making it invisible to public but preserving it for user. Can be unarchived later."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advertisement archived successfully"),
            @ApiResponse(responseCode = "400", description = "Advertisement already archived"),
            @ApiResponse(responseCode = "403", description = "User does not own this advertisement"),
            @ApiResponse(responseCode = "404", description = "Advertisement not found")
    })
    public Mono<ResponseEntity<Void>> archiveMyAd(
            @Parameter(description = "Advertisement ID to archive", required = true)
            @PathVariable @NotNull @Min(1) Long adId,
            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== ARCHIVE MY AD REQUEST === UserId: {}, AdID: {} ===", userId, adId);

        return userAdsService.archiveAd(userId, adId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(IllegalStateException.class, e -> {
                    log.warn("=== ARCHIVE AD INVALID STATE === UserId: {}, AdID: {}, Error: {} ===",
                            userId, adId, e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.warn("=== ARCHIVE AD FORBIDDEN === UserId: {}, AdID: {} ===", userId, adId);
                    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                });
    }

    @PutMapping("/my-ads/{adId}/unarchive")
    @Operation(
            summary = "Unarchive user's advertisement",
            description = "Unarchives a previously archived advertisement, making it visible to public again."
    )
    public Mono<ResponseEntity<Void>> unarchiveMyAd(
            @PathVariable @NotNull @Min(1) Long adId,
            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== UNARCHIVE MY AD REQUEST === UserId: {}, AdID: {} ===", userId, adId);

        return userAdsService.unarchiveAd(userId, adId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(IllegalStateException.class, e -> {
                    log.warn("=== UNARCHIVE AD INVALID STATE === UserId: {}, AdID: {} ===", userId, adId);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @GetMapping("/my-ads/archived")
    @Operation(
            summary = "Get user's archived advertisements",
            description = "Retrieves all advertisements that have been archived by the user"
    )
    public Flux<BoatAdResponse> getMyArchivedAds(Authentication authentication) {
        String userId = authentication.getName();
        log.info("=== GET MY ARCHIVED ADS REQUEST === UserId: {} ===", userId);

        return userAdsService.getArchivedAds(userId);
    }

    // ===========================
    // DELETE AD ENDPOINT
    // ===========================

    @DeleteMapping("/my-ads/{adId}")
    @Operation(
            summary = "Delete user's advertisement",
            description = "Permanently deletes an advertisement. Cannot be undone. Only allowed for ads with low interaction."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advertisement deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Advertisement has high interaction, use archive instead"),
            @ApiResponse(responseCode = "403", description = "User does not own this advertisement"),
            @ApiResponse(responseCode = "404", description = "Advertisement not found")
    })
    public Mono<ResponseEntity<Void>> deleteMyAd(
            @Parameter(description = "Advertisement ID to delete", required = true)
            @PathVariable @NotNull @Min(1) Long adId,
            Authentication authentication) {

        String userId = authentication.getName();
        log.info("=== DELETE MY AD REQUEST === UserId: {}, AdID: {} ===", userId, adId);

        return userAdsService.deleteAd(userId, adId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(IllegalStateException.class, e -> {
                    log.warn("=== DELETE AD INVALID STATE === UserId: {}, AdID: {}, Error: {} ===",
                            userId, adId, e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.warn("=== DELETE AD FORBIDDEN === UserId: {}, AdID: {} ===", userId, adId);
                    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                });
    }
}