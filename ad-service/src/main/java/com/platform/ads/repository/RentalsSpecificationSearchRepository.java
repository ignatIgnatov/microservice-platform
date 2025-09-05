package com.platform.ads.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@Repository
public interface RentalsSpecificationSearchRepository extends ReactiveCrudRepository<com.platform.ads.entity.RentalsSpecification, Long> {

    @Query("""
        SELECT rs.ad_id 
        FROM rentals_specifications rs
        WHERE (:rentalType IS NULL OR rs.rental_type = :rentalType)
        AND (:licenseRequired IS NULL OR rs.license_required = :licenseRequired)
        AND (:managementType IS NULL OR rs.management_type = :managementType)
        AND (:serviceType IS NULL OR rs.service_type = :serviceType)
        AND (:companyName IS NULL OR LOWER(rs.company_name) LIKE LOWER(CONCAT('%', :companyName, '%')))
        AND (:minPrice IS NULL OR rs.max_price >= :minPrice)
        AND (:maxPrice IS NULL OR rs.max_price <= :maxPrice)
        """)
    Flux<Long> searchRentalsAdIds(
            String rentalType,
            Boolean licenseRequired,
            String managementType,
            String serviceType,
            String companyName,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );
}