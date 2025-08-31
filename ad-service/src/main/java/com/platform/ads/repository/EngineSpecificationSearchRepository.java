package com.platform.ads.repository;

import com.platform.ads.entity.EngineSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EngineSpecificationSearchRepository extends ReactiveCrudRepository<EngineSpecification, Long> {

    @Query("""
        SELECT ad_id FROM engine_specifications 
        WHERE (:brand IS NULL OR LOWER(brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        AND (:minYear IS NULL OR year >= :minYear)
        AND (:maxYear IS NULL OR year <= :maxYear)
        AND (:condition IS NULL OR condition = :condition)
        AND (:engineType IS NULL OR engine_type = :engineType)
        AND (:minHorsepower IS NULL OR horsepower >= :minHorsepower)
        AND (:maxHorsepower IS NULL OR horsepower <= :maxHorsepower)
        AND (:strokeType IS NULL OR stroke_type = :strokeType)
        AND (:fuelType IS NULL OR fuel_type = :fuelType)
        """)
    Flux<Long> searchEngineAdIds(
            String brand, Integer minYear, Integer maxYear, String condition,
            String engineType, Integer minHorsepower, Integer maxHorsepower,
            String strokeType, String fuelType
    );
}
