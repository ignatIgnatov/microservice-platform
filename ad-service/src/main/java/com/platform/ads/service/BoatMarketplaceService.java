package com.platform.ads.service;

import com.platform.ads.dto.BoatAdRequest;
import com.platform.ads.dto.BoatAdResponse;
import com.platform.ads.dto.BoatMarketplaceStatsResponse;
import com.platform.ads.dto.BoatSpecificationDto;
import com.platform.ads.dto.BoatSpecificationResponse;
import com.platform.ads.dto.EngineSpecificationDto;
import com.platform.ads.dto.EngineSpecificationResponse;
import com.platform.ads.dto.FishingSpecificationDto;
import com.platform.ads.dto.FishingSpecificationResponse;
import com.platform.ads.dto.ImageUploadResponse;
import com.platform.ads.dto.JetSkiSpecificationDto;
import com.platform.ads.dto.JetSkiSpecificationResponse;
import com.platform.ads.dto.MarineAccessoriesSpecificationDto;
import com.platform.ads.dto.MarineAccessoriesSpecificationResponse;
import com.platform.ads.dto.MarineElectronicsSpecificationDto;
import com.platform.ads.dto.MarineElectronicsSpecificationResponse;
import com.platform.ads.dto.PartsSpecificationDto;
import com.platform.ads.dto.PartsSpecificationResponse;
import com.platform.ads.dto.PriceInfo;
import com.platform.ads.dto.RentalsSpecificationDto;
import com.platform.ads.dto.RentalsSpecificationResponse;
import com.platform.ads.dto.ServicesSpecificationDto;
import com.platform.ads.dto.ServicesSpecificationResponse;
import com.platform.ads.dto.TrailerSpecificationDto;
import com.platform.ads.dto.TrailerSpecificationResponse;
import com.platform.ads.dto.UserValidationResponse;
import com.platform.ads.dto.ValidatedImageData;
import com.platform.ads.dto.WaterSportsSpecificationDto;
import com.platform.ads.dto.WaterSportsSpecificationResponse;
import com.platform.ads.dto.enums.AdType;
import com.platform.ads.dto.enums.Equipment;
import com.platform.ads.dto.enums.ExteriorFeature;
import com.platform.ads.dto.enums.InteriorFeature;
import com.platform.ads.dto.enums.ItemCondition;
import com.platform.ads.dto.enums.MainBoatCategory;
import com.platform.ads.entity.Ad;
import com.platform.ads.entity.AdImage;
import com.platform.ads.entity.BoatEquipment;
import com.platform.ads.entity.BoatExteriorFeature;
import com.platform.ads.entity.BoatInteriorFeature;
import com.platform.ads.entity.BoatSpecification;
import com.platform.ads.entity.EngineSpecification;
import com.platform.ads.entity.FishingSpecification;
import com.platform.ads.entity.JetSkiSpecification;
import com.platform.ads.entity.MarineElectronicsSpecification;
import com.platform.ads.entity.PartsSpecification;
import com.platform.ads.entity.RentalsSpecification;
import com.platform.ads.entity.ServicesSpecification;
import com.platform.ads.entity.TrailerSpecification;
import com.platform.ads.entity.WaterSportsSpecification;
import com.platform.ads.exception.AdNotFoundException;
import com.platform.ads.exception.AuthServiceException;
import com.platform.ads.exception.CategoryMismatchException;
import com.platform.ads.exception.InvalidFieldValueException;
import com.platform.ads.exception.MandatoryFieldMissingException;
import com.platform.ads.exception.UserNotFoundException;
import com.platform.ads.repository.AdImageRepository;
import com.platform.ads.repository.AdRepository;
import com.platform.ads.repository.BoatEquipmentRepository;
import com.platform.ads.repository.BoatExteriorFeatureRepository;
import com.platform.ads.repository.BoatInteriorFeatureRepository;
import com.platform.ads.repository.BoatSpecificationRepository;
import com.platform.ads.repository.EngineSpecificationRepository;
import com.platform.ads.repository.FishingSpecificationRepository;
import com.platform.ads.repository.JetSkiSpecificationRepository;
import com.platform.ads.repository.MarineAccessoriesSpecificationRepository;
import com.platform.ads.repository.MarineElectronicsSpecificationRepository;
import com.platform.ads.repository.PartsSpecificationRepository;
import com.platform.ads.repository.RentalsSpecificationRepository;
import com.platform.ads.repository.ServicesSpecificationRepository;
import com.platform.ads.repository.TrailerSpecificationRepository;
import com.platform.ads.repository.WaterSportsSpecificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

// New Entities
import com.platform.ads.entity.MarineAccessoriesSpecification;

