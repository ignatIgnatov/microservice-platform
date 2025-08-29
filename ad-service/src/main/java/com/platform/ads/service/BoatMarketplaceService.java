package com.platform.ads.service;

import com.platform.ads.dto.*;
import com.platform.ads.dto.enums.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private final MarineElectronicsSpecificationRepository marineElectronicsSpecRepository;
    private final FishingSpecificationRepository fishingSpecRepository;
    private final PartsSpecificationRepository partsSpecRepository;
    private final ServicesSpecificationRepository servicesSpecRepository;
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
            MarineElectronicsSpecificationRepository marineElectronicsSpecRepository,
            FishingSpecificationRepository fishingSpecRepository,
            PartsSpecificationRepository partsSpecRepository,
            ServicesSpecificationRepository servicesSpecRepository,
            BoatInteriorFeatureRepository interiorFeatureRepository,
            BoatExteriorFeatureRepository exteriorFeatureRepository,
            BoatEquipmentRepository equipmentRepository,
            WebClient webClient) {
        this.adRepository = adRepository;
        this.boatSpecRepository = boatSpecRepository;
        this.jetSkiSpecRepository = jetSkiSpecRepository;
        this.trailerSpecRepository = trailerSpecRepository;
        this.engineSpecRepository = engineSpecRepository;
        this.marineElectronicsSpecRepository = marineElectronicsSpecRepository;
        this.fishingSpecRepository = fishingSpecRepository;
        this.partsSpecRepository = partsSpecRepository;
        this.servicesSpecRepository = servicesSpecRepository;
        this.interiorFeatureRepository = interiorFeatureRepository;
        this.exteriorFeatureRepository = exteriorFeatureRepository;
        this.equipmentRepository = equipmentRepository;
        this.webClient = webClient;
    }

    // ===========================
    // USER VALIDATION
    // ===========================
    public Mono<UserValidationResponse> validateUser(String email, String token) {
        long startTime = System.currentTimeMillis();
        log.info("=== USER VALIDATION START === Email: {} ===", email);

        return webClient.get()
                .uri(authServiceUrl + "/auth/validate-user?email=" + email)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserValidationResponse.class)
                .timeout(Duration.ofSeconds(10))
                .doOnSuccess(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== USER VALIDATION SUCCESS === Email: {}, Exists: {}, UserID: {}, Duration: {}ms ===",
                            email, response.isExists(), response.getUserId(), duration);
                })
                .onErrorMap(WebClientResponseException.class, e -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== USER VALIDATION AUTH ERROR === Email: {}, Status: {}, Duration: {}ms, Body: {} ===",
                            email, e.getStatusCode(), duration, e.getResponseBodyAsString());
                    return new AuthServiceException("Failed to validate user: " + e.getMessage());
                })
                .onErrorMap(Exception.class, e -> {
                    if (e instanceof AuthServiceException) return e;
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== USER VALIDATION UNEXPECTED ERROR === Email: {}, Duration: {}ms, Error: {} ===",
                            email, duration, e.getMessage(), e);
                    return new AuthServiceException("User validation failed: " + e.getMessage());
                });
    }

    // ===========================
    // AD CREATION
    // ===========================
    @Transactional
    public Mono<BoatAdResponse> createBoatAd(BoatAdRequest request, String token) {
        long startTime = System.currentTimeMillis();
        log.info("=== CREATE BOAT AD START === Category: {}, User: {}, Title: '{}' ===",
                request.getCategory(), request.getUserEmail(), request.getTitle());

        try {
            validateCategorySpecificFields(request);
            log.debug("Category-specific field validation passed for category: {}", request.getCategory());
        } catch (Exception e) {
            log.error("=== VALIDATION FAILED === Category: {}, User: {}, Error: {} ===",
                    request.getCategory(), request.getUserEmail(), e.getMessage());
            throw e;
        }

        return validateUser(request.getUserEmail(), token)
                .flatMap(userInfo -> {
                    if (!userInfo.isExists()) {
                        log.warn("=== USER NOT FOUND === Email: {} ===", request.getUserEmail());
                        return Mono.error(new UserNotFoundException(request.getUserEmail()));
                    }

                    log.info("=== USER VALIDATED === Email: {}, UserID: {}, Name: {} {} ===",
                            request.getUserEmail(), userInfo.getUserId(),
                            userInfo.getFirstName(), userInfo.getLastName());

                    return createAdWithSpecification(request, userInfo)
                            .flatMap(ad -> {
                                long duration = System.currentTimeMillis() - startTime;
                                log.info("=== AD ENTITY CREATED === ID: {}, Duration: {}ms ===", ad.getId(), duration);
                                return this.mapToResponse(ad);
                            });
                })
                .doOnSuccess(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== CREATE BOAT AD SUCCESS === ID: {}, Category: {}, User: {}, Duration: {}ms ===",
                            response.getId(), response.getCategory(), response.getUserEmail(), duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== CREATE BOAT AD FAILED === Category: {}, User: {}, Duration: {}ms, Error: {} ===",
                            request.getCategory(), request.getUserEmail(), duration, error.getMessage());
                });
    }

    private void validateCategorySpecificFields(BoatAdRequest request) {
        log.debug("=== VALIDATING CATEGORY FIELDS === Category: {} ===", request.getCategory());

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
            case MARINE_ELECTRONICS:
                validateMarineElectronicsSpecification(request.getMarineElectronicsSpec());
                break;
            case FISHING:
                validateFishingSpecification(request.getFishingSpec());
                break;
            case PARTS:
                validatePartsSpecification(request.getPartsSpec());
                break;
            case SERVICES:
                validateServicesSpecification(request.getServicesSpec());
                break;
            default:
                log.error("=== UNSUPPORTED CATEGORY === Category: {} ===", request.getCategory());
                throw new CategoryMismatchException(request.getCategory().name(), "UNSUPPORTED");
        }

        log.debug("=== CATEGORY VALIDATION COMPLETE === Category: {} ===", request.getCategory());
    }

    // ===========================
    // VALIDATION METHODS
    // ===========================
    private void validateBoatSpecification(BoatSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("boatSpec", "BOATS_AND_YACHTS");
        }
        if (spec.getType() == null) {
            throw new MandatoryFieldMissingException("type", "BOATS_AND_YACHTS");
        }
        if (spec.getBrand() == null || spec.getBrand().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("brand", "BOATS_AND_YACHTS");
        }
        if (spec.getModel() == null || spec.getModel().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("model", "BOATS_AND_YACHTS");
        }
        if (spec.getEngineType() == null) {
            throw new MandatoryFieldMissingException("engineType", "BOATS_AND_YACHTS");
        }
        if (spec.getEngineIncluded() == null) {
            throw new MandatoryFieldMissingException("engineIncluded", "BOATS_AND_YACHTS");
        }
        if (spec.getHorsepower() == null) {
            throw new MandatoryFieldMissingException("horsepower", "BOATS_AND_YACHTS");
        }
        if (spec.getLength() == null) {
            throw new MandatoryFieldMissingException("length", "BOATS_AND_YACHTS");
        }
        if (spec.getWidth() == null) {
            throw new MandatoryFieldMissingException("width", "BOATS_AND_YACHTS");
        }
        if (spec.getMaxPeople() == null) {
            throw new MandatoryFieldMissingException("maxPeople", "BOATS_AND_YACHTS");
        }
        if (spec.getYear() == null) {
            throw new MandatoryFieldMissingException("year", "BOATS_AND_YACHTS");
        }
        if (spec.getYear() < 1900 || spec.getYear() > LocalDate.now().getYear() + 5) {
            throw new InvalidFieldValueException("year", "Year must be between 1900 and " + (LocalDate.now().getYear() + 5));
        }
        if (spec.getInWarranty() == null) {
            throw new MandatoryFieldMissingException("inWarranty", "BOATS_AND_YACHTS");
        }
        if (spec.getWeight() == null) {
            throw new MandatoryFieldMissingException("weight", "BOATS_AND_YACHTS");
        }
        if (spec.getFuelCapacity() == null) {
            throw new MandatoryFieldMissingException("fuelCapacity", "BOATS_AND_YACHTS");
        }
        if (spec.getHasWaterTank() == null) {
            throw new MandatoryFieldMissingException("hasWaterTank", "BOATS_AND_YACHTS");
        }
        if (spec.getNumberOfEngines() == null) {
            throw new MandatoryFieldMissingException("numberOfEngines", "BOATS_AND_YACHTS");
        }
        if (spec.getHasAuxiliaryEngine() == null) {
            throw new MandatoryFieldMissingException("hasAuxiliaryEngine", "BOATS_AND_YACHTS");
        }
        if (spec.getConsoleType() == null) {
            throw new MandatoryFieldMissingException("consoleType", "BOATS_AND_YACHTS");
        }
        if (spec.getFuelType() == null) {
            throw new MandatoryFieldMissingException("fuelType", "BOATS_AND_YACHTS");
        }
        if (spec.getMaterial() == null) {
            throw new MandatoryFieldMissingException("material", "BOATS_AND_YACHTS");
        }
        if (spec.getIsRegistered() == null) {
            throw new MandatoryFieldMissingException("isRegistered", "BOATS_AND_YACHTS");
        }
        if (spec.getCondition() == null) {
            throw new MandatoryFieldMissingException("condition", "BOATS_AND_YACHTS");
        }
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
        if (spec.getIsRegistered() == null) {
            throw new MandatoryFieldMissingException("isRegistered", "JET_SKIS");
        }
        if (spec.getHorsepower() == null) {
            throw new MandatoryFieldMissingException("horsepower", "JET_SKIS");
        }
        if (spec.getYear() == null) {
            throw new MandatoryFieldMissingException("year", "JET_SKIS");
        }
        if (spec.getYear() < 1900 || spec.getYear() > LocalDate.now().getYear() + 5) {
            throw new InvalidFieldValueException("year", "Year must be between 1900 and " + (LocalDate.now().getYear() + 5));
        }
        if (spec.getWeight() == null) {
            throw new MandatoryFieldMissingException("weight", "JET_SKIS");
        }
        if (spec.getFuelCapacity() == null) {
            throw new MandatoryFieldMissingException("fuelCapacity", "JET_SKIS");
        }
        if (spec.getOperatingHours() == null) {
            throw new MandatoryFieldMissingException("operatingHours", "JET_SKIS");
        }
        if (spec.getFuelType() == null) {
            throw new MandatoryFieldMissingException("fuelType", "JET_SKIS");
        }
        if (spec.getTrailerIncluded() == null) {
            throw new MandatoryFieldMissingException("trailerIncluded", "JET_SKIS");
        }
        if (spec.getInWarranty() == null) {
            throw new MandatoryFieldMissingException("inWarranty", "JET_SKIS");
        }
        if (spec.getCondition() == null) {
            throw new MandatoryFieldMissingException("condition", "JET_SKIS");
        }
    }

    private void validateTrailerSpecification(TrailerSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("trailerSpec", "TRAILERS");
        }
        if (spec.getTrailerType() == null) {
            throw new MandatoryFieldMissingException("trailerType", "TRAILERS");
        }
        if (spec.getAxleCount() == null) {
            throw new MandatoryFieldMissingException("axleCount", "TRAILERS");
        }
        if (spec.getIsRegistered() == null) {
            throw new MandatoryFieldMissingException("isRegistered", "TRAILERS");
        }
        if (spec.getLoadCapacity() == null) {
            throw new MandatoryFieldMissingException("loadCapacity", "TRAILERS");
        }
        if (spec.getLength() == null) {
            throw new MandatoryFieldMissingException("length", "TRAILERS");
        }
        if (spec.getWidth() == null) {
            throw new MandatoryFieldMissingException("width", "TRAILERS");
        }
        if (spec.getYear() == null) {
            throw new MandatoryFieldMissingException("year", "TRAILERS");
        }
        if (spec.getYear() < 1900 || spec.getYear() > LocalDate.now().getYear() + 5) {
            throw new InvalidFieldValueException("year", "Year must be between 1900 and " + (LocalDate.now().getYear() + 5));
        }
        if (spec.getInWarranty() == null) {
            throw new MandatoryFieldMissingException("inWarranty", "TRAILERS");
        }
        if (spec.getCondition() == null) {
            throw new MandatoryFieldMissingException("condition", "TRAILERS");
        }
    }

    private void validateEngineSpecification(EngineSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("engineSpec", "ENGINES");
        }
        if (spec.getEngineType() == null) {
            throw new MandatoryFieldMissingException("engineType", "ENGINES");
        }
        if (spec.getStrokeType() == null) {
            throw new MandatoryFieldMissingException("strokeType", "ENGINES");
        }
        if (spec.getInWarranty() == null) {
            throw new MandatoryFieldMissingException("inWarranty", "ENGINES");
        }
        if (spec.getHorsepower() == null) {
            throw new MandatoryFieldMissingException("horsepower", "ENGINES");
        }
        if (spec.getOperatingHours() == null) {
            throw new MandatoryFieldMissingException("operatingHours", "ENGINES");
        }
        if (spec.getYear() == null) {
            throw new MandatoryFieldMissingException("year", "ENGINES");
        }
        if (spec.getYear() < 1900 || spec.getYear() > LocalDate.now().getYear() + 5) {
            throw new InvalidFieldValueException("year", "Year must be between 1900 and " + (LocalDate.now().getYear() + 5));
        }
        if (spec.getFuelCapacity() == null) {
            throw new MandatoryFieldMissingException("fuelCapacity", "ENGINES");
        }
        if (spec.getIgnitionType() == null) {
            throw new MandatoryFieldMissingException("ignitionType", "ENGINES");
        }
        if (spec.getControlType() == null) {
            throw new MandatoryFieldMissingException("controlType", "ENGINES");
        }
        if (spec.getShaftLength() == null) {
            throw new MandatoryFieldMissingException("shaftLength", "ENGINES");
        }
        if (spec.getFuelType() == null) {
            throw new MandatoryFieldMissingException("fuelType", "ENGINES");
        }
        if (spec.getEngineSystemType() == null) {
            throw new MandatoryFieldMissingException("engineSystemType", "ENGINES");
        }
        if (spec.getCondition() == null) {
            throw new MandatoryFieldMissingException("condition", "ENGINES");
        }
        if (spec.getColor() == null) {
            throw new MandatoryFieldMissingException("color", "ENGINES");
        }
    }

    private void validateMarineElectronicsSpecification(MarineElectronicsSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("marineElectronicsSpec", "MARINE_ELECTRONICS");
        }
        if (spec.getElectronicsType() == null) {
            throw new MandatoryFieldMissingException("electronicsType", "MARINE_ELECTRONICS");
        }
        if (spec.getBrand() == null || spec.getBrand().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("brand", "MARINE_ELECTRONICS");
        }
        if (spec.getCondition() == null) {
            throw new MandatoryFieldMissingException("condition", "MARINE_ELECTRONICS");
        }
    }

    private void validateFishingSpecification(FishingSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("fishingSpec", "FISHING");
        }
        if (spec.getFishingType() == null) {
            throw new MandatoryFieldMissingException("fishingType", "FISHING");
        }
        if (spec.getFishingTechnique() == null) {
            throw new MandatoryFieldMissingException("fishingTechnique", "FISHING");
        }
        if (spec.getTargetFish() == null) {
            throw new MandatoryFieldMissingException("targetFish", "FISHING");
        }
        if (spec.getCondition() == null) {
            throw new MandatoryFieldMissingException("condition", "FISHING");
        }
    }

    private void validatePartsSpecification(PartsSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("partsSpec", "PARTS");
        }
        if (spec.getPartType() == null) {
            throw new MandatoryFieldMissingException("partType", "PARTS");
        }
        if (spec.getCondition() == null) {
            throw new MandatoryFieldMissingException("condition", "PARTS");
        }
    }

    private void validateServicesSpecification(ServicesSpecificationDto spec) {
        if (spec == null) {
            throw new MandatoryFieldMissingException("servicesSpec", "SERVICES");
        }
        if (spec.getServiceType() == null) {
            throw new MandatoryFieldMissingException("serviceType", "SERVICES");
        }
        if (spec.getCompanyName() == null || spec.getCompanyName().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("companyName", "SERVICES");
        }
        if (spec.getContactPhone() == null || spec.getContactPhone().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("contactPhone", "SERVICES");
        }
        if (spec.getContactEmail() == null || spec.getContactEmail().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("contactEmail", "SERVICES");
        }
        if (spec.getAddress() == null || spec.getAddress().trim().isEmpty()) {
            throw new MandatoryFieldMissingException("address", "SERVICES");
        }
    }

    // ===========================
    // AD CREATION WITH SPECIFICATIONS
    // ===========================
    private Mono<Ad> createAdWithSpecification(BoatAdRequest request, UserValidationResponse userInfo) {
        // Create main ad
        Ad ad = Ad.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .quickDescription(request.getQuickDescription())
                .category(request.getCategory().name())
                .priceAmount(request.getPrice() != null ? request.getPrice().getAmount() : null)
                .priceType(request.getPrice() != null ? request.getPrice().getType().name() : null)
                .includingVat(request.getPrice() != null ? request.getPrice().getIncludingVat() : null)
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
            case MARINE_ELECTRONICS:
                return createMarineElectronicsSpecification(ad.getId(), request.getMarineElectronicsSpec());
            case FISHING:
                return createFishingSpecification(ad.getId(), request.getFishingSpec());
            case PARTS:
                return createPartsSpecification(ad.getId(), request.getPartsSpec());
            case SERVICES:
                return createServicesSpecification(ad.getId(), request.getServicesSpec());
            default:
                return Mono.empty();
        }
    }

    // ===========================
    // SPECIFICATION CREATION METHODS
    // ===========================
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

    private Mono<Void> createMarineElectronicsSpecification(Long adId, MarineElectronicsSpecificationDto spec) {
        MarineElectronicsSpecification electronicsSpec = MarineElectronicsSpecification.builder()
                .adId(adId)
                .electronicsType(spec.getElectronicsType().name())
                .brand(spec.getBrand())
                .model(spec.getModel())
                .year(spec.getYear())
                .inWarranty(spec.getInWarranty())
                .condition(spec.getCondition().name())
                // Sonar specific fields
                .workingFrequency(spec.getWorkingFrequency() != null ? spec.getWorkingFrequency().name() : null)
                .depthRange(spec.getDepthRange() != null ? spec.getDepthRange().name() : null)
                .screenSize(spec.getScreenSize() != null ? spec.getScreenSize().name() : null)
                .probeIncluded(spec.getProbeIncluded())
                .screenType(spec.getScreenType() != null ? spec.getScreenType().name() : null)
                .gpsIntegrated(spec.getGpsIntegrated())
                .bulgarianLanguage(spec.getBulgarianLanguage())
                // Probe specific fields
                .power(spec.getPower() != null ? spec.getPower().name() : null)
                .frequency(spec.getFrequency() != null ? spec.getFrequency().name() : null)
                .material(spec.getMaterial())
                .rangeLength(spec.getRangeLength() != null ? spec.getRangeLength().name() : null)
                .mounting(spec.getMounting() != null ? spec.getMounting().name() : null)
                // Trolling motor specific fields
                .thrust(spec.getThrust())
                .voltage(spec.getVoltage() != null ? spec.getVoltage().name() : null)
                .tubeLength(spec.getTubeLength() != null ? spec.getTubeLength().name() : null)
                .controlType(spec.getControlType() != null ? spec.getControlType().name() : null)
                .mountingType(spec.getMountingType() != null ? spec.getMountingType().name() : null)
                .motorType(spec.getMotorType() != null ? spec.getMotorType().name() : null)
                .waterResistance(spec.getWaterResistance() != null ? spec.getWaterResistance().name() : null)
                .weight(spec.getWeight() != null ? spec.getWeight().name() : null)
                .build();

        return marineElectronicsSpecRepository.save(electronicsSpec).then();
    }

    private Mono<Void> createFishingSpecification(Long adId, FishingSpecificationDto spec) {
        FishingSpecification fishingSpec = FishingSpecification.builder()
                .adId(adId)
                .fishingType(spec.getFishingType().name())
                .brand(spec.getBrand())
                .fishingTechnique(spec.getFishingTechnique().name())
                .targetFish(spec.getTargetFish().name())
                .condition(spec.getCondition().name())
                .build();

        return fishingSpecRepository.save(fishingSpec).then();
    }

    private Mono<Void> createPartsSpecification(Long adId, PartsSpecificationDto spec) {
        PartsSpecification partsSpec = PartsSpecification.builder()
                .adId(adId)
                .partType(spec.getPartType().name())
                .condition(spec.getCondition().name())
                .build();

        return partsSpecRepository.save(partsSpec).then();
    }

    private Mono<Void> createServicesSpecification(Long adId, ServicesSpecificationDto spec) {
        ServicesSpecification servicesSpec = ServicesSpecification.builder()
                .adId(adId)
                .serviceType(spec.getServiceType().name())
                .companyName(spec.getCompanyName())
                .isAuthorizedService(spec.getIsAuthorizedService())
                .isOfficialRepresentative(spec.getIsOfficialRepresentative())
                .description(spec.getDescription())
                .contactPhone(spec.getContactPhone())
                .contactPhone2(spec.getContactPhone2())
                .contactEmail(spec.getContactEmail())
                .address(spec.getAddress())
                .website(spec.getWebsite())
                .supportedBrands(spec.getSupportedBrands() != null ?
                        String.join(",", spec.getSupportedBrands()) : null)
                .supportedMaterials(spec.getSupportedMaterials() != null ?
                        spec.getSupportedMaterials().stream().map(Enum::name).collect(Collectors.joining(",")) : null)
                .build();

        return servicesSpecRepository.save(servicesSpec).then();
    }

    // ===========================
    // SEARCH FUNCTIONALITY
    // ===========================
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
        if (ad1.getPrice() == null && ad2.getPrice() == null) return 0;
        if (ad1.getPrice() == null) return 1;
        if (ad2.getPrice() == null) return -1;
        if (ad1.getPrice().getAmount() == null && ad2.getPrice().getAmount() == null) return 0;
        if (ad1.getPrice().getAmount() == null) return 1;
        if (ad2.getPrice().getAmount() == null) return -1;
        return ad1.getPrice().getAmount().compareTo(ad2.getPrice().getAmount());
    }

    // ===========================
    // GET AD BY ID
    // ===========================
    public Mono<BoatAdResponse> getAdById(Long id) {
        return adRepository.findById(id)
                .switchIfEmpty(Mono.error(new AdNotFoundException(id)))
                .flatMap(ad -> adRepository.incrementViewsCount(id)
                        .thenReturn(ad))
                .flatMap(this::mapToResponse);
    }

    // ===========================
    // RESPONSE MAPPING
    // ===========================
    public Mono<BoatAdResponse> mapToResponse(Ad ad) {
        BoatAdResponse.BoatAdResponseBuilder responseBuilder = BoatAdResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .quickDescription(ad.getQuickDescription())
                .category(BoatCategory.valueOf(ad.getCategory()))
                .price(ad.getPriceAmount() != null ? PriceInfo.builder()
                        .amount(ad.getPriceAmount())
                        .type(PriceInfo.PriceType.valueOf(ad.getPriceType()))
                        .includingVat(ad.getIncludingVat())
                        .build() : null)
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
                        case MARINE_ELECTRONICS:
                            responseBuilder.marineElectronicsSpec((MarineElectronicsSpecificationResponse) spec);
                            break;
                        case FISHING:
                            responseBuilder.fishingSpec((FishingSpecificationResponse) spec);
                            break;
                        case PARTS:
                            responseBuilder.partsSpec((PartsSpecificationResponse) spec);
                            break;
                        case SERVICES:
                            responseBuilder.servicesSpec((ServicesSpecificationResponse) spec);
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
            case MARINE_ELECTRONICS:
                return marineElectronicsSpecRepository.findByAdId(adId)
                        .map(this::mapMarineElectronicsSpecToResponse)
                        .cast(Object.class);
            case FISHING:
                return fishingSpecRepository.findByAdId(adId)
                        .map(this::mapFishingSpecToResponse)
                        .cast(Object.class);
            case PARTS:
                return partsSpecRepository.findByAdId(adId)
                        .map(this::mapPartsSpecToResponse)
                        .cast(Object.class);
            case SERVICES:
                return servicesSpecRepository.findByAdId(adId)
                        .map(this::mapServicesSpecToResponse)
                        .cast(Object.class);
            default:
                return Mono.empty();
        }
    }

    // ===========================
    // SPECIFICATION MAPPING METHODS
    // ===========================
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

    private MarineElectronicsSpecificationResponse mapMarineElectronicsSpecToResponse(MarineElectronicsSpecification spec) {
        return MarineElectronicsSpecificationResponse.builder()
                .electronicsType(MarineElectronicsSpecificationDto.ElectronicsType.valueOf(spec.getElectronicsType()))
                .brand(spec.getBrand())
                .model(spec.getModel())
                .year(spec.getYear())
                .inWarranty(spec.getInWarranty())
                .condition(ItemCondition.valueOf(spec.getCondition()))
                .workingFrequency(spec.getWorkingFrequency() != null ?
                        MarineElectronicsSpecificationDto.WorkingFrequency.valueOf(spec.getWorkingFrequency()) : null)
                .depthRange(spec.getDepthRange() != null ?
                        MarineElectronicsSpecificationDto.DepthRange.valueOf(spec.getDepthRange()) : null)
                .screenSize(spec.getScreenSize() != null ?
                        MarineElectronicsSpecificationDto.ScreenSize.valueOf(spec.getScreenSize()) : null)
                .probeIncluded(spec.getProbeIncluded())
                .screenType(spec.getScreenType() != null ?
                        MarineElectronicsSpecificationDto.ScreenType.valueOf(spec.getScreenType()) : null)
                .gpsIntegrated(spec.getGpsIntegrated())
                .bulgarianLanguage(spec.getBulgarianLanguage())
                .power(spec.getPower() != null ?
                        MarineElectronicsSpecificationDto.Power.valueOf(spec.getPower()) : null)
                .frequency(spec.getFrequency() != null ?
                        MarineElectronicsSpecificationDto.Frequency.valueOf(spec.getFrequency()) : null)
                .material(spec.getMaterial())
                .rangeLength(spec.getRangeLength() != null ?
                        MarineElectronicsSpecificationDto.RangeLength.valueOf(spec.getRangeLength()) : null)
                .mounting(spec.getMounting() != null ?
                        MarineElectronicsSpecificationDto.Mounting.valueOf(spec.getMounting()) : null)
                .thrust(spec.getThrust())
                .voltage(spec.getVoltage() != null ?
                        MarineElectronicsSpecificationDto.Voltage.valueOf(spec.getVoltage()) : null)
                .tubeLength(spec.getTubeLength() != null ?
                        MarineElectronicsSpecificationDto.TubeLength.valueOf(spec.getTubeLength()) : null)
                .controlType(spec.getControlType() != null ?
                        MarineElectronicsSpecificationDto.TrollingControlType.valueOf(spec.getControlType()) : null)
                .mountingType(spec.getMountingType() != null ?
                        MarineElectronicsSpecificationDto.MountingType.valueOf(spec.getMountingType()) : null)
                .motorType(spec.getMotorType() != null ?
                        MarineElectronicsSpecificationDto.MotorType.valueOf(spec.getMotorType()) : null)
                .waterResistance(spec.getWaterResistance() != null ?
                        MarineElectronicsSpecificationDto.WaterResistance.valueOf(spec.getWaterResistance()) : null)
                .weight(spec.getWeight() != null ?
                        MarineElectronicsSpecificationDto.Weight.valueOf(spec.getWeight()) : null)
                .build();
    }

    private FishingSpecificationResponse mapFishingSpecToResponse(FishingSpecification spec) {
        return FishingSpecificationResponse.builder()
                .fishingType(FishingSpecificationDto.FishingType.valueOf(spec.getFishingType()))
                .brand(spec.getBrand())
                .fishingTechnique(FishingSpecificationDto.FishingTechnique.valueOf(spec.getFishingTechnique()))
                .targetFish(FishingSpecificationDto.TargetFish.valueOf(spec.getTargetFish()))
                .condition(ItemCondition.valueOf(spec.getCondition()))
                .build();
    }

    private PartsSpecificationResponse mapPartsSpecToResponse(PartsSpecification spec) {
        return PartsSpecificationResponse.builder()
                .partType(PartsSpecificationDto.PartType.valueOf(spec.getPartType()))
                .condition(ItemCondition.valueOf(spec.getCondition()))
                .build();
    }

    private ServicesSpecificationResponse mapServicesSpecToResponse(ServicesSpecification spec) {
        return ServicesSpecificationResponse.builder()
                .serviceType(ServicesSpecificationDto.ServiceType.valueOf(spec.getServiceType()))
                .companyName(spec.getCompanyName())
                .isAuthorizedService(spec.getIsAuthorizedService())
                .isOfficialRepresentative(spec.getIsOfficialRepresentative())
                .description(spec.getDescription())
                .contactPhone(spec.getContactPhone())
                .contactPhone2(spec.getContactPhone2())
                .contactEmail(spec.getContactEmail())
                .address(spec.getAddress())
                .website(spec.getWebsite())
                .supportedBrands(spec.getSupportedBrands() != null ?
                        Arrays.asList(spec.getSupportedBrands().split(",")) : null)
                .supportedMaterials(spec.getSupportedMaterials() != null ?
                        Arrays.stream(spec.getSupportedMaterials().split(","))
                                .map(ServicesSpecificationDto.MaterialType::valueOf)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    // ===========================
    // STATISTICS
    // ===========================
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

    // ===========================
    // INNER DTO CLASS
    // ===========================
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