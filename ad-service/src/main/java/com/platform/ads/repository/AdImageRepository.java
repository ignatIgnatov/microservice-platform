// AdImageRepository Interface

package com.platform.ads.repository;

import com.platform.ads.entity.AdImage;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AdImageRepository extends ReactiveCrudRepository<AdImage, Long> {

    // Find all images for an ad, ordered by display order
    @Query("SELECT * FROM ad_images WHERE ad_id = :adId AND active = true ORDER BY display_order ASC")
    Flux<AdImage> findByAdIdOrderByDisplayOrder(Long adId);

    // Get primary (first) image for an ad
    @Query("SELECT * FROM ad_images WHERE ad_id = :adId AND active = true ORDER BY display_order ASC LIMIT 1")
    Mono<AdImage> findPrimaryImageByAdId(Long adId);

    // Count images for an ad
    @Query("SELECT COUNT(*) FROM ad_images WHERE ad_id = :adId AND active = true")
    Mono<Long> countByAdId(Long adId);

    // Check if image is owned by user
    @Query("SELECT CASE WHEN COUNT(ai) > 0 THEN true ELSE false END " +
            "FROM AdImage ai WHERE ai.id = :imageId AND ai.uploadedBy = :userId")
    Mono<Boolean> isImageOwnedByUser(Long imageId, String userId);

    // Find images by ad ID (for internal use)
    Flux<AdImage> findByAdId(Long adId);

    // Soft delete - mark as inactive
    @Query("UPDATE ad_images SET active = false WHERE id = :imageId")
    Mono<Void> softDeleteById(Long imageId);

    // Get images for response mapping
    @Query("SELECT ai.* FROM ad_images ai WHERE ai.ad_id = :adId AND ai.active = true ORDER BY ai.display_order ASC")
    Flux<AdImage> findActiveImagesByAdId(Long adId);

    @Query("SELECT MAX(display_order) FROM ad_images WHERE ad_id = :adId")
    Mono<Integer> findMaxDisplayOrderByAdId(@Param("adId") Long adId);

    Mono<AdImage> findByAdIdAndId(Long adId, Long id);

    @Modifying
    @Query("UPDATE ad_images SET display_order = :displayOrder WHERE id = :id")
    Mono<Void> updateDisplayOrder(@Param("id") Long id, @Param("displayOrder") Integer displayOrder);

    Flux<AdImage> findByAdIdOrderByDisplayOrderAsc(Long adId);
}