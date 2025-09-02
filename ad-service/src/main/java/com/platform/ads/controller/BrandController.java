package com.platform.ads.controller;

import com.platform.ads.dto.BrandResponse;
import com.platform.ads.dto.AllBrandsResponse;
import com.platform.ads.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/ads/brands")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Brands", description = "Boat, yacht, and kayak brand management API")
public class BrandController {

    private final BrandService brandService;

    @GetMapping(value = "/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get brands by category",
            description = "Retrieves all active brands for a specific category (MOTOR_BOATS, SAILBOATS, KAYAKS)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved brands"),
            @ApiResponse(responseCode = "400", description = "Invalid category provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Flux<BrandResponse> getBrandsByCategory(
            @Parameter(description = "Brand category", required = true, example = "MOTOR_BOATS")
            @PathVariable String category) {

        log.info("=== GET BRANDS BY CATEGORY REQUEST === Category: {} ===", category);
        return brandService.getBrandsByCategory(category);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get all brands",
            description = "Retrieves all active brands across all categories"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all brands"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Flux<BrandResponse> getAllBrands() {
        log.info("=== GET ALL BRANDS REQUEST ===");
        return brandService.getAllActiveBrands();
    }

    @GetMapping(value = "/grouped", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get brands grouped by category",
            description = "Retrieves all brands organized by category with counts and metadata"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved grouped brands"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<AllBrandsResponse> getBrandsGrouped() {
        log.info("=== GET GROUPED BRANDS REQUEST ===");
        return brandService.getBrandsGroupedByCategory();
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Search brands",
            description = "Search brands by name across all categories"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Flux<BrandResponse> searchBrands(
            @Parameter(description = "Search term for brand name", example = "Boston")
            @RequestParam(required = false) String q) {

        log.info("=== SEARCH BRANDS REQUEST === Query: {} ===", q);
        return brandService.searchBrands(q);
    }

    @GetMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Validate brand",
            description = "Check if a brand exists in a specific category"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Validation completed"),
            @ApiResponse(responseCode = "400", description = "Missing required parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<Boolean>> validateBrand(
            @Parameter(description = "Brand name to validate", required = true)
            @RequestParam String brand,
            @Parameter(description = "Category to validate against", required = true)
            @RequestParam String category) {

        log.info("=== VALIDATE BRAND REQUEST === Brand: {}, Category: {} ===", brand, category);

        return brandService.validateBrand(brand, category)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(false));
    }

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get categories",
            description = "Retrieves all available brand categories"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved categories"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Flux<String> getCategories() {
        log.info("=== GET CATEGORIES REQUEST ===");
        return brandService.getCategories();
    }
}