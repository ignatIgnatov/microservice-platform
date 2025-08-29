package com.platform.ads.service;

import com.platform.ads.dto.*;
import com.platform.ads.entity.*;
import com.platform.ads.repository.*;
import com.platform.ads.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BoatMarketplaceService {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    private final AdRepository adRepository;
    private final BoatSpecificationRepository boatSpecRepository;
    private final JetSkiSpecificationRepository jetSkiSpecRepository;
    private final TrailerSpecificationRepository trailerSpecRepository;
    private final EngineSpecificationRepository engineSpecRepository;
    private final BoatInteriorFeatureRepository interiorFeatureRepository;
    private final BoatExteriorFeatureRepository exteriorFeatureRepository;
    private final BoatEquipmentRepository equipmentRepository;
    private final WebClient webClient;

    public BoatMarketplaceService(
            AdRepository adRepository,
            BoatSpecificationRepository boatSpecRepository,
            JetSkiSpecificationRepository jetSkiSpecRepository,
            TrailerSpecificationRepository trailerSpecRepository,
            EngineSpecificationRepository engineSpecRepository,
            BoatInteriorFeatureRepository interiorFeatureRepository,
            BoatExteriorFeatureRepository exteriorFeatureRepository,
            BoatEquipmentRepository equipmentRepository,
            WebClient webClient) {
        this.adRepository = adRepository;
        this.boatSpecRepository = boatSpecRepository;
        this.jetSkiSpecRepository = jetSkiSpecRepository;
        this.trailerSpecRepository = trailerSpecRepository;
        this.engineSpecRepository = engineSpecRepository;
        this.interiorFeatureRepository = interiorFeatureRepository;
        this.exteriorFeatureRepository = exteriorFeatureRepository;
        this.equipmentRepository = equipmentRepository;
        this.webClient = webClient;
    }

    // User validation
    public Mono<UserValidationResponse> validateUser(String email, String token) {
        return webClient.get()
                .uri(authServiceUrl + "/auth/validate-user?email=" + email)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserValidationResponse.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.error("Auth service error: Status {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
                    return new AuthServiceException("Failed to validate user: " + e.getMessage());
                })
                .onErrorMap(Exception.class, e -> {
                    if (e instanceof AuthServiceException) return e;
                    log.error("Unexpected error validating user {}: {}", email, e.getMessage());
                    return new AuthServiceException("User validation failed: " + e.getMessage());
                });
    }

    // CREATE ADS BY CATEGORY
    @Transactional
    public Mono<BoatAdResponse> createBoatAd(BoatAdRequest request, String token) {
        validateCategorySpecificFields(request);

        return validateUser(request.getUserEmail(), token)
                .flatMap(userInfo -> {
                    if (!userInfo.isExists()) {
                        return Mono.error(new UserNotFoundException(request.getUserEmail()));
                    }

                    return createAdWithSpecification(request, userInfo)
                            .flatMap(this::mapToResponse);
                });
    }

    private void validateCategorySpecificFields(BoatAdRequest request) {
        switch (request.getCategory()) {
            case BOATS_AND_YACHTS:
                validateBoatSpecification(request.getBoatSpec());
                break;
            case JET_SKIS:
                validateJetSkiSpecification(request.getJetSkiSpec());
                break;
            case TRAILERS:
                validateTrailerSpecification(request.getTrailerSpec());
                break;
            case ENGINES:
                validateEngineSpecification(request.getEngineSpec());
                break;
            default:
                throw new CategoryMismatchException(request.getCategory().name(), "UNSUPPORTED");
        }
    }

    private void validateBoatSpecification(BoatSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("boatSpec", "BOATS_AND_YACHTS");
        }
        // Add validation logic based on QHTI.BG requirements
        if (spec.getType() == null) {
            throw new MandatoryFieldMissingException("type", "BOATS_AND_YACHTS");
        }
        if (spec.getBrand() == null || spec.getBrand().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("brand", "BOATS_AND_YACHTS");
        }
        // Continue with other mandatory field validations...
    }

    private void validateJetSkiSpecification(JetSkiSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("jetSkiSpec", "JET_SKIS");
        }
        if (spec.getBrand() == null || spec.getBrand().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("brand", "JET_SKIS");
        }
        if (spec.getModel() == null || spec.getModel().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("model", "JET_SKIS");
        }
    }

    private void validateTrailerSpecification(TrailerSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("trailerSpec", "TRAILERS");
        }
        if (spec.getTrailerType() == null) {
            throw new MandatoryFieldMissingException("trailerType", "TRAILERS");
        }
    }

    private void validateEngineSpecification(EngineSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("engineSpec", "ENGINES");
        }
        if (spec.getEngineType() == null) {
            throw new MandatoryFieldMissingException("engineType", "ENGINES");
        }
    }

    private Mono<Ad> createAdWithSpecification(BoatAdRequest request, UserValidationResponse userInfo) {
        // Create main ad
        Ad ad = Ad.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .quickDescription(request.getQuickDescription())
                .category(request.getCategory().name())
                .priceAmount(request.getPrice().getAmount())
                .priceType(request.getPrice().getType().name())
                .includingVat(request.getPrice().getIncludingVat())
                .location(request.getLocation())
                .adType(request.getAdType().name())
                .userEmail(request.getUserEmail())
                .userId(userInfo.getUserId())
                .userFirstName(userInfo.getFirstName())
                .userLastName(userInfo.getLastName())
                .createdAt(LocalDateTime.now())
                .active(true)
                .viewsCount(0)
                .featured(false)
                .build();

        return adRepository.save(ad)
                .flatMap(savedAd -> createCategorySpecification(savedAd, request)
                        .thenReturn(savedAd));
    }

    private Mono<Void> createCategorySpecification(Ad ad, BoatAdRequest request) {
        switch (request.getCategory()) {
            case BOATS_AND_YACHTS:
                return createBoatSpecification(ad.getId(), request.getBoatSpec());
            case JET_SKIS:
                return createJetSkiSpecification(ad.getId(), request.getJetSkiSpec());
            case TRAILERS:
                return createTrailerSpecification(ad.getId(), request.getTrailerSpec());
            case ENGINES:
                return createEngineSpecification(ad.getId(), request.getEngineSpec());
            default:
                return Mono.empty();
        }
    }

    private Mono<Void> createBoatSpecification(Long adId, BoatSpecificationDto spec) {
        BoatSpecification boatSpec = BoatSpecification.builder()
                .adId(adId)
                .boatType(spec.getType().name())
                .brand(spec.getBrand())
                .model(spec.getModel())
                .engineType(spec.getEngineType().name())
                .engineIncluded(spec.getEngineIncluded())
                .engineBrandModel(spec.getEngineBrandModel())
                .horsepower(spec.getHorsepower())
                .length(spec.getLength())
                .width(spec.getWidth())
                .draft(spec.getDraft())
                .maxPeople(spec.getMaxPeople())
                .year(spec.getYear())
                .inWarranty(spec.getInWarranty())
                .weight(spec.getWeight())
                .fuelCapacity(spec.getFuelCapacity())
                .hasWaterTank(spec.getHasWaterTank())
                .numberOfEngines(spec.getNumberOfEngines())
                .hasAuxiliaryEngine(spec.getHasAuxiliaryEngine())
                .consoleType(spec.getConsoleType() != null ? spec.getConsoleType().name() : null)
                .fuelType(spec.getFuelType() != null ? spec.getFuelType().name() : null)
                .material(spec.getMaterial() != null ? spec.getMaterial().name() : null)
                .isRegistered(spec.getIsRegistered())
                .hasCommercialFishingLicense(spec.getHasCommercialFishingLicense())
                .condition(spec.getCondition().name())
                .build();

        return boatSpecRepository.save(boatSpec)
                .flatMap(savedSpec -> saveBoatFeatures(savedSpec.getId(), spec))
                .then();
    }

    private Mono<Void> saveBoatFeatures(Long boatSpecId, BoatSpecificationDto spec) {
        Mono<Void> interiorMono = Mono.empty();
        Mono<Void> exteriorMono = Mono.empty();
        Mono<Void> equipmentMono = Mono.empty();

        if (spec.getInteriorFeatures() != null && !spec.getInteriorFeatures().isEmpty()) {
            interiorMono = Flux.fromIterable(spec.getInteriorFeatures())
                    .map(feature -> BoatInteriorFeature.builder()
                            .boatSpecId(boatSpecId)
                            .feature(feature.name())
                            .build())
                    .flatMap(interiorFeatureRepository::save)
                    .then();
        }

        if (spec.getExteriorFeatures() != null && !spec.getExteriorFeatures().isEmpty()) {
            exteriorMono = Flux.fromIterable(spec.getExteriorFeatures())
                    .map(feature -> BoatExteriorFeature.builder()
                            .boatSpecId(boatSpecId)
                            .feature(feature.name())
                            .build())
                    .flatMap(exteriorFeatureRepository::save)
                    .then();
        }

        if (spec.getEquipment() != null && !spec.getEquipment().isEmpty()) {
            equipmentMono = Flux.fromIterable(spec.getEquipment())
                    .map(equipment -> BoatEquipment.builder()
                            .boatSpecId(boatSpecId)
                            .equipment(equipment.name())
                            .build())
                    .flatMap(equipmentRepository::save)
                    .then();
        }

        return Mono.when(interiorMono, exteriorMono, equipmentMono);
    }

    private Mono<Void> createJetSkiSpecification(Long adId, JetSkiSpecificationDto spec) {
        JetSkiSpecification jetSkiSpec = JetSkiSpecification.builder()
                .adId(adId)
                .brand(spec.getBrand())
                .model(spec.getModel())
                .modification(spec.getModification())
                .isRegistered(spec.getIsRegistered())
                .horsepower(spec.getHorsepower())
                .year(spec.getYear())
                .weight(spec.getWeight())
                .fuelCapacity(spec.getFuelCapacity())
                .operatingHours(spec.getOperatingHours())
                .fuelType(spec.getFuelType().name())
                .trailerIncluded(spec.getTrailerIncluded())
                .inWarranty(spec.getInWarranty())
                .condition(spec.getCondition().name())
                .build();

        return jetSkiSpecRepository.save(jetSkiSpec).then();
    }

    private Mono<Void> createTrailerSpecification(Long adId, TrailerSpecificationDto spec) {
        TrailerSpecification trailerSpec = TrailerSpecification.builder()
                .adId(adId)
                .trailerType(spec.getTrailerType().name())
                .brand(spec.getBrand())
                .model(spec.getModel())
                .axleCount(spec.getAxleCount().name())
                .isRegistered(spec.getIsRegistered())
                .ownWeight(spec.getOwnWeight())
                .loadCapacity(spec.getLoadCapacity())
                .length(spec.getLength())
                .width(spec.getWidth())
                .year(spec.getYear())
                .suspensionType(spec.getSuspensionType() != null ? spec.getSuspensionType().name() : null)
                .keelRollers(spec.getKeelRollers() != null ? spec.getKeelRollers().name() : null)
                .inWarranty(spec.getInWarranty())
                .condition(spec.getCondition().name())
                .build();

        return trailerSpecRepository.save(trailerSpec).then();
    }

    private Mono<Void> createEngineSpecification(Long adId, EngineSpecificationDto spec) {
        EngineSpecification engineSpec = EngineSpecification.builder()
                .adId(adId)
                .engineType(spec.getEngineType().name())
                .brand(spec.getBrand())
                .modification(spec.getModification())
                .strokeType(spec.getStrokeType().name())
                .inWarranty(spec.getInWarranty())
                .horsepower(spec.getHorsepower())
                .operatingHours(spec.getOperatingHours())
                .cylinders(spec.getCylinders())
                .displacementCc(spec.getDisplacementCc())
                .rpm(spec.getRpm())
                .weight(spec.getWeight())
                .year(spec.getYear())
                .fuelCapacity(spec.getFuelCapacity())
                .ignitionType(spec.getIgnitionType().name())
                .controlType(spec.getControlType().name())
                .shaftLength(spec.getShaftLength().name())
                .fuelType(spec.getFuelType().name())
                .engineSystemType(spec.getEngineSystemType().name())
                .condition(spec.getCondition().name())
                .color(spec.getColor().name())
                .build();

        return engineSpecRepository.save(engineSpec).then();
    }

    // SEARCH FUNCTIONALITY
    public Flux<BoatAdResponse> searchAds(BoatSearchRequest searchRequest) {
        validateSearchRequest(searchRequest);

        return performSearch(searchRequest)
                .flatMap(this::mapToResponse)
                .sort((ad1, ad2) -> applySorting(ad1, ad2, searchRequest.getSortBy()));
    }

    private void validateSearchRequest(BoatSearchRequest searchRequest) {
        if (searchRequest.getCategory() == null) {
            throw new InvalidSearchCriteriaException("Category is required for search");
        }

        if (searchRequest.getMinPrice() != null && searchRequest.getMaxPrice() != null) {
            if (searchRequest.getMinPrice().compareTo(searchRequest.getMaxPrice()) > 0) {
                throw new InvalidSearchCriteriaException("Min price cannot be greater than max price");
            }
        }
    }

    private Flux<Ad> performSearch(BoatSearchRequest searchRequest) {
        return adRepository.advancedSearch(
                searchRequest.getCategory().name(),
                searchRequest.getLocation(),
                searchRequest.getPriceType() != null ? searchRequest.getPriceType().name() : null,
                searchRequest.getMinPrice(),
                searchRequest.getMaxPrice(),
                searchRequest.getAdType() != null ? searchRequest.getAdType().name() : null,
                true,
                searchRequest.getSortBy()
        );
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
        if (ad1.getPrice().getAmount() == null && ad2.getPrice().getAmount() == null) return 0;
        if (ad1.getPrice().getAmount() == null) return 1;
        if (ad2.getPrice().getAmount() == null) return -1;
        return ad1.getPrice().getAmount().compareTo(ad2.getPrice().getAmount());
    }

    // GET AD BY ID
    public Mono<BoatAdResponse> getAdById(Long id) {
        return adRepository.findById(id)
                .switchIfEmpty(Mono.error(new AdNotFoundException(id)))
                .flatMap(ad -> adRepository.incrementViewsCount(id)
                        .thenReturn(ad))
                .flatMap(this::mapToResponse);
    }

    // MAPPING
    private Mono<BoatAdResponse> mapToResponse(Ad ad) {
        BoatAdResponse.BoatAdResponseBuilder responseBuilder = BoatAdResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .quickDescription(ad.getQuickDescription())
                .category(BoatCategory.valueOf(ad.getCategory()))
                .price(PriceInfo.builder()
                        .amount(ad.getPriceAmount())
                        .type(PriceInfo.PriceType.valueOf(ad.getPriceType()))
                        .includingVat(ad.getIncludingVat())
                        .build())
                .location(ad.getLocation())
                .adType(AdType.valueOf(ad.getAdType()))
                .userEmail(ad.getUserEmail())
                .userId(ad.getUserId())
                .userFirstName(ad.getUserFirstName())
                .userLastName(ad.getUserLastName())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .active(ad.getActive())
                .viewsCount(ad.getViewsCount())
                .featured(ad.getFeatured());

        // Load category-specific specifications
        return loadSpecificationForResponse(ad.getId(), BoatCategory.valueOf(ad.getCategory()))
                .map(spec -> {
                    switch (BoatCategory.valueOf(ad.getCategory())) {
                        case BOATS_AND_YACHTS:
                            responseBuilder.boatSpec((BoatSpecificationResponse) spec);
                            break;
                        case JET_SKIS:
                            responseBuilder.jetSkiSpec((JetSkiSpecificationResponse) spec);
                            break;
                        case TRAILERS:
                            responseBuilder.trailerSpec((TrailerSpecificationResponse) spec);
                            break;
                        case ENGINES:
                            responseBuilder.engineSpec((EngineSpecificationResponse) spec);
                            break;
                    }
                    return responseBuilder.build();
                })
                .defaultIfEmpty(responseBuilder.build());
    }

    private Mono<Object> loadSpecificationForResponse(Long adId, BoatCategory category) {
        switch (category) {
            case BOATS_AND_YACHTS:
                return boatSpecRepository.findByAdId(adId)
                        .flatMap(this::mapBoatSpecToResponse)
                        .cast(Object.class);
            case JET_SKIS:
                return jetSkiSpecRepository.findByAdId(adId)
                        .map(this::mapJetSkiSpecToResponse)
                        .cast(Object.class);
            case TRAILERS:
                return trailerSpecRepository.findByAdId(adId)
                        .map(this::mapTrailerSpecToResponse)
                        .cast(Object.class);
            case ENGINES:
                return engineSpecRepository.findByAdId(adId)
                        .map(this::mapEngineSpecToResponse)
                        .cast(Object.class);
            default:
                return Mono.empty();
        }
    }

    private Mono<BoatSpecificationResponse> mapBoatSpecToResponse(BoatSpecification spec) {
        return Mono.zip(
                interiorFeatureRepository.findByBoatSpecId(spec.getId()).collectList(),
                exteriorFeatureRepository.findByBoatSpecId(spec.getId()).collectList(),
                equipmentRepository.findByBoatSpecId(spec.getId()).collectList()
        ).map(tuple -> BoatSpecificationResponse.builder()
                .type(BoatSpecificationDto.BoatType.valueOf(spec.getBoatType()))
                .brand(spec.getBrand())
                .model(spec.getModel())
                .engineType(BoatSpecificationDto.EngineType.valueOf(spec.getEngineType()))
                .engineIncluded(spec.getEngineIncluded())
                .engineBrandModel(spec.getEngineBrandModel())
                .horsepower(spec.getHorsepower())
                .length(spec.getLength())
                .width(spec.getWidth())
                .draft(spec.getDraft())
                .maxPeople(spec.getMaxPeople())
                .year(spec.getYear())
                .inWarranty(spec.getInWarranty())
                .weight(spec.getWeight())
                .fuelCapacity(spec.getFuelCapacity())
                .hasWaterTank(spec.getHasWaterTank())
                .numberOfEngines(spec.getNumberOfEngines())
                .hasAuxiliaryEngine(spec.getHasAuxiliaryEngine())
                .consoleType(spec.getConsoleType() != null ? BoatSpecificationDto.ConsoleType.valueOf(spec.getConsoleType()) : null)
                .fuelType(spec.getFuelType() != null ? BoatSpecificationDto.FuelType.valueOf(spec.getFuelType()) : null)
                .material(spec.getMaterial() != null ? BoatSpecificationDto.MaterialType.valueOf(spec.getMaterial()) : null)
                .isRegistered(spec.getIsRegistered())
                .hasCommercialFishingLicense(spec.getHasCommercialFishingLicense())
                .condition(ItemCondition.valueOf(spec.getCondition()))
                .interiorFeatures(tuple.getT1().stream()
                        .map(f -> InteriorFeature.valueOf(f.getFeature()))
                        .collect(Collectors.toList()))
                .exteriorFeatures(tuple.getT2().stream()
                        .map(f -> ExteriorFeature.valueOf(f.getFeature()))
                        .collect(Collectors.toList()))
                .equipment(tuple.getT3().stream()
                        .map(e -> Equipment.valueOf(e.getEquipment()))
                        .collect(Collectors.toList()))
                .build());
    }

    private JetSkiSpecificationResponse mapJetSkiSpecToResponse(JetSkiSpecification spec) {
        return JetSkiSpecificationResponse.builder()
                .brand(spec.getBrand())
                .model(spec.getModel())
                .modification(spec.getModification())
                .isRegistered(spec.getIsRegistered())
                .horsepower(spec.getHorsepower())
                .year(spec.getYear())
                .weight(spec.getWeight())
                .fuelCapacity(spec.getFuelCapacity())
                .operatingHours(spec.getOperatingHours())
                .fuelType(JetSkiSpecificationDto.JetSkiFuelType.valueOf(spec.getFuelType()))
                .trailerIncluded(spec.getTrailerIncluded())
                .inWarranty(spec.getInWarranty())
                .condition(ItemCondition.valueOf(spec.getCondition()))
                .build();
    }

    private TrailerSpecificationResponse mapTrailerSpecToResponse(TrailerSpecification spec) {
        return TrailerSpecificationResponse.builder()
                .trailerType(TrailerSpecificationDto.TrailerType.valueOf(spec.getTrailerType()))
                .brand(spec.getBrand())
                .model(spec.getModel())
                .axleCount(TrailerSpecificationDto.AxleCount.valueOf(spec.getAxleCount()))
                .isRegistered(spec.getIsRegistered())
                .ownWeight(spec.getOwnWeight())
                .loadCapacity(spec.getLoadCapacity())
                .length(spec.getLength())
                .width(spec.getWidth())
                .year(spec.getYear())
                .suspensionType(spec.getSuspensionType() != null ? TrailerSpecificationDto.SuspensionType.valueOf(spec.getSuspensionType()) : null)
                .keelRollers(spec.getKeelRollers() != null ? TrailerSpecificationDto.KeelRollers.valueOf(spec.getKeelRollers()) : null)
                .inWarranty(spec.getInWarranty())
                .condition(ItemCondition.valueOf(spec.getCondition()))
                .build();
    }

    private EngineSpecificationResponse mapEngineSpecToResponse(EngineSpecification spec) {
        return EngineSpecificationResponse.builder()
                .engineType(EngineSpecificationDto.EngineTypeMain.valueOf(spec.getEngineType()))
                .brand(spec.getBrand())
                .modification(spec.getModification())
                .strokeType(EngineSpecificationDto.StrokeType.valueOf(spec.getStrokeType()))
                .inWarranty(spec.getInWarranty())
                .horsepower(spec.getHorsepower())
                .operatingHours(spec.getOperatingHours())
                .cylinders(spec.getCylinders())
                .displacementCc(spec.getDisplacementCc())
                .rpm(spec.getRpm())
                .weight(spec.getWeight())
                .year(spec.getYear())
                .fuelCapacity(spec.getFuelCapacity())
                .ignitionType(EngineSpecificationDto.IgnitionType.valueOf(spec.getIgnitionType()))
                .controlType(EngineSpecificationDto.ControlType.valueOf(spec.getControlType()))
                .shaftLength(EngineSpecificationDto.ShaftLength.valueOf(spec.getShaftLength()))
                .fuelType(EngineSpecificationDto.EngineFuelType.valueOf(spec.getFuelType()))
                .engineSystemType(EngineSpecificationDto.EngineSystemType.valueOf(spec.getEngineSystemType()))
                .condition(ItemCondition.valueOf(spec.getCondition()))
                .color(EngineSpecificationDto.EngineColor.valueOf(spec.getColor()))
                .build();
    }

    // STATISTICS
    public Mono<BoatMarketplaceStatsResponse> getMarketplaceStats() {
        return Mono.zip(
                adRepository.count(),
                adRepository.countByActiveAndCategory(true, null),
                adRepository.countByActiveAndCategory(false, null),
                adRepository.getAveragePrice().defaultIfEmpty(BigDecimal.ZERO),
                adRepository.getMinPrice().defaultIfEmpty(BigDecimal.ZERO),
                adRepository.getMaxPrice().defaultIfEmpty(BigDecimal.ZERO)
        ).map(tuple -> BoatMarketplaceStatsResponse.builder()
                .totalAds(tuple.getT1())
                .activeAds(tuple.getT2())
                .inactiveAds(tuple.getT3())
                .averagePrice(tuple.getT4())
                .minPrice(tuple.getT5())
                .maxPrice(tuple.getT6())
                .build());
    }

    // Inner DTO class for user validation
    public static class UserValidationResponse {
        private boolean exists;
        private String userId;
        private String email;
        private String firstName;
        private String lastName;

        // Constructors, getters, and setters
        public UserValidationResponse() {}

        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }
}