// New Repositories (you'll need to create these)

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BoatMarketplaceService {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.base-url}")
    private String s3BaseUrl;

    private static final String OUTPUT_CONTENT_TYPE = "image/webp";

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final int MIN_IMAGES_REQUIRED = 1;
    private static final int MAX_IMAGES_ALLOWED = 14;

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
    private final AdImageRepository adImageRepository;
    private final S3Client s3Client;
    private final BrandService brandService;
    private final ImageConversionService imageConversionService;
    private final WaterSportsSpecificationRepository waterSportsSpecRepository;
    private final MarineAccessoriesSpecificationRepository marineAccessoriesSpecRepository;
    private final RentalsSpecificationRepository rentalsSpecRepository;
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
            BoatEquipmentRepository equipmentRepository, AdImageRepository adImageRepository, S3Client s3Client,
            BrandService brandService, ImageConversionService imageConversionService, WaterSportsSpecificationRepository waterSportsSpecRepository, MarineAccessoriesSpecificationRepository marineAccessoriesSpecRepository, RentalsSpecificationRepository rentalsSpecRepository,
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
        this.adImageRepository = adImageRepository;
        this.s3Client = s3Client;
        this.brandService = brandService;
        this.imageConversionService = imageConversionService;
        this.waterSportsSpecRepository = waterSportsSpecRepository;
        this.marineAccessoriesSpecRepository = marineAccessoriesSpecRepository;
        this.rentalsSpecRepository = rentalsSpecRepository;
        this.webClient = webClient;
    }

    // ===========================
    // AD CREATION
    // ===========================
    @Transactional
    public Mono<BoatAdResponse> createBoatAdWithImages(BoatAdRequest request, Flux<FilePart> images, String token) {
        long startTime = System.currentTimeMillis();
        log.info("=== CREATE BOAT AD WITH IMAGES START === Category: {}, User: {} ===",
                request.getCategory(), request.getUserEmail());

        return validateImagesFirst(images)
                .flatMap(imageList -> {
                    if (imageList.isEmpty()) {
                        log.error("=== NO IMAGES PROVIDED === User: {} ===", request.getUserEmail());
                        return Mono.error(new MandatoryFieldMissingException("images",
                                "At least " + MIN_IMAGES_REQUIRED + " image is required"));
                    }

                    if (imageList.size() > MAX_IMAGES_ALLOWED) {
                        log.error("=== TOO MANY IMAGES === Count: {}, Max: {} ===", imageList.size(), MAX_IMAGES_ALLOWED);
                        return Mono.error(new InvalidFieldValueException("images",
                                "Maximum " + MAX_IMAGES_ALLOWED + " images allowed"));
                    }

                    log.info("=== IMAGES VALIDATED === Count: {}, User: {} ===", imageList.size(), request.getUserEmail());

                    return validateCategorySpecificFieldsAsync(request)
                            .then(validateUser(request.getUserEmail(), token))
                            .flatMap(userInfo -> {
                                if (!userInfo.isExists()) {
                                    log.warn("=== USER NOT FOUND === Email: {} ===", request.getUserEmail());
                                    return Mono.error(new UserNotFoundException(request.getUserEmail()));
                                }

                                log.info("=== USER VALIDATED === Email: {}, UserID: {}, Name: {} {} ===",
                                        request.getUserEmail(), userInfo.getUserId(),
                                        userInfo.getFirstName(), userInfo.getLastName());

                                return createAdWithSpecificationAndImages(request, userInfo, imageList)
                                        .flatMap(ad -> {
                                            long duration = System.currentTimeMillis() - startTime;
                                            log.info("=== AD AND IMAGES CREATED === ID: {}, Duration: {}ms ===",
                                                    ad.getId(), duration);
                                            return this.mapToResponse(ad);
                                        });
                            });
                })
                .doOnSuccess(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== CREATE BOAT AD WITH IMAGES SUCCESS === ID: {}, Category: {}, User: {}, Duration: {}ms ===",
                            response.getId(), response.getCategory(), response.getUserEmail(), duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== CREATE BOAT AD WITH IMAGES FAILED === Category: {}, User: {}, Duration: {}ms, Error: {} ===",
                            request.getCategory(), request.getUserEmail(), duration, error.getMessage());
                });
    }

    // ===========================
    // IMAGE VALIDATION
    // ===========================
    private Mono<List<ValidatedImageData>> validateImagesFirst(Flux<FilePart> images) {
        log.debug("=== VALIDATING IMAGES ===");

        return images.flatMap(this::validateAndProcessImage)
                .collectList()
                .doOnNext(imageList -> log.info("=== IMAGES VALIDATION COMPLETE === Count: {} ===", imageList.size()));
    }

    private Mono<ValidatedImageData> validateAndProcessImage(FilePart filePart) {
        String originalFileName = filePart.filename();
        String contentType = filePart.headers().getContentType() != null ?
                filePart.headers().getContentType().toString() : "";

        if (!imageConversionService.isFormatSupported(contentType)) {
            return Mono.error(new InvalidFieldValueException("image",
                    "Unsupported format for '" + originalFileName + "'. Supported: JPG, PNG, WebP, HEIC"));
        }

        return filePart.content()
                .collectList()
                .map(this::collectDataBufferBytes)
                .flatMap(bytes -> {
                    if (bytes.length > MAX_FILE_SIZE) {
                        return Mono.error(new InvalidFieldValueException("image",
                                "File too large: " + originalFileName));
                    }

                    return imageConversionService.convertToWebP(bytes, originalFileName, contentType)
                            .map(converted -> ValidatedImageData.builder()
                                    .originalFileName(originalFileName)
                                    .contentType("image/webp")
                                    .bytes(converted.getConvertedBytes())
                                    .width(converted.getWidth())
                                    .height(converted.getHeight())
                                    .build());
                });
    }


    // ===========================
    // AD UPDATING WITH IMAGES
    // ===========================

    @Transactional
    public Mono<BoatAdResponse> updateBoatAdWithImages(
            Long adId,
            BoatAdRequest request,
            Flux<FilePart> newImages,
            List<Long> imagesToDelete,
            String token) {

        long startTime = System.currentTimeMillis();
        log.info("=== UPDATE BOAT AD WITH IMAGES START === AdID: {}, Category: {}, User: {} ===",
                adId, request.getCategory(), request.getUserEmail());

        return validateUser(request.getUserEmail(), token)
                .flatMap(userInfo -> {
                    if (!userInfo.isExists()) {
                        return Mono.error(new UserNotFoundException(request.getUserEmail()));
                    }

                    return adRepository.findById(adId)
                            .switchIfEmpty(Mono.error(new AdNotFoundException(adId)))
                            .filter(ad -> userInfo.getUserId().equals(ad.getUserId()))
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Advertisement not owned by user")))
                            .flatMap(existingAd ->
                                    validateCategorySpecificFieldsAsync(request)
                                            .then(processAdUpdateWithImages(adId, existingAd, request, userInfo, newImages, imagesToDelete))
                            );
                })
                .doOnSuccess(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== UPDATE BOAT AD WITH IMAGES SUCCESS === AdID: {}, Duration: {}ms ===",
                            adId, duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== UPDATE BOAT AD WITH IMAGES FAILED === AdID: {}, Duration: {}ms, Error: {} ===",
                            adId, duration, error.getMessage(), error);
                });
    }

    private Mono<BoatAdResponse> processAdUpdateWithImages(
            Long adId,
            Ad existingAd,
            BoatAdRequest request,
            UserValidationResponse userInfo,
            Flux<FilePart> newImages,
            List<Long> imagesToDelete) {

        // Add this logging to see what's being passed
        log.info("=== PROCESS AD UPDATE WITH IMAGES === AdID: {}, " +
                        "ImagesToDelete: {}, HasNewImages: {} ===",
                adId,
                imagesToDelete != null ? imagesToDelete.toString() : "null",
                newImages != null ? "yes" : "no");

        // Update the main ad entity
        Ad updatedAd = Ad.builder()
                .id(existingAd.getId())
//                .title(request.getTitle())
                .description(request.getDescription())
//                .quickDescription(request.getQuickDescription())
                .category(request.getCategory().name())
                .priceAmount(request.getPrice() != null ? request.getPrice().getAmount() : null)
                .priceType(request.getPrice() != null ? request.getPrice().getType().name() : null)
                .includingVat(request.getPrice() != null ? request.getPrice().getIncludingVat() : null)
                .location(request.getLocation())
                .adType(request.getAdType().name())
                .userEmail(existingAd.getUserEmail())
                .userId(existingAd.getUserId())
                .userFirstName(existingAd.getUserFirstName())
                .userLastName(existingAd.getUserLastName())
                .createdAt(existingAd.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .active(existingAd.getActive())
                .viewsCount(existingAd.getViewsCount())
                .featured(existingAd.getFeatured())
                .build();

        return adRepository.save(updatedAd)
                .flatMap(savedAd -> {
                    log.info("=== AD UPDATED === ID: {} ===", savedAd.getId());

                    // Process operations in sequence to avoid conflicts
                    log.info("=== STARTING IMAGE OPERATIONS === AdID: {}, DeleteCount: {}, AddNewImages: {} ===",
                            adId,
                            imagesToDelete != null ? imagesToDelete.size() : 0,
                            newImages != null ? "yes" : "no");

                    return deleteSpecifiedImages(adId, userInfo.getUserId(), imagesToDelete)
                            .doOnSuccess(v -> log.info("=== DELETE IMAGES COMPLETED === AdID: {} ===", adId))
                            .doOnError(e -> log.error("=== DELETE IMAGES FAILED === AdID: {}, Error: {} ===",
                                    adId, e.getMessage()))
                            .then(validateAndAddNewImages(adId, userInfo.getUserId(), newImages))
                            .doOnSuccess(v -> log.info("=== ADD NEW IMAGES COMPLETED === AdID: {} ===", adId))
                            .doOnError(e -> log.error("=== ADD NEW IMAGES FAILED === AdID: {}, Error: {} ===",
                                    adId, e.getMessage()))
                            .then(updateCategorySpecification(savedAd, request))
                            .doOnSuccess(v -> log.info("=== UPDATE SPECIFICATION COMPLETED === AdID: {} ===", adId))
                            .doOnError(e -> log.error("=== UPDATE SPECIFICATION FAILED === AdID: {}, Error: {} ===",
                                    adId, e.getMessage()))
                            .thenReturn(savedAd);
                })
                .flatMap(this::mapToResponse);
    }

    // Enhanced deleteSpecifiedImages with more debugging
    private Mono<Void> deleteSpecifiedImages(Long adId, String userId, List<Long> imagesToDelete) {
        if (imagesToDelete == null || imagesToDelete.isEmpty()) {
            log.info("=== NO IMAGES TO DELETE === AdID: {}, List is null or empty ===", adId);
            return Mono.empty();
        }

        log.info("=== DELETING SPECIFIED IMAGES === AdID: {}, Count: {}, UserID: {} ===",
                adId, imagesToDelete.size(), userId);

        return Flux.fromIterable(imagesToDelete)
                .flatMap(imageId -> {
                    log.info("=== PROCESSING DELETE FOR IMAGE === ImageID: {}, AdID: {}, UserID: {} ===",
                            imageId, adId, userId);

                    // Get the image and verify ownership without using the JOIN query
                    return adImageRepository.findById(imageId)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                    "Image " + imageId + " not found")))
                            .flatMap(image -> {
                                log.info("=== IMAGE FOUND === ID: {}, AdID: {}, UploadedBy: {}, S3Key: {} ===",
                                        imageId, image.getAdId(), image.getUploadedBy(), image.getS3Key());

                                // Check if image belongs to the correct ad
                                if (!image.getAdId().equals(adId)) {
                                    log.error("=== IMAGE BELONGS TO DIFFERENT AD === ImageID: {}, " +
                                                    "Expected AdID: {}, Actual AdID: {} ===",
                                            imageId, adId, image.getAdId());
                                    return Mono.error(new IllegalArgumentException(
                                            "Image " + imageId + " does not belong to ad " + adId));
                                }

                                // Now check if the ad belongs to the user (separate query)
                                return adRepository.findById(adId)
                                        .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                                "Ad " + adId + " not found")))
                                        .flatMap(ad -> {
                                            if (!userId.equals(ad.getUserId())) {
                                                log.error("=== AD NOT OWNED BY USER === AdID: {}, " +
                                                                "Expected UserID: {}, Actual UserID: {} ===",
                                                        adId, userId, ad.getUserId());
                                                return Mono.error(new IllegalArgumentException(
                                                        "Ad " + adId + " not owned by user"));
                                            }

                                            log.info("=== OWNERSHIP VERIFIED === ImageID: {}, AdID: {}, UserID: {} ===",
                                                    imageId, adId, userId);

                                            // Delete from S3 first, then from database
                                            return deleteFromS3(image.getS3Key())
                                                    .doOnSuccess(v -> log.info("=== S3 DELETE SUCCESS === ImageID: {}, S3Key: {} ===",
                                                            imageId, image.getS3Key()))
                                                    .onErrorResume(e -> {
                                                        log.warn("=== S3 DELETE FAILED (CONTINUING) === ImageID: {}, Error: {} ===",
                                                                imageId, e.getMessage());
                                                        return Mono.empty();
                                                    })
                                                    .then(adImageRepository.deleteById(imageId))
                                                    .doOnSuccess(v -> log.info("=== DB DELETE SUCCESS === ImageID: {} ===", imageId))
                                                    .doOnError(e -> log.error("=== DB DELETE FAILED === ImageID: {}, Error: {} ===",
                                                            imageId, e.getMessage()));
                                        });
                            })
                            .doOnError(e -> log.error("=== ERROR PROCESSING IMAGE === ImageID: {}, Error: {} ===",
                                    imageId, e.getMessage()));
                })
                .then()
                .then(reorderRemainingImages(adId))
                .doOnSuccess(v -> log.info("=== ALL SPECIFIED IMAGES DELETED === AdID: {}, Count: {} ===",
                        adId, imagesToDelete.size()))
                .doOnError(e -> log.error("=== DELETE SPECIFIED IMAGES FAILED === AdID: {}, Error: {} ===",
                        adId, e.getMessage()));
    }

    private Mono<Void> validateAndAddNewImages(Long adId, String userId, Flux<FilePart> newImages) {
        log.info("=== ADDING NEW IMAGES === AdID: {} ===", adId);

        return validateImagesFirst(newImages)
                .flatMap(imageList -> {
                    if (imageList.isEmpty()) {
                        return Mono.empty();
                    }

                    // Атомарна проверка - вземи текущия брой и провери
                    return adImageRepository.countByAdId(adId)
                            .flatMap(existingCount -> {
                                int totalCount = existingCount.intValue() + imageList.size();
                                if (totalCount > MAX_IMAGES_ALLOWED) {
                                    return Mono.error(new InvalidFieldValueException("images",
                                            "Total images would exceed maximum of " + MAX_IMAGES_ALLOWED +
                                                    " (existing: " + existingCount + ", adding: " + imageList.size() + ")"));
                                }

                                // Допълнителна проверка дали потребителят има права
                                return validateUserOwnsAdImages(adId, userId)
                                        .then(uploadNewImages(adId, userId, imageList));
                            });
                });
    }

    private Mono<Void> validateUserOwnsAdImages(Long adId, String userId) {
        return adRepository.findById(adId)
                .filter(ad -> userId.equals(ad.getUserId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User does not own this ad")))
                .then();
    }

    private Mono<Void> uploadNewImages(Long adId, String userId, List<ValidatedImageData> images) {
        return adImageRepository.findMaxDisplayOrderByAdId(adId)
                .defaultIfEmpty(0)
                .flatMap(maxOrder -> {
                    return Flux.fromIterable(images)
                            .index()
                            .flatMap(tuple -> {
                                int index = tuple.getT1().intValue();
                                ValidatedImageData imageData = tuple.getT2();
                                int newDisplayOrder = maxOrder + index + 1;

                                return uploadImageToS3AndSave(adId, userId, imageData, newDisplayOrder);
                            })
                            .then();
                });
    }

    private Mono<Void> reorderRemainingImages(Long adId) {
        log.debug("=== REORDERING REMAINING IMAGES === AdID: {} ===", adId);

        return adImageRepository.findByAdIdOrderByDisplayOrderAsc(adId)
                .collectList()
                .flatMap(images -> {
                    if (images.isEmpty()) {
                        log.debug("=== NO IMAGES TO REORDER === AdID: {} ===", adId);
                        return Mono.empty();
                    }

                    log.info("=== REORDERING IMAGES === AdID: {}, ImageCount: {} ===", adId, images.size());

                    return Flux.fromIterable(images)
                            .index()
                            .flatMap(tuple -> {
                                int index = tuple.getT1().intValue();
                                AdImage image = tuple.getT2();
                                int temporaryOrder = -(index + 1000);

                                log.debug("=== SETTING TEMPORARY ORDER === ImageID: {}, TempOrder: {} ===",
                                        image.getId(), temporaryOrder);

                                return adImageRepository.updateDisplayOrder(image.getId(), temporaryOrder);
                            })
                            .then()
                            // Step 2: Now set the correct positive values (1, 2, 3, etc.)
                            .then(Flux.fromIterable(images)
                                    .index()
                                    .flatMap(tuple -> {
                                        int index = tuple.getT1().intValue();
                                        AdImage image = tuple.getT2();
                                        int finalOrder = index + 1; // 1, 2, 3, etc.

                                        log.debug("=== SETTING FINAL ORDER === ImageID: {}, FinalOrder: {} ===",
                                                image.getId(), finalOrder);

                                        return adImageRepository.updateDisplayOrder(image.getId(), finalOrder);
                                    })
                                    .then()
                            );
                })
                .doOnSuccess(v -> log.info("=== REORDER COMPLETED === AdID: {} ===", adId))
                .doOnError(e -> log.error("=== REORDER FAILED === AdID: {}, Error: {} ===", adId, e.getMessage()));
    }

    // ===========================
    // SPECIFICATION UPDATE METHODS
    // ===========================

    private Mono<Void> updateCategorySpecification(Ad ad, BoatAdRequest request) {
        // Delete existing specification first, then create new one
        return deleteCategorySpecification(ad.getId(), MainBoatCategory.valueOf(ad.getCategory()))
                .then(createCategorySpecification(ad, request));
    }

    private Mono<Void> deleteCategorySpecification(Long adId, MainBoatCategory category) {
        switch (category) {
            case BOATS_AND_YACHTS:
                return deleteBoatSpecification(adId);
            case JET_SKIS:
                return jetSkiSpecRepository.deleteByAdId(adId);
            case TRAILERS:
                return trailerSpecRepository.deleteByAdId(adId);
            case ENGINES:
                return engineSpecRepository.deleteByAdId(adId);
            case MARINE_ELECTRONICS:
                return marineElectronicsSpecRepository.deleteByAdId(adId);
            case FISHING:
                return fishingSpecRepository.deleteByAdId(adId);
            case PARTS:
                return partsSpecRepository.deleteByAdId(adId);
            case SERVICES:
                return servicesSpecRepository.deleteByAdId(adId);
            case WATER_SPORTS:                                         // NEW
                return waterSportsSpecRepository.deleteByAdId(adId);   // NEW
            case MARINE_ACCESSORIES:                                        // NEW
                return marineAccessoriesSpecRepository.deleteByAdId(adId);  // NEW
            case RENTALS:                                              // NEW
                return rentalsSpecRepository.deleteByAdId(adId);       // NEW
            default:
                return Mono.empty();
        }
    }

    private Mono<Void> createWaterSportsSpecification(Long adId, WaterSportsSpecificationDto spec) {
        WaterSportsSpecification waterSportsSpec = WaterSportsSpecification.builder()
                .adId(adId)
                .waterSportsType(spec.getType().name())
                .brand(spec.getBrand())
                .condition(spec.getCondition().name())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return waterSportsSpecRepository.save(waterSportsSpec).then();
    }

    private Mono<Void> createMarineAccessoriesSpecification(Long adId, MarineAccessoriesSpecificationDto spec) {
        MarineAccessoriesSpecification accessoriesSpec = MarineAccessoriesSpecification.builder()
                .adId(adId)
                .accessoryType(spec.getType().name())
                .brand(spec.getBrand())
                .condition(spec.getCondition().name())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return marineAccessoriesSpecRepository.save(accessoriesSpec).then();
    }

    private Mono<Void> createRentalsSpecification(Long adId, RentalsSpecificationDto spec) {
        RentalsSpecification rentalsSpec = RentalsSpecification.builder()
                .adId(adId)
                .rentalType(spec.getRentalType().name())
                .licenseRequired(spec.getLicenseRequired())
                .managementType(spec.getManagementType().name())
                .numberOfPeople(spec.getNumberOfPeople())
                .serviceType(spec.getServiceType().name())
                .companyName(spec.getCompanyName())
                .description(spec.getDescription())
                .contactPhone(spec.getContactPhone())
                .contactEmail(spec.getContactEmail())
                .address(spec.getAddress())
                .website(spec.getWebsite())
                .maxPrice(spec.getMaxPrice())
                .priceSpecification(spec.getPriceSpecification())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return rentalsSpecRepository.save(rentalsSpec).then();
    }

    private Mono<Void> deleteBoatSpecification(Long adId) {
        return boatSpecRepository.findByAdId(adId)
                .flatMap(boatSpec -> {
                    // Delete related features first
                    Mono<Void> deleteInterior = interiorFeatureRepository.deleteByBoatSpecId(boatSpec.getId());
                    Mono<Void> deleteExterior = exteriorFeatureRepository.deleteByBoatSpecId(boatSpec.getId());
                    Mono<Void> deleteEquipment = equipmentRepository.deleteByBoatSpecId(boatSpec.getId());

                    return Mono.when(deleteInterior, deleteExterior, deleteEquipment)
                            .then(boatSpecRepository.deleteByAdId(adId));
                })
                .switchIfEmpty(Mono.empty());
    }

    // ===========================
    // HELPER METHODS FROM IMAGE SERVICE
    // ===========================

    private Mono<String> deleteFromS3(String s3Key) {
        return Mono.fromCallable(() -> {
            try {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build();

                s3Client.deleteObject(deleteObjectRequest);
                log.debug("=== S3 DELETE SUCCESS === S3Key: '{}' ===", s3Key);
                return s3Key;
            } catch (Exception e) {
                log.error("=== S3 DELETE FAILED === S3Key: '{}', Error: {} ===", s3Key, e.getMessage());
                throw new RuntimeException("Failed to delete from S3: " + e.getMessage(), e);
            }
        }).onErrorResume(e -> {
            log.warn("S3 deletion failed but continuing: {}", e.getMessage());
            return Mono.empty(); // Продължаваме дори ако S3 deletion fail-не
        });
    }

    // ===========================
    // AD EDITING WITHOUT IMAGES (Optional)
    // ===========================

//    @Transactional
//    public Mono<BoatAdResponse> updateBoatAd(Long adId, BoatAdRequest request, String token) {
//        return updateBoatAdWithImages(adId, request, null, null, token);
//    }

    // ===========================
    // AD CREATION WITH IMAGES
    // ===========================
    private Mono<Ad> createAdWithSpecificationAndImages(BoatAdRequest request, UserValidationResponse userInfo,
                                                        List<ValidatedImageData> images) {
        // Create main ad
        Ad ad = Ad.builder()
//                .title(request.getTitle())
                .description(request.getDescription())
//                .quickDescription(request.getQuickDescription())
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
                .contactPersonName(request.getContactPersonName())
                .contactPhone(request.getContactPhone())
                .createdAt(LocalDateTime.now())
                .active(true)
                .viewsCount(0)
                .featured(false)
                .build();

        return adRepository.save(ad)
                .flatMap(savedAd -> {
                    log.info("=== AD SAVED === ID: {} ===", savedAd.getId());

                    // Create specification and upload images in parallel
                    Mono<Void> specMono = createCategorySpecification(savedAd, request);
                    Mono<Void> imagesMono = uploadAndSaveImages(savedAd.getId(), userInfo.getUserId(), images);

                    return Mono.when(specMono, imagesMono)
                            .thenReturn(savedAd);
                })
                .onErrorMap(error -> {
                    log.error("=== AD CREATION FAILED === Error: {} ===", error.getMessage());
                    // If anything fails, the transaction will rollback
                    return new RuntimeException("Failed to create ad with images: " + error.getMessage(), error);
                });
    }

    private Mono<Void> uploadAndSaveImages(Long adId, String userId, List<ValidatedImageData> images) {
        log.info("=== UPLOADING IMAGES === AdID: {}, Count: {} ===", adId, images.size());

        return Flux.fromIterable(images)
                .index()
                .flatMap(tuple -> {
                    int index = tuple.getT1().intValue();
                    ValidatedImageData imageData = tuple.getT2();

                    return uploadImageToS3AndSave(adId, userId, imageData, index);
                })
                .then()
                .doOnSuccess(result -> log.info("=== ALL IMAGES UPLOADED === AdID: {} ===", adId));
    }

    private Mono<Void> uploadImageToS3AndSave(Long adId, String userId, ValidatedImageData imageData, int displayOrder) {
        String s3Key = generateS3Key(adId, imageData.getOriginalFileName());
        String fileName = generateFileName(imageData.getOriginalFileName());

        log.debug("=== UPLOADING WEBP TO S3 === S3Key: '{}', Size: {} bytes ===",
                s3Key, imageData.getBytes().length);

        return uploadToS3(s3Key, imageData.getBytes(), OUTPUT_CONTENT_TYPE)  // Always upload as WebP
                .flatMap(s3Url -> {
                    // Save image record
                    AdImage adImage = AdImage.builder()
                            .adId(adId)
                            .fileName(fileName)
                            .originalFileName(imageData.getOriginalFileName())
                            .s3Key(s3Key)
                            .s3Url(s3Url)
                            .contentType(OUTPUT_CONTENT_TYPE)  // Always WebP
                            .fileSize((long) imageData.getBytes().length)
                            .displayOrder(displayOrder)
                            .width(imageData.getWidth())
                            .height(imageData.getHeight())
                            .uploadedAt(LocalDateTime.now())
                            .uploadedBy(userId)
                            .active(true)
                            .build();

                    return adImageRepository.save(adImage).then();
                });
    }

    // ===========================
    // IMAGE HELPER METHODS
    // ===========================
    private byte[] collectDataBufferBytes(List<DataBuffer> dataBuffers) {
        int totalSize = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
        byte[] bytes = new byte[totalSize];
        int currentIndex = 0;

        for (DataBuffer dataBuffer : dataBuffers) {
            int readableByteCount = dataBuffer.readableByteCount();
            dataBuffer.read(bytes, currentIndex, readableByteCount);
            currentIndex += readableByteCount;
        }

        return bytes;
    }

    // ===========================
    // UPDATED S3 UPLOAD WITH WEBP
    // ===========================
    private String generateS3Key(Long adId, String originalFileName) {
        // Always use .webp extension since we convert everything
        String baseName = getFileBaseName(originalFileName);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("ads/%d/images/%s_%s.webp", adId, baseName, uniqueId);
    }

    private String generateFileName(String originalFileName) {
        // Always use .webp extension
        String baseName = getFileBaseName(originalFileName);
        return baseName + "_" + UUID.randomUUID().toString() + ".webp";
    }

    private String getFileBaseName(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName != null ? fileName : "image";
    }

    private Mono<String> uploadToS3(String s3Key, byte[] bytes, String contentType) {
        return Mono.fromCallable(() -> {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            String s3Url = String.format("%s/%s", s3BaseUrl, s3Key);
            log.debug("=== S3 UPLOAD SUCCESS === URL: '{}' ===", s3Url);
            return s3Url;
        });
    }

    private Mono<Void> validateCategorySpecificFieldsAsync(BoatAdRequest request) {
        log.debug("=== VALIDATING CATEGORY FIELDS ASYNC === Category: {} ===", request.getCategory());

        switch (request.getCategory()) {
            case BOATS_AND_YACHTS:
                return validateBoatSpecificationAsync(request.getBoatSpec());
            case JET_SKIS:
                return validateJetSkiSpecificationAsync(request.getJetSkiSpec());
            case ENGINES:
                return validateEngineSpecificationAsync(request.getEngineSpec());
            case MARINE_ELECTRONICS:
                return validateMarineElectronicsSpecificationAsync(request.getMarineElectronicsSpec());
            case TRAILERS:
                return validateTrailerSpecificationAsync(request.getTrailerSpec());
            case FISHING:
                return validateFishingSpecificationAsync(request.getFishingSpec());
            case PARTS:
                return validatePartsSpecificationAsync(request.getPartsSpec());
            case SERVICES:
                return validateServicesSpecificationAsync(request.getServicesSpec());
            case WATER_SPORTS:                                                              // NEW
                return validateWaterSportsSpecificationAsync(request.getWaterSportsSpec()); // NEW
            case MARINE_ACCESSORIES:                                                             // NEW
                return validateMarineAccessoriesSpecificationAsync(request.getMarineAccessoriesSpec()); // NEW
            case RENTALS:                                                               // NEW
                return validateRentalsSpecificationAsync(request.getRentalsSpec());     // NEW
            default:
                log.error("=== UNSUPPORTED CATEGORY === Category: {} ===", request.getCategory());
                return Mono.error(new CategoryMismatchException(request.getCategory().name(), "UNSUPPORTED"));
        }
    }

    private Mono<Void> validateWaterSportsSpecificationAsync(WaterSportsSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("waterSportsSpec", "WATER_SPORTS"));
        }
        if (spec.getType() == null) {
            return Mono.error(new MandatoryFieldMissingException("type", "WATER_SPORTS"));
        }
        if (spec.getCondition() == null) {
            return Mono.error(new MandatoryFieldMissingException("condition", "WATER_SPORTS"));
        }

        if (spec.getBrand() != null && !spec.getBrand().trim().isEmpty()) {
            return brandService.validateBrand(spec.getBrand(), "WATER_SPORTS")
                    .filter(Boolean::booleanValue)
                    .switchIfEmpty(Mono.error(new InvalidFieldValueException("brand",
                            "Brand '" + spec.getBrand() + "' is not valid for water sports equipment")))
                    .then();
        }
        return Mono.empty();
    }

    private Mono<Void> validateMarineAccessoriesSpecificationAsync(MarineAccessoriesSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("marineAccessoriesSpec", "MARINE_ACCESSORIES"));
        }
        if (spec.getType() == null) {
            return Mono.error(new MandatoryFieldMissingException("type", "MARINE_ACCESSORIES"));
        }
        if (spec.getCondition() == null) {
            return Mono.error(new MandatoryFieldMissingException("condition", "MARINE_ACCESSORIES"));
        }

        if (spec.getBrand() != null && !spec.getBrand().trim().isEmpty()) {
            return brandService.validateBrand(spec.getBrand(), "MARINE_ACCESSORIES")
                    .filter(Boolean::booleanValue)
                    .switchIfEmpty(Mono.error(new InvalidFieldValueException("brand",
                            "Brand '" + spec.getBrand() + "' is not valid for marine accessories")))
                    .then();
        }
        return Mono.empty();
    }

    private Mono<Void> validateRentalsSpecificationAsync(RentalsSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("rentalsSpec", "RENTALS"));
        }
        if (spec.getRentalType() == null) {
            return Mono.error(new MandatoryFieldMissingException("rentalType", "RENTALS"));
        }
        if (spec.getLicenseRequired() == null) {
            return Mono.error(new MandatoryFieldMissingException("licenseRequired", "RENTALS"));
        }
        if (spec.getManagementType() == null) {
            return Mono.error(new MandatoryFieldMissingException("managementType", "RENTALS"));
        }
        if (spec.getNumberOfPeople() == null) {
            return Mono.error(new MandatoryFieldMissingException("numberOfPeople", "RENTALS"));
        }
        if (spec.getServiceType() == null) {
            return Mono.error(new MandatoryFieldMissingException("serviceType", "RENTALS"));
        }
        if (spec.getCompanyName() == null || spec.getCompanyName().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("companyName", "RENTALS"));
        }
        if (spec.getContactPhone() == null || spec.getContactPhone().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("contactPhone", "RENTALS"));
        }
        if (spec.getMaxPrice() == null) {
            return Mono.error(new MandatoryFieldMissingException("maxPrice", "RENTALS"));
        }
        if (spec.getPriceSpecification() == null || spec.getPriceSpecification().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("priceSpecification", "RENTALS"));
        }
        return Mono.empty();
    }

    // ===========================
    // VALIDATION METHODS
    // ===========================
    private Mono<Void> validateBoatSpecificationAsync(BoatSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("boatSpec", "BOATS_AND_YACHTS"));
        }
        if (spec.getType() == null) {
            return Mono.error(new MandatoryFieldMissingException("type", "BOATS_AND_YACHTS"));
        }
        if (spec.getBrand() == null || spec.getBrand().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("brand", "BOATS_AND_YACHTS"));
        }
        if (spec.getModel() == null || spec.getModel().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("model", "BOATS_AND_YACHTS"));
        }
        // NEW FIELD VALIDATION
        if (spec.getPurpose() == null) {
            return Mono.error(new MandatoryFieldMissingException("purpose", "BOATS_AND_YACHTS"));
        }
        if (spec.getEngineType() == null) {
            return Mono.error(new MandatoryFieldMissingException("engineType", "BOATS_AND_YACHTS"));
        }
        if (spec.getEngineIncluded() == null) {
            return Mono.error(new MandatoryFieldMissingException("engineIncluded", "BOATS_AND_YACHTS"));
        }
        if (spec.getHorsepower() == null) {
            return Mono.error(new MandatoryFieldMissingException("horsepower", "BOATS_AND_YACHTS"));
        }
        if (spec.getLength() == null) {
            return Mono.error(new MandatoryFieldMissingException("length", "BOATS_AND_YACHTS"));
        }
        if (spec.getWidth() == null) {
            return Mono.error(new MandatoryFieldMissingException("width", "BOATS_AND_YACHTS"));
        }
        if (spec.getMaxPeople() == null) {
            return Mono.error(new MandatoryFieldMissingException("maxPeople", "BOATS_AND_YACHTS"));
        }
        if (spec.getYear() == null) {
            return Mono.error(new MandatoryFieldMissingException("year", "BOATS_AND_YACHTS"));
        }
        if (spec.getYear() < 1900 || spec.getYear() > LocalDate.now().getYear() + 5) {
            return Mono.error(new InvalidFieldValueException("year", "Year must be between 1900 and " + (LocalDate.now().getYear() + 5)));
        }
        if (spec.getInWarranty() == null) {
            return Mono.error(new MandatoryFieldMissingException("inWarranty", "BOATS_AND_YACHTS"));
        }
        if (spec.getWeight() == null) {
            return Mono.error(new MandatoryFieldMissingException("weight", "BOATS_AND_YACHTS"));
        }
        if (spec.getFuelCapacity() == null) {
            return Mono.error(new MandatoryFieldMissingException("fuelCapacity", "BOATS_AND_YACHTS"));
        }
        if (spec.getHasWaterTank() == null) {
            return Mono.error(new MandatoryFieldMissingException("hasWaterTank", "BOATS_AND_YACHTS"));
        }
        if (spec.getNumberOfEngines() == null) {
            return Mono.error(new MandatoryFieldMissingException("numberOfEngines", "BOATS_AND_YACHTS"));
        }
        if (spec.getHasAuxiliaryEngine() == null) {
            return Mono.error(new MandatoryFieldMissingException("hasAuxiliaryEngine", "BOATS_AND_YACHTS"));
        }
        if (spec.getConsoleType() == null) {
            return Mono.error(new MandatoryFieldMissingException("consoleType", "BOATS_AND_YACHTS"));
        }
        if (spec.getFuelType() == null) {
            return Mono.error(new MandatoryFieldMissingException("fuelType", "BOATS_AND_YACHTS"));
        }
        if (spec.getMaterial() == null) {
            return Mono.error(new MandatoryFieldMissingException("material", "BOATS_AND_YACHTS"));
        }
        if (spec.getIsRegistered() == null) {
            return Mono.error(new MandatoryFieldMissingException("isRegistered", "BOATS_AND_YACHTS"));
        }
        if (spec.getCondition() == null) {
            return Mono.error(new MandatoryFieldMissingException("condition", "BOATS_AND_YACHTS"));
        }
        // NEW FIELD VALIDATION
