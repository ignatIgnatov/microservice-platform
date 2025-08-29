package com.platform.ads.repository;

import com.platform.ads.dto.BoatSearchRequest;
import com.platform.ads.entity.Ad;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class AdRepositoryCustomImpl {

    private final DatabaseClient databaseClient;

    public Flux<Ad> searchWithDynamicFilters(BoatSearchRequest searchRequest) {
        StringBuilder query = new StringBuilder("""
                SELECT DISTINCT a.* FROM ads a
                LEFT JOIN boat_specifications bs ON a.id = bs.ad_id AND a.category = 'BOATS_AND_YACHTS'
                LEFT JOIN jet_ski_specifications js ON a.id = js.ad_id AND a.category = 'JET_SKIS'
                LEFT JOIN trailer_specifications ts ON a.id = ts.ad_id AND a.category = 'TRAILERS'
                LEFT JOIN engine_specifications es ON a.id = es.ad_id AND a.category = 'ENGINES'
                LEFT JOIN marine_electronics_specifications mes ON a.id = mes.ad_id AND a.category = 'MARINE_ELECTRONICS'
                LEFT JOIN fishing_specifications fs ON a.id = fs.ad_id AND a.category = 'FISHING'
                LEFT JOIN parts_specifications ps ON a.id = ps.ad_id AND a.category = 'PARTS'
                LEFT JOIN services_specifications ss ON a.id = ss.ad_id AND a.category = 'SERVICES'
                WHERE a.active = true
                """);

        DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(query.toString());

        // Add dynamic filters based on category
        if (searchRequest.getCategory() != null) {
            query.append(" AND a.category = :category");
            executeSpec = executeSpec.bind("category", searchRequest.getCategory().name());
        }

        if (searchRequest.getLocation() != null) {
            query.append(" AND LOWER(a.location) LIKE LOWER(:location)");
            executeSpec = executeSpec.bind("location", "%" + searchRequest.getLocation() + "%");
        }

        if (searchRequest.getMinPrice() != null) {
            query.append(" AND a.price_amount >= :minPrice");
            executeSpec = executeSpec.bind("minPrice", searchRequest.getMinPrice());
        }

        if (searchRequest.getMaxPrice() != null) {
            query.append(" AND a.price_amount <= :maxPrice");
            executeSpec = executeSpec.bind("maxPrice", searchRequest.getMaxPrice());
        }

        // Add category-specific filters
        addCategorySpecificFilters(query, executeSpec, searchRequest);

        // Add sorting
        addSorting(query, searchRequest.getSortBy());

        return executeSpec.map((row, metadata) -> mapRowToAd(row)).all();
    }

    private void addCategorySpecificFilters(StringBuilder query,
                                            DatabaseClient.GenericExecuteSpec executeSpec,
                                            BoatSearchRequest searchRequest) {

        // Common filters
        if (searchRequest.getBrand() != null) {
            query.append("""
                    AND (
                        (a.category = 'BOATS_AND_YACHTS' AND LOWER(bs.brand) LIKE LOWER(:brand)) OR
                        (a.category = 'JET_SKIS' AND LOWER(js.brand) LIKE LOWER(:brand)) OR
                        (a.category = 'TRAILERS' AND LOWER(ts.brand) LIKE LOWER(:brand)) OR
                        (a.category = 'ENGINES' AND LOWER(es.brand) LIKE LOWER(:brand)) OR
                        (a.category = 'MARINE_ELECTRONICS' AND LOWER(mes.brand) LIKE LOWER(:brand)) OR
                        (a.category = 'FISHING' AND LOWER(fs.brand) LIKE LOWER(:brand))
                    )
                    """);
            executeSpec.bind("brand", "%" + searchRequest.getBrand() + "%");
        }

        // Marine Electronics specific filters
        if (searchRequest.getElectronicsType() != null) {
            query.append(" AND mes.electronics_type = :electronicsType");
            executeSpec.bind("electronicsType", searchRequest.getElectronicsType());
        }

        if (searchRequest.getScreenSize() != null) {
            query.append(" AND mes.screen_size = :screenSize");
            executeSpec.bind("screenSize", searchRequest.getScreenSize());
        }

        if (searchRequest.getGpsIntegrated() != null) {
            query.append(" AND mes.gps_integrated = :gpsIntegrated");
            executeSpec.bind("gpsIntegrated", searchRequest.getGpsIntegrated());
        }

        // Fishing specific filters
        if (searchRequest.getFishingType() != null) {
            query.append(" AND fs.fishing_type = :fishingType");
            executeSpec.bind("fishingType", searchRequest.getFishingType());
        }

        if (searchRequest.getFishingTechnique() != null) {
            query.append(" AND fs.fishing_technique = :fishingTechnique");
            executeSpec.bind("fishingTechnique", searchRequest.getFishingTechnique());
        }

        if (searchRequest.getTargetFish() != null) {
            query.append(" AND fs.target_fish = :targetFish");
            executeSpec.bind("targetFish", searchRequest.getTargetFish());
        }

        // Parts specific filters
        if (searchRequest.getPartType() != null) {
            query.append(" AND ps.part_type = :partType");
            executeSpec.bind("partType", searchRequest.getPartType());
        }

        // Services specific filters
        if (searchRequest.getServiceType() != null) {
            query.append(" AND ss.service_type = :serviceType");
            executeSpec.bind("serviceType", searchRequest.getServiceType());
        }

        if (searchRequest.getAuthorizedService() != null) {
            query.append(" AND ss.is_authorized_service = :authorizedService");
            executeSpec.bind("authorizedService", searchRequest.getAuthorizedService());
        }

        if (searchRequest.getSupportedBrand() != null) {
            query.append(" AND LOWER(ss.supported_brands) LIKE LOWER(:supportedBrand)");
            executeSpec.bind("supportedBrand", "%" + searchRequest.getSupportedBrand() + "%");
        }
    }

    private void addSorting(StringBuilder query, String sortBy) {
        query.append(" ORDER BY ");

        if (sortBy != null) {
            switch (sortBy) {
                case "PRICE_LOW_TO_HIGH":
                    query.append("a.price_amount ASC NULLS LAST, ");
                    break;
                case "PRICE_HIGH_TO_LOW":
                    query.append("a.price_amount DESC NULLS LAST, ");
                    break;
                case "OLDEST":
                    query.append("a.created_at ASC, ");
                    break;
                case "MOST_VIEWED":
                    query.append("a.views_count DESC, ");
                    break;
            }
        }

        query.append("a.created_at DESC");
    }

    private Ad mapRowToAd(Row row) {
        return Ad.builder()
                .id(row.get("id", Long.class))
                .title(row.get("title", String.class))
                .description(row.get("description", String.class))
                .quickDescription(row.get("quick_description", String.class))
                .category(row.get("category", String.class))
                .priceAmount(row.get("price_amount", BigDecimal.class))
                .priceType(row.get("price_type", String.class))
                .includingVat(row.get("including_vat", Boolean.class))
                .location(row.get("location", String.class))
                .adType(row.get("ad_type", String.class))
                .userEmail(row.get("user_email", String.class))
                .userId(row.get("user_id", String.class))
                .userFirstName(row.get("user_first_name", String.class))
                .userLastName(row.get("user_last_name", String.class))
                .createdAt(row.get("created_at", LocalDateTime.class))
                .updatedAt(row.get("updated_at", LocalDateTime.class))
                .active(row.get("active", Boolean.class))
                .viewsCount(row.get("views_count", Integer.class))
                .featured(row.get("featured", Boolean.class))
                .build();
    }
}