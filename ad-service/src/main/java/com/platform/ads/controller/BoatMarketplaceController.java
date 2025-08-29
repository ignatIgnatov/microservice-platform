package com.platform.ads.controller;

import com.platform.ads.dto.AdType;
import com.platform.ads.dto.BoatAdRequest;
import com.platform.ads.dto.BoatAdResponse;
import com.platform.ads.dto.BoatCategory;
import com.platform.ads.dto.BoatMarketplaceStatsResponse;
import com.platform.ads.dto.BoatSearchRequest;
import com.platform.ads.dto.ItemCondition;
import com.platform.ads.dto.PriceInfo;
import com.platform.ads.dto.SortOrder;
import com.platform.ads.service.BoatMarketplaceService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/boats")
@Tag(name = "Boat Marketplace", description = "QHTI.BG Boat Marketplace API")
@RequiredArgsConstructor
@Slf4j
public class BoatMarketplaceController {

    private final BoatMarketplaceService marketplaceService;

    // ==================== AD CREATION ====================

    @Operation(summary = "Create new boat ad",
            description = "Create a new boat/yacht advertisement",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Boat ad created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or mandatory field missing"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "User not found in auth service"),
            @ApiResponse(responseCode = "422", description = "Validation error"),
            @ApiResponse(responseCode = "503", description = "Auth service unavailable")
    })
    @PostMapping("/boats")
    public Mono<ResponseEntity<BoatAdResponse>> createBoatAd(
            @Valid @RequestBody BoatAdRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        validateBoatAdRequest(request);

        String userEmail = jwt.getClaimAsString("email");
        String token = jwt.getTokenValue();
        request.setUserEmail(userEmail);
        request.setCategory(BoatCategory.BOATS_AND_YACHTS);

        return marketplaceService.createBoatAd(request, token)
                .map(ad -> ResponseEntity.status(HttpStatus.CREATED).body(ad));
    }

    @Operation(summary = "Create new jet ski ad",
            description = "Create a new jet ski advertisement",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/jetskis")
    public Mono<ResponseEntity<BoatAdResponse>> createJetSkiAd(
            @Valid @RequestBody BoatAdRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        validateJetSkiAdRequest(request);

        String userEmail = jwt.getClaimAsString("email");
        String token = jwt.getTokenValue();
        request.setUserEmail(userEmail);
        request.setCategory(BoatCategory.JET_SKIS);

        return marketplaceService.createBoatAd(request, token)
                .map(ad -> ResponseEntity.status(HttpStatus.CREATED).body(ad));
    }

    @Operation(summary = "Create new trailer ad",
            description = "Create a new boat trailer advertisement",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/trailers")
    public Mono<ResponseEntity<BoatAdResponse>> createTrailerAd(
            @Valid @RequestBody BoatAdRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        validateTrailerAdRequest(request);

        String userEmail = jwt.getClaimAsString("email");
        String token = jwt.getTokenValue();
        request.setUserEmail(userEmail);
        request.setCategory(BoatCategory.TRAILERS);

        return marketplaceService.createBoatAd(request, token)
                .map(ad -> ResponseEntity.status(HttpStatus.CREATED).body(ad));
    }

    @Operation(summary = "Create new engine ad",
            description = "Create a new marine engine advertisement",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/engines")
    public Mono<ResponseEntity<BoatAdResponse>> createEngineAd(
            @Valid @RequestBody BoatAdRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        validateEngineAdRequest(request);

        String userEmail = jwt.getClaimAsString("email");
        String token = jwt.getTokenValue();
        request.setUserEmail(userEmail);
        request.setCategory(BoatCategory.ENGINES);

        return marketplaceService.createBoatAd(request, token)
                .map(ad -> ResponseEntity.status(HttpStatus.CREATED).body(ad));
    }

    // ==================== SEARCH AND BROWSE ====================

    @Operation(summary = "Search boat advertisements",
            description = "Advanced search with filters for boat marketplace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    @PostMapping("/search")
    public Flux<BoatAdResponse> searchAds(@Valid @RequestBody BoatSearchRequest searchRequest) {
        return marketplaceService.searchAds(searchRequest);
    }

    @Operation(summary = "Get boats by category",
            description = "Get all active ads from specific category")
    @GetMapping("/category/{category}")
    public Flux<BoatAdResponse> getAdsByCategory(
            @PathVariable @Parameter(description = "Ad category") BoatCategory category,
            @RequestParam(defaultValue = "NEWEST") @Parameter(description = "Sort order") String sortBy,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size) {

        BoatSearchRequest searchRequest = BoatSearchRequest.builder()
                .category(category)
                .sortBy(sortBy)
                .page(page)
                .size(size)
                .build();

        return marketplaceService.searchAds(searchRequest);
    }

    @Operation(summary = "Quick search",
            description = "Simple text search across all categories")
    @GetMapping("/search")
    public Flux<BoatAdResponse> quickSearch(
            @RequestParam @Parameter(description = "Search query") String q,
            @RequestParam(required = false) @Parameter(description = "Category filter") BoatCategory category,
            @RequestParam(required = false) @Parameter(description = "Location filter") String location,
            @RequestParam(defaultValue = "NEWEST") @Parameter(description = "Sort order") String sortBy) {

        BoatSearchRequest searchRequest = BoatSearchRequest.builder()
                .query(q)
                .category(category)
                .location(location)
                .sortBy(sortBy)
                .build();

        return marketplaceService.searchAds(searchRequest);
    }

    // ==================== AD DETAILS ====================

    @Operation(summary = "Get ad by ID",
            description = "Get detailed information about specific ad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ad found and returned"),
            @ApiResponse(responseCode = "404", description = "Ad not found")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<BoatAdResponse>> getAdById(@PathVariable Long id) {
        return marketplaceService.getAdById(id)
                .map(ResponseEntity::ok);
    }

    // ==================== USER'S ADS ====================

    @Operation(summary = "Get current user's ads",
            description = "Get all ads created by the authenticated user",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/my-ads")
    public Flux<BoatAdResponse> getMyAds(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");

        BoatSearchRequest searchRequest = BoatSearchRequest.builder()
                .category(null) // All categories
                .query(null)
                .sortBy("NEWEST")
                .build();

        return marketplaceService.searchAds(searchRequest)
                .filter(ad -> userEmail.equals(ad.getUserEmail()));
    }

    @Operation(summary = "Get user's ads by email",
            description = "Get all active ads from specific user")
    @GetMapping("/user/{email}")
    public Flux<BoatAdResponse> getUserAds(@PathVariable String email) {
        return marketplaceService.searchAds(BoatSearchRequest.builder()
                        .category(null) // All categories
                        .sortBy("NEWEST")
                        .build())
                .filter(ad -> email.equals(ad.getUserEmail()) && ad.getActive());
    }

    // ==================== FEATURED AND POPULAR ====================

    @Operation(summary = "Get featured ads",
            description = "Get featured/promoted advertisements")
    @GetMapping("/featured")
    public Flux<BoatAdResponse> getFeaturedAds(
            @RequestParam(required = false) BoatCategory category,
            @RequestParam(defaultValue = "10") int limit) {

        BoatSearchRequest searchRequest = BoatSearchRequest.builder()
                .category(category)
                .sortBy("NEWEST")
                .size(limit)
                .build();

        return marketplaceService.searchAds(searchRequest)
                .filter(ad -> ad.getFeatured())
                .take(limit);
    }

    @Operation(summary = "Get most viewed ads",
            description = "Get most popular ads by views")
    @GetMapping("/popular")
    public Flux<BoatAdResponse> getPopularAds(
            @RequestParam(required = false) BoatCategory category,
            @RequestParam(defaultValue = "10") int limit) {

        BoatSearchRequest searchRequest = BoatSearchRequest.builder()
                .category(category)
                .sortBy("MOST_VIEWED")
                .size(limit)
                .build();

        return marketplaceService.searchAds(searchRequest)
                .take(limit);
    }

    @Operation(summary = "Get recent ads",
            description = "Get newest advertisements")
    @GetMapping("/recent")
    public Flux<BoatAdResponse> getRecentAds(
            @RequestParam(required = false) BoatCategory category,
            @RequestParam(defaultValue = "20") int limit) {

        BoatSearchRequest searchRequest = BoatSearchRequest.builder()
                .category(category)
                .sortBy("NEWEST")
                .size(limit)
                .build();

        return marketplaceService.searchAds(searchRequest)
                .take(limit);
    }

    // ==================== STATISTICS ====================

    @Operation(summary = "Get marketplace statistics",
            description = "Get general statistics about the marketplace")
    @GetMapping("/stats")
    public Mono<ResponseEntity<BoatMarketplaceStatsResponse>> getMarketplaceStats() {
        return marketplaceService.getMarketplaceStats()
                .map(ResponseEntity::ok);
    }

    // ==================== FILTER OPTIONS ====================

    @Operation(summary = "Get available brands",
            description = "Get list of available brands for category")
    @GetMapping("/brands")
    public Flux<String> getBrands(@RequestParam BoatCategory category) {
        // This would typically come from a brands service or cached data
        return Flux.fromIterable(getBrandsByCategory(category));
    }

    @Operation(summary = "Get available models",
            description = "Get list of available models for brand")
    @GetMapping("/models")
    public Flux<String> getModels(
            @RequestParam BoatCategory category,
            @RequestParam String brand) {
        // This would typically come from a models service or cached data
        return Flux.fromIterable(getModelsByBrandAndCategory(category, brand));
    }

    @Operation(summary = "Get filter options",
            description = "Get all available filter options for search")
    @GetMapping("/filters")
    public Mono<ResponseEntity<FilterOptionsResponse>> getFilterOptions() {
        return Mono.just(ResponseEntity.ok(buildFilterOptions()));
    }

    // ==================== VALIDATION HELPERS ====================

    private void validateBoatAdRequest(BoatAdRequest request) {
        if (request.getBoatSpec() == null) {
            throw new IllegalArgumentException("Boat specification is required for boat ads");
        }
    }

    private void validateJetSkiAdRequest(BoatAdRequest request) {
        if (request.getJetSkiSpec() == null) {
            throw new IllegalArgumentException("Jet ski specification is required for jet ski ads");
        }
    }

    private void validateTrailerAdRequest(BoatAdRequest request) {
        if (request.getTrailerSpec() == null) {
            throw new IllegalArgumentException("Trailer specification is required for trailer ads");
        }
    }

    private void validateEngineAdRequest(BoatAdRequest request) {
        if (request.getEngineSpec() == null) {
            throw new IllegalArgumentException("Engine specification is required for engine ads");
        }
    }

    // ==================== DATA HELPERS ====================

    private java.util.List<String> getBrandsByCategory(BoatCategory category) {
        switch (category) {
            case BOATS_AND_YACHTS:
                return java.util.Arrays.asList("Всички", "Beneteau", "Bavaria", "Jeanneau", "Sea Ray", "Boston Whaler",
                        "Princess", "Azimut", "Ferretti", "Собствено производство");
            case JET_SKIS:
                return java.util.Arrays.asList("Всички", "Sea-Doo (BRP)", "Yamaha", "Kawasaki", "Honda", "Собствено производство");
            case TRAILERS:
                return java.util.Arrays.asList("Всички", "RESPO", "TRIGANO", "BRENDERUP", "VENITRAILERS",
                        "THOMAS", "NIEWIADOW", "LORRIES", "Собствено производство", "Друго");
            case ENGINES:
                return java.util.Arrays.asList("Всички", "Mercury Marine", "Suzuki Marine", "Yamaha Marine",
                        "Honda Marine", "Tohatsu", "Torqeedo", "Evinrude", "Volvo Penta", "MerCruiser");
            default:
                return java.util.Arrays.asList("Всички");
        }
    }

    private java.util.List<String> getModelsByBrandAndCategory(BoatCategory category, String brand) {
        // This would be a comprehensive mapping based on the QHTI.BG specifications
        // For now, returning a sample
        return java.util.Arrays.asList("Всички", "Model 1", "Model 2", "Собствено производство");
    }

    private FilterOptionsResponse buildFilterOptions() {
        return FilterOptionsResponse.builder()
                .categories(java.util.Arrays.asList(BoatCategory.values()))
                .priceTypes(java.util.Arrays.asList(PriceInfo.PriceType.values()))
                .conditions(java.util.Arrays.asList(ItemCondition.values()))
                .adTypes(java.util.Arrays.asList(AdType.values()))
                .sortOptions(java.util.Arrays.asList(SortOrder.values()))
                .build();
    }

    // Filter options response DTO
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FilterOptionsResponse {
        private java.util.List<BoatCategory> categories;
        private java.util.List<PriceInfo.PriceType> priceTypes;
        private java.util.List<ItemCondition> conditions;
        private java.util.List<AdType> adTypes;
        private java.util.List<SortOrder> sortOptions;
    }
}