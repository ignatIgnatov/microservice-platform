package com.platform.ads.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MarineAccessoriesSpecificationSearchRepository extends ReactiveCrudRepository<com.platform.ads.entity.MarineAccessoriesSpecification, Long> {

    @Query("""
        SELECT mas.ad_id 
        FROM marine_accessories_specifications mas
        WHERE (:brand IS NULL OR LOWER(mas.brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        AND (:accessoryType IS NULL OR mas.accessory_type = :accessoryType)
        AND (:condition IS NULL OR mas.condition = :condition)
        """)
    Flux<Long> searchMarineAccessoriesAdIds(String brand, String accessoryType, String condition);
}