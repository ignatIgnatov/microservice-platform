package com.platform.ads.service;

import com.platform.ads.dto.BrandResponse;
import com.platform.ads.dto.BrandsByCategoryResponse;
import com.platform.ads.dto.AllBrandsResponse;
import com.platform.ads.dto.enums.BoatCategory;
import com.platform.ads.entity.Brand;
import com.platform.ads.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    @Cacheable(value = "brandsByCategory", key = "#category")
    public Flux<BrandResponse> getBrandsByCategory(String category) {
        log.info("=== GET BRANDS BY CATEGORY === Category: {} ===", category);

        return brandRepository.findByCategoryOrderByDisplayOrder(category.toUpperCase())
                .map(this::mapToResponse)
                .doOnComplete(() -> log.info("Successfully retrieved brands for category: {}", category))
                .doOnError(error -> log.error("Error retrieving brands for category {}: {}", category, error.getMessage()));
    }

    @Cacheable("allActiveBrands")
    public Flux<BrandResponse> getAllActiveBrands() {
        log.info("=== GET ALL ACTIVE BRANDS ===");

        return brandRepository.findAllActiveOrderByCategory()
                .map(this::mapToResponse)
                .doOnComplete(() -> log.info("Successfully retrieved all active brands"))
                .doOnError(error -> log.error("Error retrieving all brands: {}", error.getMessage()));
    }

    @Cacheable("brandsGroupedByCategory")
    public Mono<AllBrandsResponse> getBrandsGroupedByCategory() {
        log.info("=== GET BRANDS GROUPED BY CATEGORIES ===");

        return brandRepository.findDistinctActiveCategories()
                .flatMap(category ->
                        brandRepository.findByCategoryOrderByDisplayOrder(category)
                                .map(this::mapToResponse)
                                .collectList()
                                .zipWith(brandRepository.countByCategory(category))
                                .map(tuple -> {
                                    List<BrandResponse> brands = tuple.getT1();
                                    Long count = tuple.getT2();

                                    return BrandsByCategoryResponse.builder()
                                            .category(category)
                                            .categoryDisplayName(getCategoryDisplayName(category))
                                            .totalCount(count.intValue())
                                            .brands(brands)
                                            .build();
                                })
                )
                .collectList()
                .map(categoryList -> {
                    Map<String, BrandsByCategoryResponse> brandsMap = new HashMap<>();
                    int totalBrands = 0;

                    for (BrandsByCategoryResponse categoryResponse : categoryList) {
                        brandsMap.put(categoryResponse.getCategory(), categoryResponse);
                        totalBrands += categoryResponse.getTotalCount();
                    }

                    return AllBrandsResponse.builder()
                            .brandsByCategory(brandsMap)
                            .totalBrands(totalBrands)
                            .categories(categoryList.stream()
                                    .map(BrandsByCategoryResponse::getCategory)
                                    .toList())
                            .build();
                })
                .doOnSuccess(result -> log.info("Successfully grouped brands by categories. Total: {}", result.getTotalBrands()))
                .doOnError(error -> log.error("Error grouping brands by categories: {}", error.getMessage()));
    }

    public Flux<BrandResponse> searchBrands(String searchTerm) {
        log.info("=== SEARCH BRANDS === Term: {} ===", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllActiveBrands();
        }

        return brandRepository.searchByNameContaining(searchTerm.trim())
                .map(this::mapToResponse)
                .doOnComplete(() -> log.info("Search completed for term: {}", searchTerm))
                .doOnError(error -> log.error("Error searching brands with term {}: {}", searchTerm, error.getMessage()));
    }

    public Mono<Boolean> validateBrand(String brandName, String category) {
        log.info("=== VALIDATE BRAND === Brand: {}, Category: {} ===", brandName, category);

        // Always allow custom production
        if ("Собствено производство".equals(brandName)) {
            return Mono.just(true);
        }

        return brandRepository.findByCategoryAndNameIgnoreCase(category.toUpperCase(), brandName)
                .hasElement()
                .doOnNext(exists -> log.info("Brand validation result: {}", exists ? "VALID" : "INVALID"))
                .doOnError(error -> log.error("Error validating brand: {}", error.getMessage()));
    }

    public Flux<String> getCategories() {
        log.info("=== GET CATEGORIES ===");

        return brandRepository.findDistinctActiveCategories()
                .doOnComplete(() -> log.info("Successfully retrieved categories"))
                .doOnError(error -> log.error("Error retrieving categories: {}", error.getMessage()));
    }

    private BrandResponse mapToResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .category(brand.getCategory())
                .displayOrder(brand.getDisplayOrder())
                .build();
    }

    private String getCategoryDisplayName(String category) {
        try {
            return BoatCategory.fromString(category).getDisplayName();
        } catch (IllegalArgumentException e) {
            return category; // fallback to original category name
        }
    }
}
