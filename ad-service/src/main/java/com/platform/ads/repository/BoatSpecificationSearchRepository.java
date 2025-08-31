package com.platform.ads.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BoatSpecificationSearchRepository extends ReactiveCrudRepository<com.platform.ads.entity.BoatSpecification, Long> {

    @Query("""
        SELECT ad_id FROM boat_specifications 
        WHERE (:brand IS NULL OR LOWER(brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        AND (:model IS NULL OR LOWER(model) LIKE LOWER(CONCAT('%', :model, '%')))
        AND (:minYear IS NULL OR year >= :minYear)
        AND (:maxYear IS NULL OR year <= :maxYear)
        AND (:condition IS NULL OR condition = :condition)
        AND (:boatType IS NULL OR boat_type = :boatType)
        AND (:material IS NULL OR material = :material)
        AND (:engineType IS NULL OR engine_type = :engineType)
        AND (:minHorsepower IS NULL OR horsepower >= :minHorsepower)
        AND (:maxHorsepower IS NULL OR horsepower <= :maxHorsepower)
        """)
    Flux<Long> searchBoatAdIds(
            String brand, String model, Integer minYear, Integer maxYear,
            String condition, String boatType, String material, String engineType,
            Integer minHorsepower, Integer maxHorsepower
    );
}