package com.platform.ads.repository;

import com.platform.ads.model.Ad;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface AdRepository extends ReactiveCrudRepository<Ad, Long> {

    // Find by user email
    Flux<Ad> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    // Find by active status
    Flux<Ad> findByActiveOrderByCreatedAtDesc(Boolean active);

    // Count ads by user email
    Mono<Long> countByUserEmail(String userEmail);

    // Search in title and description
    @Query("SELECT * FROM ads WHERE (LOWER(title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(description) LIKE LOWER(CONCAT('%', :query, '%'))) AND active = :active ORDER BY created_at DESC")
    Flux<Ad> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndActive(String query1, String query2, Boolean active);

    // Find by category
    Flux<Ad> findByCategoryIgnoreCaseAndActiveOrderByCreatedAtDesc(String category, Boolean active);

    // Find by location
    Flux<Ad> findByLocationIgnoreCaseAndActiveOrderByCreatedAtDesc(String location, Boolean active);

    // Find by price range
    @Query("SELECT * FROM ads WHERE price BETWEEN :minPrice AND :maxPrice AND active = :active ORDER BY created_at DESC")
    Flux<Ad> findByPriceBetweenAndActiveOrderByCreatedAtDesc(BigDecimal minPrice, BigDecimal maxPrice, Boolean active);

    // Complex search query
    @Query("SELECT * FROM ads WHERE " +
            "(:category IS NULL OR LOWER(category) = LOWER(:category)) AND " +
            "(:location IS NULL OR LOWER(location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:minPrice IS NULL OR price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR price <= :maxPrice) AND " +
            "(:query IS NULL OR LOWER(title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "active = :active " +
            "ORDER BY created_at DESC")
    Flux<Ad> searchAds(String query, String category, String location, BigDecimal minPrice, BigDecimal maxPrice, Boolean active);

    // Statistics queries
    @Query("SELECT COUNT(*) FROM ads WHERE active = true")
    Mono<Long> countActiveAds();

    @Query("SELECT COUNT(*) FROM ads WHERE active = false")
    Mono<Long> countInactiveAds();

    @Query("SELECT AVG(price) FROM ads WHERE active = true")
    Mono<BigDecimal> getAveragePrice();

    @Query("SELECT MIN(price) FROM ads WHERE active = true")
    Mono<BigDecimal> getMinPrice();

    @Query("SELECT MAX(price) FROM ads WHERE active = true")
    Mono<BigDecimal> getMaxPrice();

    // User statistics
    @Query("SELECT COUNT(*) FROM ads WHERE user_email = :userEmail AND active = true")
    Mono<Long> countActiveAdsByUser(String userEmail);

    @Query("SELECT COUNT(*) FROM ads WHERE user_email = :userEmail AND active = false")
    Mono<Long> countInactiveAdsByUser(String userEmail);

    @Query("SELECT SUM(price) FROM ads WHERE user_email = :userEmail AND active = true")
    Mono<BigDecimal> getTotalValueByUser(String userEmail);

    @Query("SELECT AVG(price) FROM ads WHERE user_email = :userEmail AND active = true")
    Mono<BigDecimal> getAveragePriceByUser(String userEmail);

    // Category statistics
    @Query("SELECT category, COUNT(*) as ad_count FROM ads WHERE active = true GROUP BY category ORDER BY ad_count DESC")
    Flux<Object[]> getCategoryStatistics();
}