//        if (spec.getWaterType() == null) {
//            return Mono.error(new MandatoryFieldMissingException("waterType", "BOATS_AND_YACHTS"));
//        }
//        if (spec.getEngineHours() == null) {
//            return Mono.error(new MandatoryFieldMissingException("engineHours", "BOATS_AND_YACHTS"));
//        }
        if (spec.getLocatedInBulgaria() == null) {
            return Mono.error(new MandatoryFieldMissingException("locatedInBulgaria", "BOATS_AND_YACHTS"));
        }

        String boatCategory = mapBoatTypeToCategory(spec.getType());
        return brandService.validateBrand(spec.getBrand(), boatCategory)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new InvalidFieldValueException("brand",
                        "Brand '" + spec.getBrand() + "' is not valid for category '" + boatCategory + "'")))
                .then();
    }

    private Mono<Void> validateJetSkiSpecificationAsync(JetSkiSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("jetSkiSpec", "JET_SKIS"));
        }
        if (spec.getBrand() == null || spec.getBrand().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("brand", "JET_SKIS"));
        }
        if (spec.getModel() == null || spec.getModel().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("model", "JET_SKIS"));
        }
        if (spec.getIsRegistered() == null) {
            return Mono.error(new MandatoryFieldMissingException("isRegistered", "JET_SKIS"));
        }
        if (spec.getHorsepower() == null) {
            return Mono.error(new MandatoryFieldMissingException("horsepower", "JET_SKIS"));
        }
        if (spec.getYear() == null) {
            return Mono.error(new MandatoryFieldMissingException("year", "JET_SKIS"));
        }
        if (spec.getYear() < 1900 || spec.getYear() > LocalDate.now().getYear() + 5) {
            return Mono.error(new InvalidFieldValueException("year", "Year must be between 1900 and " + (LocalDate.now().getYear() + 5)));
        }
        if (spec.getWeight() == null) {
            return Mono.error(new MandatoryFieldMissingException("weight", "JET_SKIS"));
        }
        if (spec.getFuelCapacity() == null) {
            return Mono.error(new MandatoryFieldMissingException("fuelCapacity", "JET_SKIS"));
        }
        if (spec.getOperatingHours() == null) {
            return Mono.error(new MandatoryFieldMissingException("operatingHours", "JET_SKIS"));
        }
        if (spec.getFuelType() == null) {
            return Mono.error(new MandatoryFieldMissingException("fuelType", "JET_SKIS"));
        }
        if (spec.getTrailerIncluded() == null) {
            return Mono.error(new MandatoryFieldMissingException("trailerIncluded", "JET_SKIS"));
        }
        if (spec.getInWarranty() == null) {
            return Mono.error(new MandatoryFieldMissingException("inWarranty", "JET_SKIS"));
        }
        if (spec.getCondition() == null) {
            return Mono.error(new MandatoryFieldMissingException("condition", "JET_SKIS"));
        }

        return brandService.validateBrand(spec.getBrand(), "MOTOR_BOATS")
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new InvalidFieldValueException("brand",
                        "Brand '" + spec.getBrand() + "' is not valid for jet skis")))
                .then();
    }

    private Mono<Void> validateEngineSpecificationAsync(EngineSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("engineSpec", "ENGINES"));
        }
        if (spec.getBrand() == null || spec.getBrand().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("brand", "ENGINES"));
        }
        if (spec.getEngineType() == null) {
            return Mono.error(new MandatoryFieldMissingException("engineType", "ENGINES"));
        }
        if (spec.getStrokeType() == null) {
            return Mono.error(new MandatoryFieldMissingException("strokeType", "ENGINES"));
        }
        if (spec.getInWarranty() == null) {
            return Mono.error(new MandatoryFieldMissingException("inWarranty", "ENGINES"));
        }
        if (spec.getHorsepower() == null) {
            return Mono.error(new MandatoryFieldMissingException("horsepower", "ENGINES"));
        }
        if (spec.getOperatingHours() == null) {
            return Mono.error(new MandatoryFieldMissingException("operatingHours", "ENGINES"));
        }
        if (spec.getYear() == null) {
            return Mono.error(new MandatoryFieldMissingException("year", "ENGINES"));
        }
        if (spec.getYear() < 1900 || spec.getYear() > LocalDate.now().getYear() + 5) {
            return Mono.error(new InvalidFieldValueException("year", "Year must be between 1900 and " + (LocalDate.now().getYear() + 5)));
        }
        if (spec.getFuelCapacity() == null) {
            return Mono.error(new MandatoryFieldMissingException("fuelCapacity", "ENGINES"));
        }
        if (spec.getIgnitionType() == null) {
            return Mono.error(new MandatoryFieldMissingException("ignitionType", "ENGINES"));
        }
        if (spec.getControlType() == null) {
            return Mono.error(new MandatoryFieldMissingException("controlType", "ENGINES"));
        }
        if (spec.getShaftLength() == null) {
            return Mono.error(new MandatoryFieldMissingException("shaftLength", "ENGINES"));
        }
        if (spec.getFuelType() == null) {
            return Mono.error(new MandatoryFieldMissingException("fuelType", "ENGINES"));
        }
        if (spec.getEngineSystemType() == null) {
            return Mono.error(new MandatoryFieldMissingException("engineSystemType", "ENGINES"));
        }
        if (spec.getCondition() == null) {
            return Mono.error(new MandatoryFieldMissingException("condition", "ENGINES"));
        }
        if (spec.getColor() == null) {
            return Mono.error(new MandatoryFieldMissingException("color", "ENGINES"));
        }

        return brandService.validateBrand(spec.getBrand(), "MOTOR_BOATS")
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new InvalidFieldValueException("brand",
                        "Brand '" + spec.getBrand() + "' is not valid for engines")))
                .then();
    }

    private Mono<Void> validateMarineElectronicsSpecificationAsync(MarineElectronicsSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("marineElectronicsSpec", "MARINE_ELECTRONICS"));
        }
        if (spec.getElectronicsType() == null) {
            return Mono.error(new MandatoryFieldMissingException("electronicsType", "MARINE_ELECTRONICS"));
        }
        if (spec.getBrand() == null || spec.getBrand().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("brand", "MARINE_ELECTRONICS"));
        }
        if (spec.getCondition() == null) {
            return Mono.error(new MandatoryFieldMissingException("condition", "MARINE_ELECTRONICS"));
        }

        return brandService.validateBrand(spec.getBrand(), "MOTOR_BOATS")
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new InvalidFieldValueException("brand",
                        "Brand '" + spec.getBrand() + "' is not valid for marine electronics")))
                .then();
    }

    private Mono<Void> validateTrailerSpecificationAsync(TrailerSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("trailerSpec", "TRAILERS"));
        }
        if (spec.getTrailerType() == null) {
            return Mono.error(new MandatoryFieldMissingException("trailerType", "TRAILERS"));
        }
        if (spec.getAxleCount() == null) {
            return Mono.error(new MandatoryFieldMissingException("axleCount", "TRAILERS"));
        }
        if (spec.getIsRegistered() == null) {
            return Mono.error(new MandatoryFieldMissingException("isRegistered", "TRAILERS"));
        }
        if (spec.getLoadCapacity() == null) {
            return Mono.error(new MandatoryFieldMissingException("loadCapacity", "TRAILERS"));
        }
        if (spec.getLength() == null) {
            return Mono.error(new MandatoryFieldMissingException("length", "TRAILERS"));
        }
        if (spec.getWidth() == null) {
            return Mono.error(new MandatoryFieldMissingException("width", "TRAILERS"));
        }
        if (spec.getYear() == null) {
            return Mono.error(new MandatoryFieldMissingException("year", "TRAILERS"));
        }
        if (spec.getYear() < 1900 || spec.getYear() > LocalDate.now().getYear() + 5) {
            return Mono.error(new InvalidFieldValueException("year", "Year must be between 1900 and " + (LocalDate.now().getYear() + 5)));
        }
        if (spec.getInWarranty() == null) {
            return Mono.error(new MandatoryFieldMissingException("inWarranty", "TRAILERS"));
        }
        if (spec.getCondition() == null) {
            return Mono.error(new MandatoryFieldMissingException("condition", "TRAILERS"));
        }

        if (spec.getBrand() != null && !spec.getBrand().trim().isEmpty()) {
            return brandService.validateBrand(spec.getBrand(), "MOTOR_BOATS")
                    .filter(Boolean::booleanValue)
                    .switchIfEmpty(Mono.error(new InvalidFieldValueException("brand",
                            "Brand '" + spec.getBrand() + "' is not valid for trailers")))
                    .then();
        }
        return Mono.empty();
    }

    private Mono<Void> validateFishingSpecificationAsync(FishingSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("fishingSpec", "FISHING"));
        }
        if (spec.getFishingType() == null) {
            return Mono.error(new MandatoryFieldMissingException("fishingType", "FISHING"));
        }
        if (spec.getFishingTechnique() == null) {
            return Mono.error(new MandatoryFieldMissingException("fishingTechnique", "FISHING"));
        }
        if (spec.getTargetFish() == null) {
            return Mono.error(new MandatoryFieldMissingException("targetFish", "FISHING"));
        }
        if (spec.getCondition() == null) {
            return Mono.error(new MandatoryFieldMissingException("condition", "FISHING"));
        }

        if (spec.getBrand() != null && !spec.getBrand().trim().isEmpty()) {
            return brandService.validateBrand(spec.getBrand(), "MOTOR_BOATS")
                    .filter(Boolean::booleanValue)
                    .switchIfEmpty(Mono.error(new InvalidFieldValueException("brand",
                            "Brand '" + spec.getBrand() + "' is not valid for fishing equipment")))
                    .then();
        }
        return Mono.empty();
    }

    private Mono<Void> validatePartsSpecificationAsync(PartsSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("partsSpec", "PARTS"));
        }
        if (spec.getPartType() == null) {
            return Mono.error(new MandatoryFieldMissingException("partType", "PARTS"));
        }
        if (spec.getCondition() == null) {
            return Mono.error(new MandatoryFieldMissingException("condition", "PARTS"));
        }
        return Mono.empty();
    }

    private Mono<Void> validateServicesSpecificationAsync(ServicesSpecificationDto spec) {
        if (spec == null) {
            return Mono.error(new MandatoryFieldMissingException("servicesSpec", "SERVICES"));
        }
        if (spec.getServiceType() == null) {
            return Mono.error(new MandatoryFieldMissingException("serviceType", "SERVICES"));
        }
        if (spec.getCompanyName() == null || spec.getCompanyName().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("companyName", "SERVICES"));
        }
        if (spec.getContactPhone() == null || spec.getContactPhone().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("contactPhone", "SERVICES"));
        }
        if (spec.getContactEmail() == null || spec.getContactEmail().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("contactEmail", "SERVICES"));
        }
        if (spec.getAddress() == null || spec.getAddress().trim().isEmpty()) {
            return Mono.error(new MandatoryFieldMissingException("address", "SERVICES"));
        }
        return Mono.empty();
    }

    private String mapBoatTypeToCategory(BoatSpecificationDto.BoatType boatType) {
        switch (boatType) {
            case MOTOR_BOAT:
            case MOTOR_YACHT:
            case INFLATABLE_BOAT:
            case SHIP:
            case PONTOON:
                return "MOTOR_BOATS";
            case SAILING_BOAT:
            case SAILING_YACHT:
                return "SAILBOATS";
            case CANOE:
                return "KAYAKS";
            case ALL:
            default:
                return "MOTOR_BOATS";
        }
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
            case WATER_SPORTS:                                                              // NEW
                return createWaterSportsSpecification(ad.getId(), request.getWaterSportsSpec()); // NEW
            case MARINE_ACCESSORIES:                                                             // NEW
                return createMarineAccessoriesSpecification(ad.getId(), request.getMarineAccessoriesSpec()); // NEW
            case RENTALS:                                                               // NEW
                return createRentalsSpecification(ad.getId(), request.getRentalsSpec()); // NEW
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
                .boatPurpose(spec.getPurpose().name())
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
//                .waterType(spec.getWaterType().name())
//                .engineHours(spec.getEngineHours())
                .locatedInBulgaria(spec.getLocatedInBulgaria())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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

    public Mono<BoatAdResponse> getAdById(Long id) {
        return adRepository.findById(id)
                .switchIfEmpty(Mono.error(new AdNotFoundException(id)))
                .flatMap(ad -> adRepository.incrementViewsCount(id)
                        .thenReturn(ad))
                .flatMap(this::mapToResponse);
    }

    public Mono<BoatAdResponse> mapToResponse(Ad ad) {
        BoatAdResponse.BoatAdResponseBuilder responseBuilder = BoatAdResponse.builder()
                .id(ad.getId())
//                .title(ad.getTitle())
                .description(ad.getDescription())
//                .quickDescription(ad.getQuickDescription())
                .category(MainBoatCategory.valueOf(ad.getCategory()))
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
                .featured(ad.getFeatured())
                .contactPersonName(ad.getContactPersonName())
                .contactPhone(ad.getContactPhone());

        // Load images for the ad
        Mono<List<ImageUploadResponse>> imagesMono = adImageRepository.findByAdIdOrderByDisplayOrder(ad.getId())
                .map(image -> ImageUploadResponse.builder()
                        .id(image.getId())
                        .fileName(image.getFileName())
                        .originalFileName(image.getOriginalFileName())
                        .url(image.getS3Url())
                        .contentType(image.getContentType())
                        .fileSize(image.getFileSize())
                        .width(image.getWidth())
                        .height(image.getHeight())
                        .displayOrder(image.getDisplayOrder())
                        .uploadedAt(image.getUploadedAt())
                        .build())
                .collectList();

        // Load category-specific specifications
        Mono<Object> specMono = loadSpecificationForResponse(ad.getId(), MainBoatCategory.valueOf(ad.getCategory()));

        return Mono.zip(imagesMono, specMono)
                .map(tuple -> {
                    List<ImageUploadResponse> images = tuple.getT1();
                    Object spec = tuple.getT2();

                    responseBuilder.images(images);

                    switch (MainBoatCategory.valueOf(ad.getCategory())) {
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
                        case WATER_SPORTS:                                                                  // NEW
                            responseBuilder.waterSportsSpec((WaterSportsSpecificationResponse) spec);       // NEW
                            break;                                                                          // NEW
                        case MARINE_ACCESSORIES:                                                                 // NEW
                            responseBuilder.marineAccessoriesSpec((MarineAccessoriesSpecificationResponse) spec); // NEW
                            break;                                                                               // NEW
                        case RENTALS:                                                                       // NEW
                            responseBuilder.rentalsSpec((RentalsSpecificationResponse) spec);               // NEW
                            break;                                                                          // NEW
                    }
                    return responseBuilder.build();
                })
                .defaultIfEmpty(responseBuilder.build());
    }

    // Replace your existing loadSpecificationForResponse method:
    private Mono<Object> loadSpecificationForResponse(Long adId, MainBoatCategory category) {
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
            case WATER_SPORTS:                                                              // NEW
                return waterSportsSpecRepository.findByAdId(adId)                           // NEW
                        .map(this::mapWaterSportsSpecToResponse)                            // NEW
                        .cast(Object.class);                                                // NEW
            case MARINE_ACCESSORIES:                                                             // NEW
                return marineAccessoriesSpecRepository.findByAdId(adId)                          // NEW
                        .map(this::mapMarineAccessoriesSpecToResponse)                           // NEW
                        .cast(Object.class);                                                     // NEW
            case RENTALS:                                                               // NEW
                return rentalsSpecRepository.findByAdId(adId)                           // NEW
                        .map(this::mapRentalsSpecToResponse)                            // NEW
                        .cast(Object.class);                                            // NEW
            default:
                return Mono.empty();
        }
    }

    private WaterSportsSpecificationResponse mapWaterSportsSpecToResponse(WaterSportsSpecification spec) {
        return WaterSportsSpecificationResponse.builder()
                .waterSportsType(WaterSportsSpecificationDto.WaterSportsType.valueOf(spec.getWaterSportsType()))
                .brand(spec.getBrand())
                .condition(ItemCondition.valueOf(spec.getCondition()))
                .build();
    }

    private MarineAccessoriesSpecificationResponse mapMarineAccessoriesSpecToResponse(MarineAccessoriesSpecification spec) {
        return MarineAccessoriesSpecificationResponse.builder()
                .accessoryType(MarineAccessoriesSpecificationDto.AccessoryType.valueOf(spec.getAccessoryType()))
                .brand(spec.getBrand())
                .condition(ItemCondition.valueOf(spec.getCondition()))
                .build();
    }

    private RentalsSpecificationResponse mapRentalsSpecToResponse(RentalsSpecification spec) {
        return RentalsSpecificationResponse.builder()
                .rentalType(RentalsSpecificationDto.RentalType.valueOf(spec.getRentalType()))
                .licenseRequired(spec.getLicenseRequired())
                .managementType(RentalsSpecificationDto.ManagementType.valueOf(spec.getManagementType()))
                .numberOfPeople(spec.getNumberOfPeople())
                .serviceType(RentalsSpecificationDto.ServiceType.valueOf(spec.getServiceType()))
                .companyName(spec.getCompanyName())
                .description(spec.getDescription())
                .contactPhone(spec.getContactPhone())
                .contactEmail(spec.getContactEmail())
                .address(spec.getAddress())
                .website(spec.getWebsite())
                .maxPrice(spec.getMaxPrice())
                .priceSpecification(spec.getPriceSpecification())
                .build();
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
                .purpose(spec.getBoatPurpose() != null ?                                     // NEW FIELD
                        BoatSpecificationDto.BoatPurpose.valueOf(spec.getBoatPurpose()) : null) // NEW FIELD
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
                .waterType(spec.getWaterType() != null ?                                     // NEW FIELD
                        BoatSpecificationDto.WaterType.valueOf(spec.getWaterType()) : null)  // NEW FIELD
                .engineHours(spec.getEngineHours())                                          // NEW FIELD
                .locatedInBulgaria(spec.getLocatedInBulgaria())                              // NEW FIELD
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
    // STATISTICS - UNCHANGED
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
}