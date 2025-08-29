package com.platform.ads.repository;

import com.platform.ads.entity.ServicesSpecification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ServicesSpecificationRepository extends ReactiveCrudRepository<ServicesSpecification, Long> {
    Mono<ServicesSpecification> findByAdId(Long adId);
    Mono<Void> deleteByAdId(Long adId);
}