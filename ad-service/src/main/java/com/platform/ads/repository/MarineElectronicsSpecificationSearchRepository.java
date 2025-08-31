package com.platform.ads.repository;

import com.platform.ads.entity.MarineElectronicsSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MarineElectronicsSpecificationSearchRepository extends ReactiveCrudRepository<MarineElectronicsSpecification, Long> {

    @Query("""
        SELECT ad_id FROM marine_electronics_specifications 
        WHERE (:brand IS NULL OR LOWER(brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        AND (:electronicsType IS NULL OR electronics_type = :electronicsType)
        AND (:screenSize IS NULL OR screen_size = :screenSize)
        AND (:gpsIntegrated IS NULL OR gps_integrated = :gpsIntegrated)
        AND (:condition IS NULL OR condition = :condition)
        AND (:minYear IS NULL OR year >= :minYear)
        AND (:maxYear IS NULL OR year <= :maxYear)
        """)
    Flux<Long> searchMarineElectronicsAdIds(
            String brand, String electronicsType, String screenSize,
            Boolean gpsIntegrated, String condition, Integer minYear, Integer maxYear
    );
}
