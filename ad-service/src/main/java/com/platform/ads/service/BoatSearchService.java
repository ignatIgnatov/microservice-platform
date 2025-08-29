package com.platform.ads.service;

import com.platform.ads.dto.BoatAdResponse;
import com.platform.ads.dto.BoatSearchRequest;
import com.platform.ads.dto.FishingSpecificationDto;
import com.platform.ads.dto.MarineElectronicsSpecificationDto;
import com.platform.ads.dto.PartsSpecificationDto;
import com.platform.ads.dto.ServicesSpecificationDto;
import com.platform.ads.dto.enums.BoatCategory;
import com.platform.ads.entity.Ad;
import com.platform.ads.exception.InvalidSearchCriteriaException;
import com.platform.ads.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoatSearchService {

    private final AdRepository adRepository;
    private final BoatMarketplaceService marketplaceService;

    // ===========================
    // MAIN SEARCH FUNCTIONALITY
    // ===========================

    public Flux<BoatAdResponse> searchAds(BoatSearchRequest searchRequest) {
        long startTime = System.currentTimeMillis();
        log.info("=== SEARCH ADS START === Category: {}, Location: '{}', PriceRange: {}-{}, Brand: '{}', Model: '{}' ===",
                searchRequest.getCategory(), searchRequest.getLocation(),
                searchRequest.getMinPrice(), searchRequest.getMaxPrice(),
                searchRequest.getBrand(), searchRequest.getModel());

        try {
            validateSearchRequest(searchRequest);
            log.debug("Search request validation passed for category: {}", searchRequest.getCategory());
        } catch (Exception e) {
            log.error("=== SEARCH VALIDATION FAILED === Category: {}, Error: {} ===",
                    searchRequest.getCategory(), e.getMessage());
            throw e;
        }

        return performAdvancedSearch(searchRequest)
                .doOnNext(ad -> log.debug("=== SEARCH RESULT === AdID: {}, Title: '{}', Category: {}, Price: {} ===",
                        ad.getId(), ad.getTitle(), ad.getCategory(),
                        ad.getPriceAmount() != null ? ad.getPriceAmount().toString() : "N/A"))
                .flatMap(ad -> {
                    log.debug("=== MAPPING AD TO RESPONSE === AdID: {} ===", ad.getId());
                    return marketplaceService.mapToResponse(ad);
                })
                .doOnNext(response -> log.debug("=== MAPPED RESPONSE === AdID: {}, ViewsCount: {} ===",
                        response.getId(), response.getViewsCount()))
                .sort((ad1, ad2) -> applySorting(ad1, ad2, searchRequest.getSortBy()))
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== SEARCH ADS COMPLETE === Category: {}, SortBy: '{}', Duration: {}ms ===",
                            searchRequest.getCategory(), searchRequest.getSortBy(), duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== SEARCH ADS ERROR === Category: {}, Duration: {}ms, Error: {} ===",
                            searchRequest.getCategory(), duration, error.getMessage(), error);
                });
    }

    // ===========================
    // SEARCH VALIDATION
    // ===========================

    private void validateSearchRequest(BoatSearchRequest searchRequest) {
        log.debug("=== VALIDATING SEARCH REQUEST === Category: {} ===", searchRequest.getCategory());

        if (searchRequest.getCategory() == null) {
            log.error("=== VALIDATION ERROR === Missing required category field ===");
            throw new InvalidSearchCriteriaException("Category is required for search");
        }

        // Price validation
        if (searchRequest.getMinPrice() != null && searchRequest.getMaxPrice() != null) {
            if (searchRequest.getMinPrice().compareTo(searchRequest.getMaxPrice()) > 0) {
                log.error("=== VALIDATION ERROR === Invalid price range: min={}, max={} ===",
                        searchRequest.getMinPrice(), searchRequest.getMaxPrice());
                throw new InvalidSearchCriteriaException("Min price cannot be greater than max price");
            }
            log.debug("Price range validation passed: {}-{}", searchRequest.getMinPrice(), searchRequest.getMaxPrice());
        }

        // Year validation
        if (searchRequest.getMinYear() != null && searchRequest.getMaxYear() != null) {
            if (searchRequest.getMinYear() > searchRequest.getMaxYear()) {
                log.error("=== VALIDATION ERROR === Invalid year range: min={}, max={} ===",
                        searchRequest.getMinYear(), searchRequest.getMaxYear());
                throw new InvalidSearchCriteriaException("Min year cannot be greater than max year");
            }
            log.debug("Year range validation passed: {}-{}", searchRequest.getMinYear(), searchRequest.getMaxYear());
        }

        // Category-specific validation
        validateCategorySpecificCriteria(searchRequest);

        log.debug("=== SEARCH VALIDATION COMPLETE === Category: {} ===", searchRequest.getCategory());
    }

    private void validateCategorySpecificCriteria(BoatSearchRequest searchRequest) {
        BoatCategory category = searchRequest.getCategory();
        log.debug("=== VALIDATING CATEGORY-SPECIFIC CRITERIA === Category: {} ===", category);

        switch (category) {
            case MARINE_ELECTRONICS:
                validateMarineElectronicsSearch(searchRequest);
                break;
            case FISHING:
                validateFishingSearch(searchRequest);
                break;
            case PARTS:
                validatePartsSearch(searchRequest);
                break;
            case SERVICES:
                validateServicesSearch(searchRequest);
                break;
            default:
                log.debug("No specific validation required for category: {}", category);
        }
    }

    private void validateMarineElectronicsSearch(BoatSearchRequest searchRequest) {
        log.debug("=== VALIDATING MARINE ELECTRONICS SEARCH ===");

        if (searchRequest.getElectronicsType() != null) {
            try {
                MarineElectronicsSpecificationDto.ElectronicsType.valueOf(searchRequest.getElectronicsType());
                log.debug("Electronics type validation passed: {}", searchRequest.getElectronicsType());
            } catch (IllegalArgumentException e) {
                log.error("=== MARINE ELECTRONICS VALIDATION ERROR === Invalid electronics type: {} ===",
                        searchRequest.getElectronicsType());
                throw new InvalidSearchCriteriaException("Invalid electronics type: " + searchRequest.getElectronicsType());
            }
        }

        if (searchRequest.getScreenSize() != null) {
            log.debug("Screen size filter applied: {}", searchRequest.getScreenSize());
        }

        if (searchRequest.getGpsIntegrated() != null) {
            log.debug("GPS integrated filter applied: {}", searchRequest.getGpsIntegrated());
        }
    }

    private void validateFishingSearch(BoatSearchRequest searchRequest) {
        log.debug("=== VALIDATING FISHING SEARCH ===");

        if (searchRequest.getFishingType() != null) {
            try {
                FishingSpecificationDto.FishingType.valueOf(searchRequest.getFishingType());
                log.debug("Fishing type validation passed: {}", searchRequest.getFishingType());
            } catch (IllegalArgumentException e) {
                log.error("=== FISHING VALIDATION ERROR === Invalid fishing type: {} ===",
                        searchRequest.getFishingType());
                throw new InvalidSearchCriteriaException("Invalid fishing type: " + searchRequest.getFishingType());
            }
        }

        if (searchRequest.getFishingTechnique() != null) {
            try {
                FishingSpecificationDto.FishingTechnique.valueOf(searchRequest.getFishingTechnique());
                log.debug("Fishing technique validation passed: {}", searchRequest.getFishingTechnique());
            } catch (IllegalArgumentException e) {
                log.error("=== FISHING VALIDATION ERROR === Invalid fishing technique: {} ===",
                        searchRequest.getFishingTechnique());
                throw new InvalidSearchCriteriaException("Invalid fishing technique: " + searchRequest.getFishingTechnique());
            }
        }

        if (searchRequest.getTargetFish() != null) {
            log.debug("Target fish filter applied: {}", searchRequest.getTargetFish());
        }
    }

    private void validatePartsSearch(BoatSearchRequest searchRequest) {
        log.debug("=== VALIDATING PARTS SEARCH ===");

        if (searchRequest.getPartType() != null) {
            try {
                PartsSpecificationDto.PartType.valueOf(searchRequest.getPartType());
                log.debug("Part type validation passed: {}", searchRequest.getPartType());
            } catch (IllegalArgumentException e) {
                log.error("=== PARTS VALIDATION ERROR === Invalid part type: {} ===",
                        searchRequest.getPartType());
                throw new InvalidSearchCriteriaException("Invalid part type: " + searchRequest.getPartType());
            }
        }
    }

    private void validateServicesSearch(BoatSearchRequest searchRequest) {
        log.debug("=== VALIDATING SERVICES SEARCH ===");

        if (searchRequest.getServiceType() != null) {
            try {
                ServicesSpecificationDto.ServiceType.valueOf(searchRequest.getServiceType());
                log.debug("Service type validation passed: {}", searchRequest.getServiceType());
            } catch (IllegalArgumentException e) {
                log.error("=== SERVICES VALIDATION ERROR === Invalid service type: {} ===",
                        searchRequest.getServiceType());
                throw new InvalidSearchCriteriaException("Invalid service type: " + searchRequest.getServiceType());
            }
        }

        if (searchRequest.getAuthorizedService() != null) {
            log.debug("Authorized service filter applied: {}", searchRequest.getAuthorizedService());
        }

        if (searchRequest.getSupportedBrand() != null) {
            log.debug("Supported brand filter applied: {}", searchRequest.getSupportedBrand());
        }
    }

    // ===========================
    // SEARCH EXECUTION
    // ===========================

    private Flux<Ad> performAdvancedSearch(BoatSearchRequest searchRequest) {
        log.info("=== EXECUTING ADVANCED SEARCH === Category: {}, Filters: Brand='{}', Model='{}', Location='{}' ===",
                searchRequest.getCategory(), searchRequest.getBrand(), searchRequest.getModel(), searchRequest.getLocation());

        return adRepository.advancedSearch(
                        searchRequest.getCategory() != null ? searchRequest.getCategory().name() : null,
                        searchRequest.getLocation(),
                        searchRequest.getPriceType() != null ? searchRequest.getPriceType().name() : null,
                        searchRequest.getMinPrice(),
                        searchRequest.getMaxPrice(),
                        searchRequest.getAdType() != null ? searchRequest.getAdType().name() : null,
                        true, // only active ads
                        searchRequest.getSortBy(),
                        searchRequest.getBrand(),
                        searchRequest.getModel(),
                        searchRequest.getMinYear(),
                        searchRequest.getMaxYear(),
                        searchRequest.getCondition(),
                        searchRequest.getElectronicsType(),
                        searchRequest.getScreenSize(),
                        searchRequest.getGpsIntegrated(),
                        searchRequest.getFishingType(),
                        searchRequest.getFishingTechnique(),
                        searchRequest.getTargetFish(),
                        searchRequest.getPartType(),
                        searchRequest.getServiceType(),
                        searchRequest.getAuthorizedService(),
                        searchRequest.getSupportedBrand()
                )
                .doOnNext(ad -> log.debug("=== RAW SEARCH RESULT === AdID: {}, Category: {}, Title: '{}' ===",
                        ad.getId(), ad.getCategory(), ad.getTitle()))
                .doOnComplete(() -> log.debug("=== RAW SEARCH QUERY COMPLETE ==="));
    }

    private int applySorting(BoatAdResponse ad1, BoatAdResponse ad2, String sortBy) {
        if (sortBy == null) {
            log.debug("Applying default sorting (newest first)");
            return ad2.getCreatedAt().compareTo(ad1.getCreatedAt());
        }

        log.debug("=== APPLYING SORTING === SortBy: {} ===", sortBy);

        switch (sortBy) {
            case "PRICE_LOW_TO_HIGH":
                log.debug("Sorting by price: low to high");
                return comparePrices(ad1, ad2);
            case "PRICE_HIGH_TO_LOW":
                log.debug("Sorting by price: high to low");
                return comparePrices(ad2, ad1);
            case "OLDEST":
                log.debug("Sorting by date: oldest first");
                return ad1.getCreatedAt().compareTo(ad2.getCreatedAt());
            case "MOST_VIEWED":
                log.debug("Sorting by views: most viewed first");
                return ad2.getViewsCount().compareTo(ad1.getViewsCount());
            default:
                log.debug("Unknown sort option '{}', using default (newest first)", sortBy);
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
    // CATEGORY-SPECIFIC SEARCH
    // ===========================

    public Flux<BoatAdResponse> searchByCategory(BoatCategory category) {
        long startTime = System.currentTimeMillis();
        log.info("=== SEARCH BY CATEGORY START === Category: {} ===", category);

        BoatSearchRequest searchRequest = BoatSearchRequest.builder()
                .category(category)
                .build();

        return searchAds(searchRequest)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== SEARCH BY CATEGORY COMPLETE === Category: {}, Duration: {}ms ===",
                            category, duration);
                });
    }

    // ===========================
    // LOCATION-BASED SEARCH
    // ===========================

    public Flux<BoatAdResponse> searchByLocation(String location) {
        long startTime = System.currentTimeMillis();
        log.info("=== SEARCH BY LOCATION START === Location: '{}' ===", location);

        return adRepository.findAll()
                .filter(ad -> {
                    boolean isActive = ad.getActive();
                    boolean locationMatch = location == null ||
                            ad.getLocation().toLowerCase().contains(location.toLowerCase());

                    if (!isActive) {
                        log.trace("Filtering out inactive ad: {}", ad.getId());
                    }
                    if (!locationMatch) {
                        log.trace("Filtering out ad {} due to location mismatch: '{}' vs '{}'",
                                ad.getId(), ad.getLocation(), location);
                    }

                    return isActive && locationMatch;
                })
                .doOnNext(ad -> log.debug("=== LOCATION MATCH === AdID: {}, Location: '{}' ===",
                        ad.getId(), ad.getLocation()))
                .flatMap(ad -> {
                    log.debug("=== MAPPING LOCATION RESULT === AdID: {} ===", ad.getId());
                    return marketplaceService.mapToResponse(ad);
                })
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== SEARCH BY LOCATION COMPLETE === Location: '{}', Duration: {}ms ===",
                            location, duration);
                });
    }

    // ===========================
    // FEATURED ADS
    // ===========================

    public Flux<BoatAdResponse> getFeaturedAds() {
        long startTime = System.currentTimeMillis();
        log.info("=== GET FEATURED ADS START ===");

        return adRepository.findAll()
                .filter(ad -> {
                    boolean isFeatured = ad.getActive() && ad.getFeatured();
                    if (isFeatured) {
                        log.debug("=== FEATURED AD FOUND === AdID: {}, Title: '{}' ===",
                                ad.getId(), ad.getTitle());
                    }
                    return isFeatured;
                })
                .flatMap(ad -> {
                    log.debug("=== MAPPING FEATURED AD === AdID: {} ===", ad.getId());
                    return marketplaceService.mapToResponse(ad);
                })
                .take(10)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== GET FEATURED ADS COMPLETE === Duration: {}ms ===", duration);
                });
    }

    // ===========================
    // RECENT ADS
    // ===========================

    public Flux<BoatAdResponse> getRecentAds(int limit) {
        long startTime = System.currentTimeMillis();
        log.info("=== GET RECENT ADS START === Limit: {} ===", limit);

        return adRepository.findAll()
                .filter(ad -> {
                    if (ad.getActive()) {
                        log.trace("Including active ad: {} (created: {})", ad.getId(), ad.getCreatedAt());
                        return true;
                    }
                    log.trace("Filtering out inactive ad: {}", ad.getId());
                    return false;
                })
                .sort((a1, a2) -> {
                    int comparison = a2.getCreatedAt().compareTo(a1.getCreatedAt());
                    if (comparison == 0) {
                        log.trace("Same creation time for ads {} and {}", a1.getId(), a2.getId());
                    }
                    return comparison;
                })
                .take(limit)
                .doOnNext(ad -> log.debug("=== RECENT AD === AdID: {}, Created: {} ===",
                        ad.getId(), ad.getCreatedAt()))
                .flatMap(ad -> {
                    log.debug("=== MAPPING RECENT AD === AdID: {} ===", ad.getId());
                    return marketplaceService.mapToResponse(ad);
                })
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== GET RECENT ADS COMPLETE === Limit: {}, Duration: {}ms ===", limit, duration);
                });
    }

    // ===========================
    // MOST VIEWED ADS
    // ===========================

    public Flux<BoatAdResponse> getMostViewedAds(int limit) {
        long startTime = System.currentTimeMillis();
        log.info("=== GET MOST VIEWED ADS START === Limit: {} ===", limit);

        return adRepository.findAll()
                .filter(ad -> {
                    if (ad.getActive()) {
                        log.trace("Including active ad: {} (views: {})", ad.getId(), ad.getViewsCount());
                        return true;
                    }
                    log.trace("Filtering out inactive ad: {}", ad.getId());
                    return false;
                })
                .sort((a1, a2) -> {
                    int comparison = a2.getViewsCount().compareTo(a1.getViewsCount());
                    if (comparison == 0) {
                        log.trace("Same view count for ads {} and {} ({})", a1.getId(), a2.getId(), a1.getViewsCount());
                        // Secondary sort by creation date for ties
                        return a2.getCreatedAt().compareTo(a1.getCreatedAt());
                    }
                    return comparison;
                })
                .take(limit)
                .doOnNext(ad -> log.debug("=== POPULAR AD === AdID: {}, Views: {}, Title: '{}' ===",
                        ad.getId(), ad.getViewsCount(), ad.getTitle()))
                .flatMap(ad -> {
                    log.debug("=== MAPPING POPULAR AD === AdID: {} ===", ad.getId());
                    return marketplaceService.mapToResponse(ad);
                })
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== GET MOST VIEWED ADS COMPLETE === Limit: {}, Duration: {}ms ===", limit, duration);
                });
    }
}