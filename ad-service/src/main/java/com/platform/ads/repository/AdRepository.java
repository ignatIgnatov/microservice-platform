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

    // Advanced search method (keeping existing signature)
    @Query("""
        SELECT DISTINCT a.* FROM ads a
        LEFT JOIN boat_specifications bs ON a.id = bs.ad_id AND a.category = 'BOATS_AND_YACHTS'
        LEFT JOIN jet_ski_specifications js ON a.id = js.ad_id AND a.category = 'JET_SKIS'
        LEFT JOIN trailer_specifications ts ON a.id = ts.ad_id AND a.category = 'TRAILERS'
        LEFT JOIN engine_specifications es ON a.id = es.ad_id AND a.category = 'ENGINES'
        LEFT JOIN marine_electronics_specifications mes ON a.id = mes.ad_id AND a.category = 'MARINE_ELECTRONICS'
        LEFT JOIN fishing_specifications fs ON a.id = fs.ad_id AND a.category = 'FISHING'
        LEFT JOIN parts_specifications ps ON a.id = ps.ad_id AND a.category = 'PARTS'
        LEFT JOIN services_specifications ss ON a.id = ss.ad_id AND a.category = 'SERVICES'
        WHERE 
            (:category IS NULL OR a.category = :category)
            AND (:location IS NULL OR LOWER(a.location) LIKE LOWER(CONCAT('%', :location, '%')))
            AND (:priceType IS NULL OR a.price_type = :priceType)
            AND (:minPrice IS NULL OR a.price_amount >= :minPrice)
            AND (:maxPrice IS NULL OR a.price_amount <= :maxPrice)
            AND (:adType IS NULL OR a.ad_type = :adType)
            AND (:active IS NULL OR a.active = :active)
            AND (:brand IS NULL OR 
                (a.category = 'BOATS_AND_YACHTS' AND LOWER(bs.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) OR
                (a.category = 'JET_SKIS' AND LOWER(js.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) OR
                (a.category = 'TRAILERS' AND LOWER(ts.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) OR
                (a.category = 'ENGINES' AND LOWER(es.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) OR
                (a.category = 'MARINE_ELECTRONICS' AND LOWER(mes.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) OR
                (a.category = 'FISHING' AND LOWER(fs.brand) LIKE LOWER(CONCAT('%', :brand, '%'))))
            AND (:model IS NULL OR 
                (a.category = 'BOATS_AND_YACHTS' AND LOWER(bs.model) LIKE LOWER(CONCAT('%', :model, '%'))) OR
                (a.category = 'JET_SKIS' AND LOWER(js.model) LIKE LOWER(CONCAT('%', :model, '%'))) OR
                (a.category = 'TRAILERS' AND LOWER(ts.model) LIKE LOWER(CONCAT('%', :model, '%'))))
            AND (:minYear IS NULL OR 
                (a.category = 'BOATS_AND_YACHTS' AND bs.year >= :minYear) OR
                (a.category = 'JET_SKIS' AND js.year >= :minYear) OR
                (a.category = 'TRAILERS' AND ts.year >= :minYear) OR
                (a.category = 'ENGINES' AND es.year >= :minYear) OR
                (a.category = 'MARINE_ELECTRONICS' AND mes.year >= :minYear))
            AND (:maxYear IS NULL OR 
                (a.category = 'BOATS_AND_YACHTS' AND bs.year <= :maxYear) OR
                (a.category = 'JET_SKIS' AND js.year <= :maxYear) OR
                (a.category = 'TRAILERS' AND ts.year <= :maxYear) OR
                (a.category = 'ENGINES' AND es.year <= :maxYear) OR
                (a.category = 'MARINE_ELECTRONICS' AND mes.year <= :maxYear))
            AND (:condition IS NULL OR 
                (a.category = 'BOATS_AND_YACHTS' AND bs.condition = :condition) OR
                (a.category = 'JET_SKIS' AND js.condition = :condition) OR
                (a.category = 'TRAILERS' AND ts.condition = :condition) OR
                (a.category = 'ENGINES' AND es.condition = :condition) OR
                (a.category = 'MARINE_ELECTRONICS' AND mes.condition = :condition) OR
                (a.category = 'FISHING' AND fs.condition = :condition) OR
                (a.category = 'PARTS' AND ps.condition = :condition))
            AND (:electronicsType IS NULL OR (a.category = 'MARINE_ELECTRONICS' AND mes.electronics_type = :electronicsType))
            AND (:screenSize IS NULL OR (a.category = 'MARINE_ELECTRONICS' AND mes.screen_size = :screenSize))
            AND (:gpsIntegrated IS NULL OR (a.category = 'MARINE_ELECTRONICS' AND mes.gps_integrated = :gpsIntegrated))
            AND (:fishingType IS NULL OR (a.category = 'FISHING' AND fs.fishing_type = :fishingType))
            AND (:fishingTechnique IS NULL OR (a.category = 'FISHING' AND fs.fishing_technique = :fishingTechnique))
            AND (:targetFish IS NULL OR (a.category = 'FISHING' AND fs.target_fish = :targetFish))
            AND (:partType IS NULL OR (a.category = 'PARTS' AND ps.part_type = :partType))
            AND (:serviceType IS NULL OR (a.category = 'SERVICES' AND ss.service_type = :serviceType))
            AND (:authorizedService IS NULL OR (a.category = 'SERVICES' AND ss.is_authorized_service = :authorizedService))
            AND (:supportedBrand IS NULL OR (a.category = 'SERVICES' AND LOWER(ss.supported_brands) LIKE LOWER(CONCAT('%', :supportedBrand, '%'))))
        ORDER BY 
            CASE WHEN :sortBy = 'PRICE_LOW_TO_HIGH' THEN a.price_amount END ASC,
            CASE WHEN :sortBy = 'PRICE_HIGH_TO_LOW' THEN a.price_amount END DESC,
            CASE WHEN :sortBy = 'OLDEST' THEN a.created_at END ASC,
            CASE WHEN :sortBy = 'MOST_VIEWED' THEN a.views_count END DESC,
            a.created_at DESC
        """)
    Flux<Ad> advancedSearch(
            String category, String location, String priceType, BigDecimal minPrice, BigDecimal maxPrice,
            String adType, Boolean active, String sortBy, String brand, String model, Integer minYear,
            Integer maxYear, String condition, String electronicsType, String screenSize, Boolean gpsIntegrated,
            String fishingType, String fishingTechnique, String targetFish, String partType, String serviceType,
            Boolean authorizedService, String supportedBrand
    );
}