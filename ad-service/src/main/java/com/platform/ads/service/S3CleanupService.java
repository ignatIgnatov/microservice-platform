package com.platform.ads.service;

import com.platform.ads.repository.AdImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3CleanupService {

    private final S3Client s3Client;
    private final AdImageRepository imageRepository;
    private final ImageService imageService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Scheduled job to clean up orphaned S3 files
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOrphanedS3Files() {
        log.info("=== S3 CLEANUP JOB START ===");

        // List all objects in S3 with ads/ prefix
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix("ads/")
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        Flux.fromIterable(listResponse.contents())
                .flatMap(this::checkAndDeleteOrphanedFile)
                .then()
                .subscribe(
                        result -> log.info("=== S3 CLEANUP JOB COMPLETE ==="),
                        error -> log.error("=== S3 CLEANUP JOB ERROR === Error: {} ===", error.getMessage())
                );
    }

    private Mono<Void> checkAndDeleteOrphanedFile(S3Object s3Object) {
        String s3Key = s3Object.key();

        return imageRepository.findAll()
                .any(image -> s3Key.equals(image.getS3Key()))
                .flatMap(exists -> {
                    if (!exists) {
                        log.info("=== ORPHANED S3 FILE FOUND === S3Key: '{}' ===", s3Key);
                        return imageService.deleteFromS3(s3Key)
                                .doOnSuccess(result -> log.info("=== ORPHANED FILE DELETED === S3Key: '{}' ===", s3Key));
                    }
                    return Mono.empty();
                });
    }
}