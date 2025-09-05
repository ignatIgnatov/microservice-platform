package com.platform.ads.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface WaterSportsSpecificationSearchRepository extends ReactiveCrudRepository<com.platform.ads.entity.WaterSportsSpecification, Long> {

    @Query("""
        SELECT ws.ad_id 
        FROM water_sports_specifications ws
        WHERE (:brand IS NULL OR LOWER(ws.brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        AND (:waterSportsType IS NULL OR ws.water_sports_type = :waterSportsType)
        AND (:condition IS NULL OR ws.condition = :condition)
        """)
    Flux<Long> searchWaterSportsAdIds(String brand, String waterSportsType, String condition);
}