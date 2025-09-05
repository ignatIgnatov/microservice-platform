package com.platform.ads.repository;

import com.platform.ads.entity.WaterSportsSpecification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface WaterSportsSpecificationRepository extends ReactiveCrudRepository<WaterSportsSpecification, Long> {
    Mono<WaterSportsSpecification> findByAdId(Long adId);
    Mono<Void> deleteByAdId(Long adId);
}