package com.platform.ads.service;

import com.platform.ads.dto.ImageUploadResponse;
import com.platform.ads.entity.AdImage;
import com.platform.ads.repository.AdImageRepository;
import com.platform.ads.repository.AdRepository;
import com.platform.ads.exception.AdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;
    private final AdImageRepository imageRepository;
    private final AdRepository adRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.base-url}")
    private String s3BaseUrl;

    // Allowed image types
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    // Max file size: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // ===========================
    // UPLOAD IMAGES
    // ===========================

    @Transactional
    public Mono<ImageUploadResponse> uploadImage(String userId, Long adId, FilePart filePart) {
        long startTime = System.currentTimeMillis();
        log.info("=== UPLOAD IMAGE START === UserId: {}, AdID: {}, FileName: '{}' ===",
                userId, adId, filePart.filename());

        return validateAdOwnership(userId, adId)
                .flatMap(ad -> validateImageFile(filePart))
                .flatMap(filePart2 -> processImageUpload(userId, adId, filePart2))
                .doOnSuccess(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== UPLOAD IMAGE SUCCESS === UserId: {}, AdID: {}, ImageID: {}, Duration: {}ms ===",
                            userId, adId, response.getId(), duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== UPLOAD IMAGE ERROR === UserId: {}, AdID: {}, Duration: {}ms, Error: {} ===",
                            userId, adId, duration, error.getMessage());
                });
    }

    @Transactional
    public Flux<ImageUploadResponse> uploadMultipleImages(String userId, Long adId, Flux<FilePart> fileParts) {
        long startTime = System.currentTimeMillis();
        log.info("=== UPLOAD MULTIPLE IMAGES START === UserId: {}, AdID: {} ===", userId, adId);

        return validateAdOwnership(userId, adId)
                .flatMapMany(ad -> fileParts
                        .flatMap(filePart -> validateImageFile(filePart)
                                .flatMap(validFilePart -> processImageUpload(userId, adId, validFilePart))
                                .onErrorResume(error -> {
                                    log.error("=== SINGLE IMAGE UPLOAD ERROR === FileName: '{}', Error: {} ===",
                                            filePart.filename(), error.getMessage());
                                    return Mono.empty(); // Skip failed uploads, continue with others
                                })
                        )
                )
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== UPLOAD MULTIPLE IMAGES COMPLETE === UserId: {}, AdID: {}, Duration: {}ms ===",
                            userId, adId, duration);
                });
    }

    private Mono<FilePart> validateImageFile(FilePart filePart) {
        log.debug("=== VALIDATING IMAGE FILE === FileName: '{}', ContentType: '{}' ===",
                filePart.filename(), filePart.headers().getContentType());

        // Validate content type
        String contentType = filePart.headers().getContentType() != null ?
                filePart.headers().getContentType().toString() : "";

        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.error("=== INVALID CONTENT TYPE === FileName: '{}', ContentType: '{}' ===",
                    filePart.filename(), contentType);
            return Mono.error(new IllegalArgumentException(
                    "Invalid file type. Allowed types: JPEG, PNG, WEBP"));
        }

        // Validate file size would be done here in a real implementation
        // For now, we'll validate after reading the content

        return Mono.just(filePart);
    }

    private Mono<ImageUploadResponse> processImageUpload(String userId, Long adId, FilePart filePart) {
        String originalFileName = filePart.filename();
        String contentType = filePart.headers().getContentType().toString();
        String s3Key = generateS3Key(adId, originalFileName);

        log.debug("=== PROCESSING IMAGE UPLOAD === S3Key: '{}', ContentType: '{}' ===", s3Key, contentType);

        return filePart.content()
                .collectList()
                .flatMap(dataBuffers -> {
                    byte[] bytes = collectDataBufferBytes(dataBuffers);

                    // Validate file size
                    if (bytes.length > MAX_FILE_SIZE) {
                        log.error("=== FILE TOO LARGE === Size: {} bytes, Max: {} bytes ===",
                                bytes.length, MAX_FILE_SIZE);
                        return Mono.error(new IllegalArgumentException(
                                "File too large. Maximum size: " + (MAX_FILE_SIZE / 1024 / 1024) + "MB"));
                    }

                    // Get image dimensions
                    int[] dimensions = getImageDimensions(bytes);

                    // Upload to S3
                    return uploadToS3(s3Key, bytes, contentType)
                            .flatMap(s3Url -> {
                                // Get next display order
                                return getNextDisplayOrder(adId)
                                        .flatMap(displayOrder -> {
                                            // Save image record
                                            AdImage adImage = AdImage.builder()
                                                    .adId(adId)
                                                    .fileName(generateFileName(originalFileName))
                                                    .originalFileName(originalFileName)
                                                    .s3Key(s3Key)
                                                    .s3Url(s3Url)
                                                    .contentType(contentType)
                                                    .fileSize((long) bytes.length)
                                                    .displayOrder(displayOrder)
                                                    .width(dimensions[0])
                                                    .height(dimensions[1])
                                                    .uploadedAt(LocalDateTime.now())
                                                    .uploadedBy(userId)
                                                    .active(true)
                                                    .build();

                                            return imageRepository.save(adImage);
                                        });
                            })
                            .map(savedImage -> ImageUploadResponse.builder()
                                    .id(savedImage.getId())
                                    .fileName(savedImage.getFileName())
                                    .originalFileName(savedImage.getOriginalFileName())
                                    .url(savedImage.getS3Url())
                                    .contentType(savedImage.getContentType())
                                    .fileSize(savedImage.getFileSize())
                                    .width(savedImage.getWidth())
                                    .height(savedImage.getHeight())
                                    .displayOrder(savedImage.getDisplayOrder())
                                    .uploadedAt(savedImage.getUploadedAt())
                                    .build());
                });
    }

    // ===========================
    // IMAGE MANAGEMENT
    // ===========================

    public Flux<ImageUploadResponse> getAdImages(Long adId) {
        log.info("=== GET AD IMAGES === AdID: {} ===", adId);

        return imageRepository.findByAdIdOrderByDisplayOrder(adId)
                .map(this::mapToResponse)
                .doOnComplete(() -> log.debug("=== GET AD IMAGES COMPLETE === AdID: {} ===", adId));
    }

    public Mono<ImageUploadResponse> getPrimaryImage(Long adId) {
        log.debug("=== GET PRIMARY IMAGE === AdID: {} ===", adId);

        return imageRepository.findPrimaryImageByAdId(adId)
                .map(this::mapToResponse);
    }

    @Transactional
    public Mono<Void> deleteImage(String userId, Long imageId) {
        long startTime = System.currentTimeMillis();
        log.info("=== DELETE IMAGE START === UserId: {}, ImageID: {} ===", userId, imageId);

        return imageRepository.isImageOwnedByUser(imageId, userId)
                .flatMap(isOwned -> {
                    if (!isOwned) {
                        log.warn("=== IMAGE NOT OWNED BY USER === UserId: {}, ImageID: {} ===", userId, imageId);
                        return Mono.error(new IllegalArgumentException("Image not owned by user"));
                    }

                    return imageRepository.findById(imageId)
                            .flatMap(image -> {
                                log.info("=== DELETING IMAGE FROM S3 === S3Key: '{}' ===", image.getS3Key());

                                // Delete from S3
                                return deleteFromS3(image.getS3Key())
                                        .then(imageRepository.deleteById(imageId))
                                        .then(reorderImagesAfterDeletion(image.getAdId(), image.getDisplayOrder()));
                            });
                })
                .doOnSuccess(result -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== DELETE IMAGE SUCCESS === UserId: {}, ImageID: {}, Duration: {}ms ===",
                            userId, imageId, duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("=== DELETE IMAGE ERROR === UserId: {}, ImageID: {}, Duration: {}ms, Error: {} ===",
                            userId, imageId, duration, error.getMessage());
                });
    }

    @Transactional
    public Mono<Void> reorderImages(String userId, Long adId, List<Long> imageIds) {
        long startTime = System.currentTimeMillis();
        log.info("=== REORDER IMAGES START === UserId: {}, AdID: {}, NewOrder: {} ===",
                userId, adId, imageIds);

        return validateAdOwnership(userId, adId)
                .flatMapMany(ad -> Flux.fromIterable(imageIds)
                        .index()
                        .flatMap(tuple -> {
                            Long imageId = tuple.getT2();
                            Integer newOrder = tuple.getT1().intValue();
                            return imageRepository.updateDisplayOrder(imageId, newOrder);
                        })
                )
                .then()
                .doOnSuccess(result -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("=== REORDER IMAGES SUCCESS === UserId: {}, AdID: {}, Duration: {}ms ===",
                            userId, adId, duration);
                });
    }

    public Mono<Void> deleteFromS3(String s3Key) {
        return Mono.fromRunnable(() -> {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.debug("=== S3 DELETE SUCCESS === S3Key: '{}' ===", s3Key);
        });
    }

    // ===========================
    // HELPER METHODS
    // ===========================

    private Mono<Object> validateAdOwnership(String userId, Long adId) {
        return adRepository.findById(adId)
                .switchIfEmpty(Mono.error(new AdNotFoundException(adId)))
                .flatMap(ad -> {
                    if (!userId.equals(ad.getUserId())) {
                        log.warn("=== AD NOT OWNED BY USER === UserId: {}, AdID: {}, ActualOwner: {} ===",
                                userId, adId, ad.getUserId());
                        return Mono.error(new IllegalArgumentException("Advertisement not owned by user"));
                    }
                    return Mono.just(ad);
                });
    }

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

    private int[] getImageDimensions(byte[] imageBytes) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (bufferedImage != null) {
                return new int[]{bufferedImage.getWidth(), bufferedImage.getHeight()};
            }
        } catch (IOException e) {
            log.warn("=== FAILED TO READ IMAGE DIMENSIONS === Error: {} ===", e.getMessage());
        }
        return new int[]{0, 0};
    }

    private String generateS3Key(Long adId, String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("ads/%d/images/%s%s", adId, uniqueId, extension);
    }

    private String generateFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

    private Mono<Integer> getNextDisplayOrder(Long adId) {
        return imageRepository.countByAdId(adId)
                .map(count -> count.intValue());
    }

    private Mono<String> uploadToS3(String s3Key, byte[] bytes, String contentType) {
        log.debug("=== UPLOADING TO S3 === S3Key: '{}', Size: {} bytes ===", s3Key, bytes.length);

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

    private Mono<Void> reorderImagesAfterDeletion(Long adId, Integer deletedOrder) {
        return imageRepository.findByAdIdOrderByDisplayOrder(adId)
                .filter(image -> image.getDisplayOrder() > deletedOrder)
                .flatMap(image -> imageRepository.updateDisplayOrder(image.getId(), image.getDisplayOrder() - 1))
                .then();
    }

    private ImageUploadResponse mapToResponse(AdImage image) {
        return ImageUploadResponse.builder()
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
                .build();
    }
}