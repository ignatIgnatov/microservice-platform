package com.platform.ads.service;

import com.platform.ads.dto.BoatAdResponse;
import com.platform.ads.dto.BoatSearchRequest;
import com.platform.ads.dto.enums.MainBoatCategory;
import com.platform.ads.entity.Ad;
import com.platform.ads.exception.InvalidSearchCriteriaException;
import com.platform.ads.repository.AdRepository;
import com.platform.ads.repository.BoatSpecificationSearchRepository;
import com.platform.ads.repository.EngineSpecificationSearchRepository;
import com.platform.ads.repository.FishingSpecificationSearchRepository;
import com.platform.ads.repository.JetSkiSpecificationSearchRepository;
import com.platform.ads.repository.MarineAccessoriesSpecificationSearchRepository;
import com.platform.ads.repository.MarineElectronicsSpecificationSearchRepository;
import com.platform.ads.repository.PartsSpecificationSearchRepository;
import com.platform.ads.repository.RentalsSpecificationSearchRepository;
import com.platform.ads.repository.ServicesSpecificationSearchRepository;
import com.platform.ads.repository.TrailerSpecificationSearchRepository;
import com.platform.ads.repository.WaterSportsSpecificationSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoatSearchService {

    private final AdRepository adRepository;
    private final BoatMarketplaceService marketplaceService;

    // Category-specific search repositories
    private final BoatSpecificationSearchRepository boatSearchRepo;
    private final JetSkiSpecificationSearchRepository jetSkiSearchRepo;
    private final TrailerSpecificationSearchRepository trailerSearchRepo;
    private final EngineSpecificationSearchRepository engineSearchRepo;
    private final MarineElectronicsSpecificationSearchRepository electronicsSearchRepo;
    private final FishingSpecificationSearchRepository fishingSearchRepo;
    private final PartsSpecificationSearchRepository partsSearchRepo;
    private final ServicesSpecificationSearchRepository servicesSearchRepo;

    // ADD NEW REPOSITORY DEPENDENCIES
    private final WaterSportsSpecificationSearchRepository waterSportsSearchRepo;
    private final MarineAccessoriesSpecificationSearchRepository marineAccessoriesSearchRepo;
    private final RentalsSpecificationSearchRepository rentalsSearchRepo;

    // ===========================
    // MAIN SEARCH METHOD - SIMPLIFIED
    // ===========================

    public Flux<BoatAdResponse> searchAds(BoatSearchRequest searchRequest) {
        long startTime = System.currentTimeMillis();
        log.info("=== SIMPLIFIED SEARCH START === Category: {}, Brand: '{}', Model: '{}' ===",
                searchRequest.getCategory(), searchRequest.getBrand(), searchRequest.getModel());

        try {
            validateSearchRequest(searchRequest);
        } catch (Exception e) {
            log.error("=== SEARCH VALIDATION FAILED === Category: {}, Error: {} ===",
                    searchRequest.getCategory(), e.getMessage());
            return Flux.error(e);
        }

        // Step 1: Get base ads using simple criteria
        Flux<Ad> baseAds = performBasicSearch(searchRequest);

        // Step 2: Apply category-specific filtering
        Flux<Ad> filteredAds = applyCategorySpecificFiltering(baseAds, searchRequest);

        // Step 3: Map to response and sort
        return filteredAds
                .flatMap(ad -> {
                    log.debug("=== MAPPING RESULT === AdID: {}, Title: '{}' ===", ad.getId(), ad.getTitle());
                    return marketplaceService.mapToResponse(ad);
                })
                .sort((ad1, ad2) -> applySorting(ad1, ad2, searchRequest.getSortBy()))
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== SIMPLIFIED SEARCH COMPLETE === Category: {}, Duration: {}ms ===",
                            searchRequest.getCategory(), duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== SIMPLIFIED SEARCH ERROR === Category: {}, Duration: {}ms, Error: {} ===",
                            searchRequest.getCategory(), duration, error.getMessage());
                });
    }

    // ===========================
    // STEP 1: BASIC SEARCH
    // ===========================

    private Flux<Ad> performBasicSearch(BoatSearchRequest searchRequest) {
        log.debug("=== PERFORMING BASIC SEARCH === Category: {} ===", searchRequest.getCategory());

        return adRepository.basicSearch(
                searchRequest.getCategory() != null ? searchRequest.getCategory().name() : null,
                searchRequest.getLocation(),
                searchRequest.getPriceType() != null ? searchRequest.getPriceType().name() : null,
                searchRequest.getMinPrice(),
                searchRequest.getMaxPrice(),
                searchRequest.getAdType() != null ? searchRequest.getAdType().name() : null,
                true, // only active ads
                searchRequest.getSortBy()
        );
    }

    // ===========================
    // STEP 2: CATEGORY-SPECIFIC FILTERING
    // ===========================

    private Flux<Ad> applyCategorySpecificFiltering(Flux<Ad> baseAds, BoatSearchRequest searchRequest) {
        // If no category-specific filters are applied, return base results
        if (!hasCategorySpecificFilters(searchRequest)) {
            log.debug("=== NO CATEGORY FILTERS === Returning base results ===");
            return baseAds;
        }

        log.debug("=== APPLYING CATEGORY FILTERS === Category: {} ===", searchRequest.getCategory());

        // Get matching ad IDs from category-specific search
        Flux<Long> matchingAdIds = getCategorySpecificAdIds(searchRequest);

        // Filter base ads to only include matching IDs
        return matchingAdIds
                .collectList()
                .flatMapMany(idList -> {
                    if (idList.isEmpty()) {
                        log.debug("=== NO CATEGORY MATCHES === Returning empty results ===");
                        return Flux.empty();
                    }

                    log.debug("=== CATEGORY MATCHES FOUND === Count: {} ===", idList.size());
                    Set<Long> idSet = new HashSet<>(idList);

                    return baseAds.filter(ad -> idSet.contains(ad.getId()));
                });
    }

    private boolean hasCategorySpecificFilters(BoatSearchRequest searchRequest) {
        return (searchRequest.getBrand() != null && !searchRequest.getBrand().trim().isEmpty()) ||
                (searchRequest.getModel() != null && !searchRequest.getModel().trim().isEmpty()) ||
                searchRequest.getMinYear() != null || searchRequest.getMaxYear() != null ||
                (searchRequest.getCondition() != null && !searchRequest.getCondition().trim().isEmpty()) ||
                hasCategoryUniqueFilters(searchRequest);
    }

    // UPDATED METHOD TO INCLUDE NEW CATEGORIES
    private boolean hasCategoryUniqueFilters(BoatSearchRequest searchRequest) {
        if (searchRequest.getCategory() == null) return false;

        switch (searchRequest.getCategory()) {
            case MARINE_ELECTRONICS:
                return searchRequest.getElectronicsType() != null ||
                        searchRequest.getScreenSize() != null ||
                        searchRequest.getGpsIntegrated() != null;
            case FISHING:
                return searchRequest.getFishingType() != null ||
                        searchRequest.getFishingTechnique() != null ||
                        searchRequest.getTargetFish() != null;
            case PARTS:
                return searchRequest.getPartType() != null;
            case SERVICES:
                return searchRequest.getServiceType() != null ||
                        searchRequest.getAuthorizedService() != null ||
                        searchRequest.getSupportedBrand() != null;
            // ADD NEW CATEGORY FILTERS
            case WATER_SPORTS:
                return searchRequest.getWaterSportsType() != null;
            case MARINE_ACCESSORIES:
                return searchRequest.getAccessoryType() != null;
            case RENTALS:
                return searchRequest.getRentalType() != null ||
                        searchRequest.getLicenseRequired() != null ||
                        searchRequest.getManagementType() != null ||
                        searchRequest.getServiceTypeRentals() != null;
            default:
                return false;
        }
    }

    // UPDATED METHOD TO INCLUDE NEW CATEGORIES
    private Flux<Long> getCategorySpecificAdIds(BoatSearchRequest searchRequest) {
        switch (searchRequest.getCategory()) {
            case BOATS_AND_YACHTS:
                return boatSearchRepo.searchBoatAdIds(
                        searchRequest.getBrand(), searchRequest.getModel(),
                        searchRequest.getMinYear(), searchRequest.getMaxYear(),
                        searchRequest.getCondition(), null, null, null, null, null
                );

            case JET_SKIS:
                return jetSkiSearchRepo.searchJetSkiAdIds(
                        searchRequest.getBrand(), searchRequest.getModel(),
                        searchRequest.getMinYear(), searchRequest.getMaxYear(),
                        searchRequest.getCondition(), null, null, null, null
                );

            case TRAILERS:
                return trailerSearchRepo.searchTrailerAdIds(
                        searchRequest.getBrand(), searchRequest.getModel(),
                        searchRequest.getMinYear(), searchRequest.getMaxYear(),
                        searchRequest.getCondition(), null, null, null, null
                );

            case ENGINES:
                return engineSearchRepo.searchEngineAdIds(
                        searchRequest.getBrand(), searchRequest.getMinYear(), searchRequest.getMaxYear(),
                        searchRequest.getCondition(), null, null, null, null, null
                );

            case MARINE_ELECTRONICS:
                return electronicsSearchRepo.searchMarineElectronicsAdIds(
                        searchRequest.getBrand(), searchRequest.getElectronicsType(),
                        searchRequest.getScreenSize(), searchRequest.getGpsIntegrated(),
                        searchRequest.getCondition(), searchRequest.getMinYear(), searchRequest.getMaxYear()
                );

            case FISHING:
                return fishingSearchRepo.searchFishingAdIds(
                        searchRequest.getBrand(), searchRequest.getFishingType(),
                        searchRequest.getFishingTechnique(), searchRequest.getTargetFish(),
                        searchRequest.getCondition()
                );

            case PARTS:
                return partsSearchRepo.searchPartsAdIds(
                        searchRequest.getPartType(), searchRequest.getCondition()
                );

            case SERVICES:
                return servicesSearchRepo.searchServicesAdIds(
                        searchRequest.getServiceType(), searchRequest.getAuthorizedService(),
                        searchRequest.getSupportedBrand(), null
                );

            // ADD NEW CATEGORY SEARCH METHODS
            case WATER_SPORTS:
                return waterSportsSearchRepo.searchWaterSportsAdIds(
                        searchRequest.getBrand(),
                        searchRequest.getWaterSportsType(),
                        searchRequest.getCondition()
                );

            case MARINE_ACCESSORIES:
                return marineAccessoriesSearchRepo.searchMarineAccessoriesAdIds(
                        searchRequest.getBrand(),
                        searchRequest.getAccessoryType(),
                        searchRequest.getCondition()
                );

            case RENTALS:
                return rentalsSearchRepo.searchRentalsAdIds(
                        searchRequest.getRentalType(),
                        searchRequest.getLicenseRequired(),
                        searchRequest.getManagementType(),
                        searchRequest.getServiceTypeRentals(),
                        searchRequest.getCompanyName(),
                        searchRequest.getMinPrice(),
                        searchRequest.getMaxPrice()
                );

            default:
                log.warn("=== UNSUPPORTED CATEGORY FOR SPECIFIC SEARCH === Category: {} ===",
                        searchRequest.getCategory());
                return Flux.empty();
        }
    }

    // ===========================
    // VALIDATION & SORTING (unchanged)
    // ===========================

    private void validateSearchRequest(BoatSearchRequest searchRequest) {
        if (searchRequest.getCategory() == null) {
            throw new InvalidSearchCriteriaException("Category is required for search");
        }

        if (searchRequest.getMinPrice() != null && searchRequest.getMaxPrice() != null) {
            if (searchRequest.getMinPrice().compareTo(searchRequest.getMaxPrice()) > 0) {
                throw new InvalidSearchCriteriaException("Min price cannot be greater than max price");
            }
        }

        if (searchRequest.getMinYear() != null && searchRequest.getMaxYear() != null) {
            if (searchRequest.getMinYear() > searchRequest.getMaxYear()) {
                throw new InvalidSearchCriteriaException("Min year cannot be greater than max year");
            }
        }
    }

    private int applySorting(BoatAdResponse ad1, BoatAdResponse ad2, String sortBy) {
        if (sortBy == null) {
            return ad2.getCreatedAt().compareTo(ad1.getCreatedAt()); // Default: newest first
        }

        switch (sortBy) {
            case "PRICE_LOW_TO_HIGH":
                return comparePrices(ad1, ad2);
            case "PRICE_HIGH_TO_LOW":
                return comparePrices(ad2, ad1);
            case "OLDEST":
                return ad1.getCreatedAt().compareTo(ad2.getCreatedAt());
            case "MOST_VIEWED":
                return ad2.getViewsCount().compareTo(ad1.getViewsCount());
            default:
                return ad2.getCreatedAt().compareTo(ad1.getCreatedAt());
        }
    }

    private int comparePrices(BoatAdResponse ad1, BoatAdResponse ad2) {
        if (ad1.getPrice() == null && ad2.getPrice() == null) return 0;
        if (ad1.getPrice() == null) return 1;
        if (ad2.getPrice() == null) return -1;
        if (ad1.getPrice().getAmount() == null && ad2.getPrice().getAmount() == null) return 0;
        if (ad1.getPrice().getAmount() == null) return 1;
        if (ad2.getPrice().getAmount() == null) return -1;
        return ad1.getPrice().getAmount().compareTo(ad2.getPrice().getAmount());
    }

    // ===========================
    // SIMPLE CATEGORY SEARCH METHODS
    // ===========================

    public Flux<BoatAdResponse> searchByCategory(MainBoatCategory category) {
        log.info("=== SIMPLE CATEGORY SEARCH === Category: {} ===", category);

        BoatSearchRequest searchRequest = BoatSearchRequest.builder()
                .category(category)
                .build();

        return searchAds(searchRequest);
    }

    public Flux<BoatAdResponse> searchByLocation(String location) {
        log.info("=== SIMPLE LOCATION SEARCH === Location: '{}' ===", location);

        return adRepository.basicSearch(null, location, null, null, null, null, true, null)
                .flatMap(marketplaceService::mapToResponse);
    }

    public Flux<BoatAdResponse> getFeaturedAds() {
        log.info("=== GET FEATURED ADS ===");

        return adRepository.findAll()
                .filter(ad -> ad.getActive() && ad.getFeatured())
                .take(10)
                .flatMap(marketplaceService::mapToResponse);
    }

    public Flux<BoatAdResponse> getRecentAds(int limit) {
        log.info("=== GET RECENT ADS === Limit: {} ===", limit);

        return adRepository.findAll()
                .filter(Ad::getActive)
                .sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .take(limit)
                .flatMap(marketplaceService::mapToResponse);
    }

    public Flux<BoatAdResponse> getMostViewedAds(int limit) {
        log.info("=== GET MOST VIEWED ADS === Limit: {} ===", limit);

        return adRepository.findAll()
                .filter(Ad::getActive)
                .sort((a1, a2) -> a2.getViewsCount().compareTo(a1.getViewsCount()))
                .take(limit)
                .flatMap(marketplaceService::mapToResponse);
    }
}