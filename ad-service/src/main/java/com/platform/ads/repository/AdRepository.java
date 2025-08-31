package com.platform.ads.repository;

import com.platform.ads.entity.Ad;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Repository
public interface AdRepository extends ReactiveCrudRepository<Ad, Long> {

    // Existing methods...
    Mono<Long> countByActiveAndCategory(Boolean active, String category);

    @Query("UPDATE ads SET views_count = views_count + 1 WHERE id = :id")
    Mono<Void> incrementViewsCount(Long id);

    @Query("SELECT AVG(price_amount) FROM ads WHERE active = true AND price_amount IS NOT NULL")
    Mono<BigDecimal> getAveragePrice();

    @Query("SELECT MIN(price_amount) FROM ads WHERE active = true AND price_amount IS NOT NULL")
    Mono<BigDecimal> getMinPrice();

    @Query("SELECT MAX(price_amount) FROM ads WHERE active = true AND price_amount IS NOT NULL")
    Mono<BigDecimal> getMaxPrice();

    // New admin-specific methods
    @Query("SELECT * FROM ads WHERE approval_status = :status ORDER BY created_at ASC")
    Flux<Ad> findByApprovalStatus(String status);

    @Query("UPDATE ads SET featured = :featured WHERE id = :id")
    Mono<Void> updateFeaturedStatus(Long id, Boolean featured);

    @Query("UPDATE ads SET approval_status = :status, approved_by_user_id = :approvedBy, approved_at = :approvedAt, rejection_reason = :rejectionReason WHERE id = :id")
    Mono<Void> updateApprovalStatus(Long id, String status, String approvedBy, java.time.LocalDateTime approvedAt, String rejectionReason);

    @Query("SELECT category, COUNT(*) as count FROM ads GROUP BY category")
    Flux<Map<String, Object>> countByCategory();

    @Query("SELECT category, COUNT(*) as count FROM ads WHERE active = true GROUP BY category")
    Flux<Map<String, Object>> countActiveByCategory();

    @Query("SELECT category, AVG(price_amount) as avg_price FROM ads WHERE price_amount IS NOT NULL GROUP BY category")
    Flux<Map<String, Object>> getAveragePriceByCategory();

    @Query("SELECT user_email, COUNT(*) as ad_count FROM ads GROUP BY user_email ORDER BY ad_count DESC LIMIT :limit")
    Flux<Map<String, Object>> getTopSellersByAdCount(int limit);

    @Query("SELECT DATE(created_at) as date, COUNT(*) as count FROM ads WHERE created_at >= :fromDate GROUP BY DATE(created_at) ORDER BY date DESC")
    Flux<Map<String, Object>> getAdCountsByDate(java.time.LocalDateTime fromDate);

    @Query("SELECT approval_status, COUNT(*) as count FROM ads GROUP BY approval_status")
    Flux<Map<String, Object>> countByApprovalStatus();

    @Query("""
        SELECT * FROM ads 
        WHERE (:category IS NULL OR category = :category)
        AND (:location IS NULL OR LOWER(location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:priceType IS NULL OR price_type = :priceType)
        AND (:minPrice IS NULL OR price_amount >= :minPrice)
        AND (:maxPrice IS NULL OR price_amount <= :maxPrice)
        AND (:adType IS NULL OR ad_type = :adType)
        AND (:active IS NULL OR active = :active)
        ORDER BY 
            CASE WHEN :sortBy = 'PRICE_LOW_TO_HIGH' THEN price_amount END ASC,
            CASE WHEN :sortBy = 'PRICE_HIGH_TO_LOW' THEN price_amount END DESC,
            CASE WHEN :sortBy = 'OLDEST' THEN created_at END ASC,
            CASE WHEN :sortBy = 'MOST_VIEWED' THEN views_count END DESC,
            created_at DESC
        """)
    Flux<Ad> basicSearch(
            String category, String location, String priceType,
            BigDecimal minPrice, BigDecimal maxPrice, String adType,
            Boolean active, String sortBy
    );
}