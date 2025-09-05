package com.platform.ads.repository;

import com.platform.ads.entity.MarineAccessoriesSpecification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MarineAccessoriesSpecificationRepository extends ReactiveCrudRepository<MarineAccessoriesSpecification, Long> {
    Mono<MarineAccessoriesSpecification> findByAdId(Long adId);
    Mono<Void> deleteByAdId(Long adId);
}