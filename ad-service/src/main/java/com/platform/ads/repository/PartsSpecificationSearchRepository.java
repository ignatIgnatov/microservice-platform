package com.platform.ads.repository;

import com.platform.ads.entity.PartsSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PartsSpecificationSearchRepository extends ReactiveCrudRepository<PartsSpecification, Long> {

    @Query("""
        SELECT ad_id FROM parts_specifications 
        WHERE (:partType IS NULL OR part_type = :partType)
        AND (:condition IS NULL OR condition = :condition)
        """)
    Flux<Long> searchPartsAdIds(String partType, String condition);
}
