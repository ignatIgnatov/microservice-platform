package com.platform.ads.repository;

import com.platform.ads.entity.BoatSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface BoatSpecificationRepository extends ReactiveCrudRepository<BoatSpecification, Long> {

    Mono<BoatSpecification> findByAdId(Long adId);

    Flux<BoatSpecification> findByBrandIgnoreCaseContaining(String brand);

    Flux<BoatSpecification> findByBoatType(String boatType);

    @Query("SELECT * FROM boat_specifications bs JOIN ads a ON bs.ad_id = a.id WHERE " +
            "(:boatType IS NULL OR bs.boat_type = :boatType) AND " +
            "(:brand IS NULL OR LOWER(bs.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:engineType IS NULL OR bs.engine_type = :engineType) AND " +
            "(:minHorsepower IS NULL OR bs.horsepower >= :minHorsepower) AND " +
            "(:maxHorsepower IS NULL OR bs.horsepower <= :maxHorsepower) AND " +
            "(:minLength IS NULL OR bs.length >= :minLength) AND " +
            "(:maxLength IS NULL OR bs.length <= :maxLength) AND " +
            "(:minYear IS NULL OR bs.year >= :minYear) AND " +
            "(:maxYear IS NULL OR bs.year <= :maxYear) AND " +
            "(:material IS NULL OR bs.material = :material) AND " +
            "(:fuelType IS NULL OR bs.fuel_type = :fuelType) AND " +
            "(:isRegistered IS NULL OR bs.is_registered = :isRegistered) AND " +
            "(:inWarranty IS NULL OR bs.in_warranty = :inWarranty) AND " +
            "(:engineIncluded IS NULL OR bs.engine_included = :engineIncluded) AND " +
            "(:consoleType IS NULL OR bs.console_type = :consoleType) AND " +
            "(:condition IS NULL OR bs.condition = :condition) AND " +
            "a.active = true")
    Flux<BoatSpecification> searchBoats(String boatType, String brand, String engineType,
                                        Integer minHorsepower, Integer maxHorsepower,
                                        BigDecimal minLength, BigDecimal maxLength,
                                        Integer minYear, Integer maxYear,
                                        String material, String fuelType,
                                        Boolean isRegistered, Boolean inWarranty,
                                        Boolean engineIncluded, String consoleType,
                                        String condition);

    // Statistics for boats
    @Query("SELECT brand, COUNT(*) as count FROM boat_specifications bs JOIN ads a ON bs.ad_id = a.id WHERE a.active = true GROUP BY brand ORDER BY count DESC LIMIT 10")
    Flux<Object[]> getPopularBrands();

    @Query("SELECT AVG(horsepower) FROM boat_specifications bs JOIN ads a ON bs.ad_id = a.id WHERE a.active = true")
    Mono<BigDecimal> getAverageHorsepower();
}

