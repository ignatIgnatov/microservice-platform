package com.platform.ads.repository;

import com.platform.ads.entity.AdImage;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AdImageRepository extends ReactiveCrudRepository<AdImage, Long> {

    // Get all images for an ad, ordered by display order
    @Query("SELECT * FROM ad_images WHERE ad_id = :adId AND active = true ORDER BY display_order ASC")
    Flux<AdImage> findByAdIdOrderByDisplayOrder(Long adId);

    // Get primary image (display_order = 0)
    @Query("SELECT * FROM ad_images WHERE ad_id = :adId AND display_order = 0 AND active = true LIMIT 1")
    Mono<AdImage> findPrimaryImageByAdId(Long adId);

    // Count images for an ad
    @Query("SELECT COUNT(*) FROM ad_images WHERE ad_id = :adId AND active = true")
    Mono<Long> countByAdId(Long adId);

    // Delete all images for an ad
    @Query("UPDATE ad_images SET active = false WHERE ad_id = :adId")
    Mono<Void> deactivateByAdId(Long adId);

    // Update display order
    @Query("UPDATE ad_images SET display_order = :displayOrder WHERE id = :imageId")
    Mono<Void> updateDisplayOrder(Long imageId, Integer displayOrder);

    // Check if user owns the image (through ad ownership)
    @Query("""
        SELECT COUNT(*) > 0 FROM ad_images ai 
        JOIN ads a ON ai.ad_id = a.id 
        WHERE ai.id = :imageId AND a.user_id = :userId AND ai.active = true
        """)
    Mono<Boolean> isImageOwnedByUser(Long imageId, String userId);
}
