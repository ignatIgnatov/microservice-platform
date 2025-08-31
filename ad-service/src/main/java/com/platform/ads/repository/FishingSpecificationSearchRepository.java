package com.platform.ads.repository;

import com.platform.ads.entity.FishingSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FishingSpecificationSearchRepository extends ReactiveCrudRepository<FishingSpecification, Long> {

    @Query("""
        SELECT ad_id FROM fishing_specifications 
        WHERE (:brand IS NULL OR LOWER(brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        AND (:fishingType IS NULL OR fishing_type = :fishingType)
        AND (:fishingTechnique IS NULL OR fishing_technique = :fishingTechnique)
        AND (:targetFish IS NULL OR target_fish = :targetFish)
        AND (:condition IS NULL OR condition = :condition)
        """)
    Flux<Long> searchFishingAdIds(
            String brand, String fishingType, String fishingTechnique,
            String targetFish, String condition
    );
}
