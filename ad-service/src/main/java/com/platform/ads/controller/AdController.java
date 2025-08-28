package com.platform.ads.controller;

import com.platform.ads.dto.*;
import com.platform.ads.service.AdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/ads")
@Tag(name = "Advertisements", description = "Ad management endpoints")
@RequiredArgsConstructor
@Slf4j
public class AdController {

    private final AdService adService;

    // CREATE AD
    @Operation(summary = "Create new ad",
            description = "Creates a new advertisement for authenticated user",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ad created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public Mono<ResponseEntity<AdResponse>> createAd(
            @Valid @RequestBody AdRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String userEmail = jwt.getClaimAsString("email");
        request.setUserEmail(userEmail);

        return adService.createAd(request, jwt.getTokenValue())
                .map(ad -> ResponseEntity.status(HttpStatus.CREATED).body(ad));
    }

    // GET ALL ADS (PUBLIC)
    @Operation(summary = "Get all active ads",
            description = "Returns all active advertisements (public endpoint)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ads retrieved successfully")
    })
    @GetMapping
    public Flux<AdResponse> getAllAds() {
        return adService.getAllAds();
    }

    // GET AD BY ID
    @Operation(summary = "Get ad by ID",
            description = "Returns a specific advertisement by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ad found"),
            @ApiResponse(responseCode = "404", description = "Ad not found")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AdResponse>> getAdById(@PathVariable Long id) {
        return adService.getAdById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // GET MY ADS
    @Operation(summary = "Get current user's ads",
            description = "Returns all ads created by the authenticated user",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ads retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/my-ads")
    public Flux<AdResponse> getMyAds(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        return adService.getAdsByUserEmail(userEmail, jwt.getTokenValue());
    }

    // GET ADS BY USER EMAIL
    @Operation(summary = "Get ads by user email",
            description = "Returns all ads created by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ads retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{email}")
    public Flux<AdResponse> getAdsByUserEmail(@AuthenticationPrincipal Jwt jwt, @PathVariable String email) {
        return adService.getAdsByUserEmail(email, jwt.getTokenValue());
    }

    // SEARCH ADS
    @Operation(summary = "Search ads",
            description = "Search ads with various filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    })
    @GetMapping("/search")
    public Flux<AdResponse> searchAds(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        return adService.searchAds(query, category, location, minPrice, maxPrice);
    }

    // ADVANCED SEARCH WITH REQUEST BODY
    @Operation(summary = "Advanced search ads",
            description = "Advanced search with complex filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    })
    @PostMapping("/search")
    public Flux<AdResponse> advancedSearchAds(@RequestBody AdSearchRequest searchRequest) {
        return adService.advancedSearchAds(searchRequest);
    }

    // GET ADS BY CATEGORY
    @Operation(summary = "Get ads by category",
            description = "Returns all active ads in a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ads retrieved successfully")
    })
    @GetMapping("/category/{category}")
    public Flux<AdResponse> getAdsByCategory(@PathVariable String category) {
        return adService.getAdsByCategory(category);
    }

    // UPDATE AD
    @Operation(summary = "Update ad",
            description = "Updates an existing advertisement (owner only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ad updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Not owner of the ad"),
            @ApiResponse(responseCode = "404", description = "Ad not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AdResponse>> updateAd(
            @PathVariable Long id,
            @Valid @RequestBody AdUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String userEmail = jwt.getClaimAsString("email");

        return adService.updateAd(id, request, userEmail)
                .map(ResponseEntity::ok);
    }

    // UPDATE AD STATUS (ACTIVATE/DEACTIVATE)
    @Operation(summary = "Update ad status",
            description = "Activate or deactivate an advertisement (owner only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ad status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Not owner of the ad"),
            @ApiResponse(responseCode = "404", description = "Ad not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<AdResponse>> updateAdStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdStatusUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String userEmail = jwt.getClaimAsString("email");

        return adService.updateAdStatus(id, request.getActive(), userEmail)
                .map(ResponseEntity::ok);
    }

    // DELETE AD
    @Operation(summary = "Delete ad",
            description = "Deletes an advertisement (owner only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ad deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Not owner of the ad"),
            @ApiResponse(responseCode = "404", description = "Ad not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAd(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        String userEmail = jwt.getClaimAsString("email");

        return adService.deleteAd(id, userEmail)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    // GET USER AD SUMMARY
    @Operation(summary = "Get user ad summary",
            description = "Returns user info with total ad count and statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User summary retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{email}/summary")
    public Mono<ResponseEntity<AdService.UserAdSummary>> getUserAdSummary(@AuthenticationPrincipal Jwt jwt, @PathVariable String email) {
        return adService.getUserAdSummary(email, jwt.getTokenValue())
                .map(ResponseEntity::ok);
    }

    // GET MY STATS
    @Operation(summary = "Get my ad statistics",
            description = "Returns detailed statistics for authenticated user's ads",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/my-stats")
    public Mono<ResponseEntity<UserAdStatsResponse>> getMyAdStats(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        return adService.getUserAdStats(userEmail, jwt.getTokenValue())
                .map(ResponseEntity::ok);
    }

    // GET GENERAL STATISTICS
    @Operation(summary = "Get general ad statistics",
            description = "Returns general statistics about all ads")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @GetMapping("/stats")
    public Mono<ResponseEntity<AdSummaryResponse>> getGeneralStats() {
        return adService.getGeneralStats()
                .map(ResponseEntity::ok);
    }

    // GET CATEGORIES
    @Operation(summary = "Get all categories",
            description = "Returns all categories with ad counts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    @GetMapping("/categories")
    public Flux<CategoryResponse> getCategories() {
        return adService.getCategories();
    }

    // VALIDATE USER
    @Operation(summary = "Validate user exists",
            description = "Check if a user exists in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User validation result"),
            @ApiResponse(responseCode = "400", description = "Invalid email")
    })
    @GetMapping("/validate-user")
    public Mono<ResponseEntity<Boolean>> validateUser(@AuthenticationPrincipal Jwt jwt, @RequestParam String email) {
        return adService.userExistsByEmail(email, jwt.getTokenValue())
                .map(exists -> ResponseEntity.ok(exists));
    }

    // GET RECENT ADS
    @Operation(summary = "Get recent ads",
            description = "Returns the most recently created active ads")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recent ads retrieved successfully")
    })
    @GetMapping("/recent")
    public Flux<AdResponse> getRecentAds(
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of recent ads to return") int limit) {
        return adService.getRecentAds(limit);
    }

    // GET FEATURED/POPULAR ADS
    @Operation(summary = "Get popular ads",
            description = "Returns ads sorted by some popularity metric")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Popular ads retrieved successfully")
    })
    @GetMapping("/popular")
    public Flux<AdResponse> getPopularAds(
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of popular ads to return") int limit) {
        return adService.getPopularAds(limit);
    }
}