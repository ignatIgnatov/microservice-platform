-- V14__Fix_data_constraints_and_defaults.sql (FIXED)
-- Fix data constraints, add missing fields, and set proper defaults

-- 1. Update existing boat specifications to have default values for new fields
UPDATE boat_specifications
SET
    boat_purpose = 'ALL',
    water_type = 'ALL',
    engine_hours = 0,
    located_in_bulgaria = true
WHERE
    boat_purpose IS NULL
    OR water_type IS NULL
    OR engine_hours IS NULL
    OR located_in_bulgaria IS NULL;

-- 2. Make the new required fields NOT NULL after setting defaults (separate statements)
ALTER TABLE boat_specifications ALTER COLUMN boat_purpose SET NOT NULL;
ALTER TABLE boat_specifications ALTER COLUMN water_type SET NOT NULL;
ALTER TABLE boat_specifications ALTER COLUMN engine_hours SET NOT NULL;
ALTER TABLE boat_specifications ALTER COLUMN located_in_bulgaria SET NOT NULL;

-- 3. Add missing timestamps to specification tables that don't have them
ALTER TABLE boat_specifications
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE jetski_specifications
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE trailer_specifications
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE engine_specifications
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- 4. Add missing unique constraints for one-to-one relationships (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'unique_marine_electronics_spec_per_ad') THEN
        ALTER TABLE marine_electronics_specifications
            ADD CONSTRAINT unique_marine_electronics_spec_per_ad UNIQUE(ad_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'unique_fishing_spec_per_ad') THEN
        ALTER TABLE fishing_specifications
            ADD CONSTRAINT unique_fishing_spec_per_ad UNIQUE(ad_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'unique_parts_spec_per_ad') THEN
        ALTER TABLE parts_specifications
            ADD CONSTRAINT unique_parts_spec_per_ad UNIQUE(ad_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'unique_services_spec_per_ad') THEN
        ALTER TABLE services_specifications
            ADD CONSTRAINT unique_services_spec_per_ad UNIQUE(ad_id);
    END IF;
END $$;

-- 5. Add proper validation for price fields (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'valid_price_amount_positive') THEN
        ALTER TABLE ads
            ADD CONSTRAINT valid_price_amount_positive
            CHECK (price_amount IS NULL OR price_amount >= 0);
    END IF;
END $$;

-- 6. Update brand display_order to be NOT NULL with default (if not already set)
UPDATE brands SET display_order = 0 WHERE display_order IS NULL;
ALTER TABLE brands ALTER COLUMN display_order SET NOT NULL;
ALTER TABLE brands ALTER COLUMN display_order SET DEFAULT 0;

-- 7. Add indexes for new query patterns (if not exists)
CREATE INDEX IF NOT EXISTS idx_ads_category_price_type ON ads(category, price_type);
CREATE INDEX IF NOT EXISTS idx_ads_category_price_amount ON ads(category, price_amount) WHERE price_type = 'FIXED_PRICE';

-- 8. Add partial indexes for active records only (if not exists)
CREATE INDEX IF NOT EXISTS idx_active_ads_category_created ON ads(category, created_at DESC) WHERE active = true;
CREATE INDEX IF NOT EXISTS idx_active_ads_location_created ON ads(location, created_at DESC) WHERE active = true;

-- 9. Create triggers for updating updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to all specification tables (drop first if exists)
DROP TRIGGER IF EXISTS update_boat_specifications_updated_at ON boat_specifications;
CREATE TRIGGER update_boat_specifications_updated_at
    BEFORE UPDATE ON boat_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_jetski_specifications_updated_at ON jetski_specifications;
CREATE TRIGGER update_jetski_specifications_updated_at
    BEFORE UPDATE ON jetski_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_trailer_specifications_updated_at ON trailer_specifications;
CREATE TRIGGER update_trailer_specifications_updated_at
    BEFORE UPDATE ON trailer_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_engine_specifications_updated_at ON engine_specifications;
CREATE TRIGGER update_engine_specifications_updated_at
    BEFORE UPDATE ON engine_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_marine_electronics_specifications_updated_at ON marine_electronics_specifications;
CREATE TRIGGER update_marine_electronics_specifications_updated_at
    BEFORE UPDATE ON marine_electronics_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_fishing_specifications_updated_at ON fishing_specifications;
CREATE TRIGGER update_fishing_specifications_updated_at
    BEFORE UPDATE ON fishing_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_parts_specifications_updated_at ON parts_specifications;
CREATE TRIGGER update_parts_specifications_updated_at
    BEFORE UPDATE ON parts_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_services_specifications_updated_at ON services_specifications;
CREATE TRIGGER update_services_specifications_updated_at
    BEFORE UPDATE ON services_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_water_sports_specifications_updated_at ON water_sports_specifications;
CREATE TRIGGER update_water_sports_specifications_updated_at
    BEFORE UPDATE ON water_sports_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_marine_accessories_specifications_updated_at ON marine_accessories_specifications;
CREATE TRIGGER update_marine_accessories_specifications_updated_at
    BEFORE UPDATE ON marine_accessories_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_rentals_specifications_updated_at ON rentals_specifications;
CREATE TRIGGER update_rentals_specifications_updated_at
    BEFORE UPDATE ON rentals_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();