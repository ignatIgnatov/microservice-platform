-- V4__Create_views_and_optimizations.sql
-- Views, final optimizations (FIXED - removed problematic indexes)

-- =============================================================================
-- USEFUL VIEWS
-- =============================================================================

-- Active ads view (most commonly queried)
CREATE OR REPLACE VIEW active_ads AS
SELECT * FROM ads
WHERE active = true AND archived = false;

-- Featured ads view
CREATE OR REPLACE VIEW featured_ads AS
SELECT * FROM ads
WHERE active = true AND featured = true AND archived = false;

-- Boat ads with specifications (for search)
CREATE OR REPLACE VIEW boat_ads_with_specs AS
SELECT
    a.*,
    bs.boat_type,
    bs.brand,
    bs.model,
    bs.horsepower,
    bs.length,
    bs.year,
    bs.boat_purpose,
    bs.water_type,
    bs.engine_hours,
    bs.located_in_bulgaria
FROM ads a
JOIN boat_specifications bs ON a.id = bs.ad_id
WHERE a.active = true AND a.category = 'BOATS_AND_YACHTS';

-- Recent ads view (last 30 days) - using a simple filter without index
CREATE OR REPLACE VIEW recent_ads AS
SELECT * FROM ads
WHERE active = true
ORDER BY created_at DESC
LIMIT 1000;

-- =============================================================================
-- HELPFUL FUNCTIONS
-- =============================================================================

-- Function to get ad with all specifications
CREATE OR REPLACE FUNCTION get_ad_with_specs(ad_id_param BIGINT)
RETURNS JSON AS $$
DECLARE
    result JSON;
    ad_record ads%ROWTYPE;
BEGIN
    -- Get the main ad
    SELECT * INTO ad_record FROM ads WHERE id = ad_id_param;

    IF NOT FOUND THEN
        RETURN NULL;
    END IF;

    -- Build JSON result with all specifications
    SELECT json_build_object(
        'ad', row_to_json(ad_record),
        'boat_specs', (SELECT row_to_json(bs) FROM boat_specifications bs WHERE bs.ad_id = ad_id_param),
        'jetski_specs', (SELECT row_to_json(js) FROM jetski_specifications js WHERE js.ad_id = ad_id_param),
        'trailer_specs', (SELECT row_to_json(ts) FROM trailer_specifications ts WHERE ts.ad_id = ad_id_param),
        'engine_specs', (SELECT row_to_json(es) FROM engine_specifications es WHERE es.ad_id = ad_id_param),
        'marine_electronics_specs', (SELECT row_to_json(mes) FROM marine_electronics_specifications mes WHERE mes.ad_id = ad_id_param),
        'fishing_specs', (SELECT row_to_json(fs) FROM fishing_specifications fs WHERE fs.ad_id = ad_id_param),
        'water_sports_specs', (SELECT row_to_json(wss) FROM water_sports_specifications wss WHERE wss.ad_id = ad_id_param),
        'marine_accessories_specs', (SELECT row_to_json(mas) FROM marine_accessories_specifications mas WHERE mas.ad_id = ad_id_param),
        'parts_specs', (SELECT row_to_json(ps) FROM parts_specifications ps WHERE ps.ad_id = ad_id_param),
        'services_specs', (SELECT row_to_json(ss) FROM services_specifications ss WHERE ss.ad_id = ad_id_param),
        'rentals_specs', (SELECT row_to_json(rs) FROM rentals_specifications rs WHERE rs.ad_id = ad_id_param),
        'images', (SELECT json_agg(row_to_json(ai)) FROM ad_images ai WHERE ai.ad_id = ad_id_param AND ai.active = true ORDER BY ai.display_order)
    ) INTO result;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Function to update ad views count safely
