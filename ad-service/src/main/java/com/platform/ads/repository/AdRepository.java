package com.platform.ads.repository;

import com.platform.ads.entity.*;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface AdRepository extends ReactiveCrudRepository<Ad, Long> {

    // Basic queries
    Flux<Ad> findByActiveOrderByCreatedAtDesc(Boolean active);
    Flux<Ad> findByCategoryAndActiveOrderByCreatedAtDesc(String category, Boolean active);
    Flux<Ad> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    Flux<Ad> findByLocationIgnoreCaseContainingAndActiveOrderByCreatedAtDesc(String location, Boolean active);

    // Price queries
    @Query("SELECT * FROM ads WHERE price_type = 'FIXED_PRICE' AND price_amount BETWEEN :minPrice AND :maxPrice AND active = true ORDER BY created_at DESC")
    Flux<Ad> findByPriceBetweenAndActive(BigDecimal minPrice, BigDecimal maxPrice, Boolean active);

    // Search queries
    @Query("SELECT * FROM ads WHERE " +
            "(LOWER(title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND active = :active ORDER BY created_at DESC")
    Flux<Ad> searchByTitleOrDescription(String query, Boolean active);

    @Query("SELECT * FROM ads WHERE " +
            "(:category IS NULL OR category = :category) AND " +
            "(:location IS NULL OR LOWER(location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:priceType IS NULL OR price_type = :priceType) AND " +
            "(:minPrice IS NULL OR (price_type = 'FIXED_PRICE' AND price_amount >= :minPrice)) AND " +
            "(:maxPrice IS NULL OR (price_type = 'FIXED_PRICE' AND price_amount <= :maxPrice)) AND " +
            "(:adType IS NULL OR ad_type = :adType) AND " +
            "active = :active " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'PRICE_LOW_TO_HIGH' THEN price_amount END ASC, " +
            "CASE WHEN :sortBy = 'PRICE_HIGH_TO_LOW' THEN price_amount END DESC, " +
            "CASE WHEN :sortBy = 'OLDEST' THEN created_at END ASC, " +
            "created_at DESC")
    Flux<Ad> advancedSearch(String category, String location, String priceType,
                            BigDecimal minPrice, BigDecimal maxPrice, String adType,
                            Boolean active, String sortBy);

    // Statistics
    Mono<Long> countByActiveAndCategory(Boolean active, String category);
    Mono<Long> countByUserEmail(String userEmail);

    @Query("SELECT AVG(price_amount) FROM ads WHERE active = true AND price_type = 'FIXED_PRICE'")
    Mono<BigDecimal> getAveragePrice();

    @Query("SELECT MIN(price_amount) FROM ads WHERE active = true AND price_type = 'FIXED_PRICE'")
    Mono<BigDecimal> getMinPrice();

    @Query("SELECT MAX(price_amount) FROM ads WHERE active = true AND price_type = 'FIXED_PRICE'")
    Mono<BigDecimal> getMaxPrice();

    @Query("SELECT category, COUNT(*) as count FROM ads WHERE active = true GROUP BY category ORDER BY count DESC")
    Flux<Object[]> getCategoryStatistics();

    @Query("SELECT location, COUNT(*) as count FROM ads WHERE active = true GROUP BY location ORDER BY count DESC LIMIT 10")
    Flux<Object[]> getLocationStatistics();

    // Featured and popular
    Flux<Ad> findByFeaturedAndActiveOrderByCreatedAtDesc(Boolean featured, Boolean active);
    Flux<Ad> findByActiveOrderByViewsCountDesc(Boolean active);

    // Update views count
    @Query("UPDATE ads SET views_count = views_count + 1 WHERE id = :id")
    Mono<Void> incrementViewsCount(Long id);
}