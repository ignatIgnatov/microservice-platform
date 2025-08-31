package com.platform.ads.repository;

import com.platform.ads.entity.TrailerSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TrailerSpecificationSearchRepository extends ReactiveCrudRepository<TrailerSpecification, Long> {

    @Query("""
        SELECT ad_id FROM trailer_specifications 
        WHERE (:brand IS NULL OR LOWER(brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        AND (:model IS NULL OR LOWER(model) LIKE LOWER(CONCAT('%', :model, '%')))
        AND (:minYear IS NULL OR year >= :minYear)
        AND (:maxYear IS NULL OR year <= :maxYear)
        AND (:condition IS NULL OR condition = :condition)
        AND (:trailerType IS NULL OR trailer_type = :trailerType)
        AND (:axleCount IS NULL OR axle_count = :axleCount)
        AND (:minLoadCapacity IS NULL OR load_capacity >= :minLoadCapacity)
        AND (:maxLoadCapacity IS NULL OR load_capacity <= :maxLoadCapacity)
        """)
    Flux<Long> searchTrailerAdIds(
            String brand, String model, Integer minYear, Integer maxYear,
            String condition, String trailerType, String axleCount,
            Integer minLoadCapacity, Integer maxLoadCapacity
    );
}
