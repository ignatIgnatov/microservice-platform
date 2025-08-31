package com.platform.ads.repository;

import com.platform.ads.entity.JetSkiSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface JetSkiSpecificationSearchRepository extends ReactiveCrudRepository<JetSkiSpecification, Long> {

    @Query("""
        SELECT ad_id FROM jet_ski_specifications 
        WHERE (:brand IS NULL OR LOWER(brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        AND (:model IS NULL OR LOWER(model) LIKE LOWER(CONCAT('%', :model, '%')))
        AND (:minYear IS NULL OR year >= :minYear)
        AND (:maxYear IS NULL OR year <= :maxYear)
        AND (:condition IS NULL OR condition = :condition)
        AND (:minHorsepower IS NULL OR horsepower >= :minHorsepower)
        AND (:maxHorsepower IS NULL OR horsepower <= :maxHorsepower)
        AND (:fuelType IS NULL OR fuel_type = :fuelType)
        AND (:trailerIncluded IS NULL OR trailer_included = :trailerIncluded)
        """)
    Flux<Long> searchJetSkiAdIds(
            String brand, String model, Integer minYear, Integer maxYear,
            String condition, Integer minHorsepower, Integer maxHorsepower,
            String fuelType, Boolean trailerIncluded
    );
}