package com.platform.ads.repository;

import com.platform.ads.entity.TrailerSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface TrailerSpecificationRepository extends ReactiveCrudRepository<TrailerSpecification, Long> {

    Mono<TrailerSpecification> findByAdId(Long adId);

    Flux<TrailerSpecification> findByBrandIgnoreCaseContaining(String brand);

    @Query("SELECT * FROM trailer_specifications ts JOIN ads a ON ts.ad_id = a.id WHERE " +
            "(:trailerType IS NULL OR ts.trailer_type = :trailerType) AND " +
            "(:brand IS NULL OR LOWER(ts.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:axleCount IS NULL OR ts.axle_count = :axleCount) AND " +
            "(:minLoadCapacity IS NULL OR ts.load_capacity >= :minLoadCapacity) AND " +
            "(:maxLoadCapacity IS NULL OR ts.load_capacity <= :maxLoadCapacity) AND " +
            "(:minYear IS NULL OR ts.year >= :minYear) AND " +
            "(:maxYear IS NULL OR ts.year <= :maxYear) AND " +
            "(:isRegistered IS NULL OR ts.is_registered = :isRegistered) AND " +
            "(:inWarranty IS NULL OR ts.in_warranty = :inWarranty) AND " +
            "(:keelRollers IS NULL OR ts.keel_rollers = :keelRollers) AND " +
            "(:condition IS NULL OR ts.condition = :condition) AND " +
            "a.active = true")
    Flux<TrailerSpecification> searchTrailers(String trailerType, String brand, String axleCount,
                                              BigDecimal minLoadCapacity, BigDecimal maxLoadCapacity,
                                              Integer minYear, Integer maxYear,
                                              Boolean isRegistered, Boolean inWarranty,
                                              String keelRollers, String condition);
}
