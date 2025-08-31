package com.platform.ads.repository;

import com.platform.ads.entity.ServicesSpecification;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ServicesSpecificationSearchRepository extends ReactiveCrudRepository<ServicesSpecification, Long> {

    @Query("""
        SELECT ad_id FROM services_specifications 
        WHERE (:serviceType IS NULL OR service_type = :serviceType)
        AND (:authorizedService IS NULL OR is_authorized_service = :authorizedService)
        AND (:supportedBrand IS NULL OR LOWER(supported_brands) LIKE LOWER(CONCAT('%', :supportedBrand, '%')))
        AND (:companyName IS NULL OR LOWER(company_name) LIKE LOWER(CONCAT('%', :companyName, '%')))
        """)
    Flux<Long> searchServicesAdIds(
            String serviceType, Boolean authorizedService,
            String supportedBrand, String companyName
    );
}