CREATE OR REPLACE FUNCTION increment_ad_views(ad_id_param BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE ads
    SET views_count = views_count + 1
    WHERE id = ad_id_param AND active = true;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- STATISTICS VIEWS
-- =============================================================================

-- Category statistics view
CREATE OR REPLACE VIEW category_stats AS
SELECT
    category,
    COUNT(*) as total_ads,
    COUNT(*) FILTER (WHERE active = true) as active_ads,
    COUNT(*) FILTER (WHERE featured = true AND active = true) as featured_ads,
    AVG(price_amount) FILTER (WHERE price_type = 'FIXED_PRICE' AND active = true) as avg_price,
    MIN(price_amount) FILTER (WHERE price_type = 'FIXED_PRICE' AND active = true) as min_price,
    MAX(price_amount) FILTER (WHERE price_type = 'FIXED_PRICE' AND active = true) as max_price
FROM ads
GROUP BY category;

-- Brand popularity view (for motor boats)
CREATE OR REPLACE VIEW motor_boat_brand_stats AS
SELECT
    bs.brand,
    COUNT(*) as total_ads,
    COUNT(*) FILTER (WHERE a.active = true) as active_ads,
    AVG(a.price_amount) FILTER (WHERE a.price_type = 'FIXED_PRICE' AND a.active = true) as avg_price
FROM boat_specifications bs
JOIN ads a ON bs.ad_id = a.id
WHERE a.category = 'BOATS_AND_YACHTS'
GROUP BY bs.brand
ORDER BY active_ads DESC;

-- =============================================================================
-- ADDITIONAL OPTIMIZATIONS (SAFE INDEXES ONLY)
-- =============================================================================

-- Partial index for expensive boats (using fixed value instead of function)
CREATE INDEX IF NOT EXISTS idx_ads_expensive_boats
ON ads(category, price_amount DESC)
WHERE price_type = 'FIXED_PRICE'
  AND price_amount > 50000
  AND active = true;

-- Partial index for featured ads only
CREATE INDEX IF NOT EXISTS idx_ads_featured_active
ON ads(category, created_at DESC)
WHERE active = true AND featured = true;

-- Partial index for boat search by recent years (using fixed year instead of function)
CREATE INDEX IF NOT EXISTS idx_boat_specs_recent_year
ON boat_specifications(year DESC, brand)
WHERE year >= 2010;

-- =============================================================================
-- HELPFUL MAINTENANCE FUNCTIONS
-- =============================================================================

-- Clean up orphaned images
CREATE OR REPLACE FUNCTION cleanup_orphaned_images()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    UPDATE ad_images
    SET active = false
    WHERE ad_id NOT IN (SELECT id FROM ads WHERE active = true);

    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Archive old inactive ads (with parameter for days)
CREATE OR REPLACE FUNCTION archive_old_inactive_ads(days_old INTEGER DEFAULT 90)
RETURNS INTEGER AS $$
DECLARE
    archived_count INTEGER;
    cutoff_date DATE;
BEGIN
    cutoff_date := CURRENT_DATE - INTERVAL '1 day' * days_old;

    UPDATE ads
    SET archived = true, archived_at = CURRENT_TIMESTAMP
    WHERE active = false
      AND archived = false
      AND updated_at < cutoff_date;

    GET DIAGNOSTICS archived_count = ROW_COUNT;
    RETURN archived_count;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- TABLE COMMENTS FOR DOCUMENTATION
-- =============================================================================

COMMENT ON TABLE ads IS 'Main table for all marketplace advertisements across all categories';
COMMENT ON TABLE brands IS 'Validated brands for each category to ensure data consistency';
COMMENT ON TABLE boat_specifications IS 'Detailed specifications for boats and yachts';
COMMENT ON TABLE jetski_specifications IS 'Detailed specifications for jet skis and personal watercraft';
COMMENT ON TABLE trailer_specifications IS 'Detailed specifications for boat and jet ski trailers';
COMMENT ON TABLE engine_specifications IS 'Detailed specifications for marine engines';
COMMENT ON TABLE marine_electronics_specifications IS 'Specifications for marine electronic equipment';
COMMENT ON TABLE fishing_specifications IS 'Specifications for fishing equipment and gear';
COMMENT ON TABLE water_sports_specifications IS 'Specifications for water sports equipment';
COMMENT ON TABLE marine_accessories_specifications IS 'Specifications for marine accessories and parts';
COMMENT ON TABLE parts_specifications IS 'Specifications for marine parts and components';
COMMENT ON TABLE services_specifications IS 'Specifications for marine services and companies';
COMMENT ON TABLE rentals_specifications IS 'Specifications for boat and yacht rental services';
COMMENT ON TABLE ad_images IS 'Image management for advertisements with S3 integration';

-- Column comments for key fields
COMMENT ON COLUMN ads.quick_description IS 'Optional short description for search results and previews';
COMMENT ON COLUMN ads.price_type IS 'Type of pricing: FIXED_PRICE, FREE, NEGOTIABLE, or BARTER';
COMMENT ON COLUMN ads.approval_status IS 'Moderation status of the advertisement';
COMMENT ON COLUMN boat_specifications.boat_purpose IS 'Primary intended use of the boat';
COMMENT ON COLUMN boat_specifications.water_type IS 'Type of water the boat is designed for';
COMMENT ON COLUMN boat_specifications.located_in_bulgaria IS 'Whether the boat is currently located in Bulgaria';

-- =============================================================================
-- FINAL OPTIMIZATIONS
-- =============================================================================

-- Update table statistics for query planner
ANALYZE brands;
ANALYZE ads;
ANALYZE boat_specifications;
ANALYZE jetski_specifications;
ANALYZE trailer_specifications;
ANALYZE engine_specifications;
ANALYZE marine_electronics_specifications;
ANALYZE fishing_specifications;
ANALYZE water_sports_specifications;
ANALYZE marine_accessories_specifications;
ANALYZE parts_specifications;
ANALYZE services_specifications;
ANALYZE rentals_specifications;
ANALYZE ad_images;