package com.platform.ads.repository;

import com.platform.ads.entity.RentalsSpecification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RentalsSpecificationRepository extends ReactiveCrudRepository<RentalsSpecification, Long> {
    Mono<RentalsSpecification> findByAdId(Long adId);
    Mono<Void> deleteByAdId(Long adId);
}