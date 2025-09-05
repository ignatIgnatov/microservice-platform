-- V15__Final_optimizations_and_validations.sql (SIMPLE VERSION)
-- Basic optimizations only - no complex views or functions

-- 1. Create basic performance indexes
CREATE INDEX IF NOT EXISTS idx_ads_search_basic ON ads(category, active, created_at DESC) WHERE active = true;
CREATE INDEX IF NOT EXISTS idx_boat_specs_basic ON boat_specifications(boat_type, horsepower, year);
CREATE INDEX IF NOT EXISTS idx_jetski_specs_basic ON jetski_specifications(brand, year);
CREATE INDEX IF NOT EXISTS idx_engine_specs_basic ON engine_specifications(brand, horsepower, year);
CREATE INDEX IF NOT EXISTS idx_trailer_specs_basic ON trailer_specifications(brand, year);

-- 2. Create indexes for new specification tables
CREATE INDEX IF NOT EXISTS idx_water_sports_specs_ad_id ON water_sports_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_water_sports_specs_type ON water_sports_specifications(water_sports_type);

CREATE INDEX IF NOT EXISTS idx_marine_accessories_specs_ad_id ON marine_accessories_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_marine_accessories_specs_type ON marine_accessories_specifications(accessory_type);

CREATE INDEX IF NOT EXISTS idx_rentals_specs_ad_id ON rentals_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_rentals_specs_type ON rentals_specifications(rental_type);

-- 3. Add search optimization indexes
CREATE INDEX IF NOT EXISTS idx_ads_category_location ON ads(category, location) WHERE active = true;
CREATE INDEX IF NOT EXISTS idx_ads_category_price ON ads(category, price_amount) WHERE active = true AND price_type = 'FIXED_PRICE';
CREATE INDEX IF NOT EXISTS idx_ads_user_category ON ads(user_id, category) WHERE active = true;

-- 4. Add basic validation trigger for boat specifications
CREATE OR REPLACE FUNCTION validate_boat_basic()
RETURNS TRIGGER AS $$
BEGIN
    -- Basic validation: engine details required if engine included
    IF NEW.engine_included = true AND NEW.horsepower IS NULL THEN
        RAISE EXCEPTION 'Horsepower is required when engine is included';
    END IF;

    -- Basic validation: reasonable proportions
    IF NEW.length IS NOT NULL AND NEW.width IS NOT NULL AND NEW.width > NEW.length THEN
        RAISE EXCEPTION 'Width cannot be greater than length';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS validate_boat_basic_trigger ON boat_specifications;
CREATE TRIGGER validate_boat_basic_trigger
    BEFORE INSERT OR UPDATE ON boat_specifications
    FOR EACH ROW EXECUTE FUNCTION validate_boat_basic();

-- 5. Add helpful comments
COMMENT ON TABLE ads IS 'Main table for all marketplace ads across all categories';
COMMENT ON TABLE boat_specifications IS 'Detailed specifications for boats and yachts';
COMMENT ON TABLE jetski_specifications IS 'Detailed specifications for jet skis';
COMMENT ON TABLE engine_specifications IS 'Detailed specifications for marine engines';
COMMENT ON TABLE water_sports_specifications IS 'Specifications for water sports equipment';
COMMENT ON TABLE marine_accessories_specifications IS 'Specifications for marine accessories';
COMMENT ON TABLE rentals_specifications IS 'Specifications for boat/yacht rental services';

-- 6. Basic brand indexes
CREATE INDEX IF NOT EXISTS idx_brands_category_active ON brands(category, active, display_order) WHERE active = true;

-- End of migration