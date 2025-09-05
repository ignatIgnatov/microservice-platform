-- V11__Update_categories_and_boat_types.sql (FIXED)
-- Update main categories and boat types to match specification

-- 1. Add new main categories to ads table
ALTER TABLE ads DROP CONSTRAINT IF EXISTS ads_category_check;
ALTER TABLE ads ADD CONSTRAINT ads_category_check
CHECK (category IN (
    'BOATS_AND_YACHTS',
    'JET_SKIS',
    'TRAILERS',
    'MARINE_ELECTRONICS',
    'ENGINES',
    'FISHING',
    'WATER_SPORTS',        -- NEW
    'PARTS',
    'MARINE_ACCESSORIES',  -- NEW
    'SERVICES',
    'RENTALS'              -- NEW
));

-- 2. Update boat types to match specification
ALTER TABLE boat_specifications DROP CONSTRAINT IF EXISTS boat_specifications_boat_type_check;
ALTER TABLE boat_specifications ADD CONSTRAINT boat_specifications_boat_type_check
CHECK (boat_type IN (
    'ALL',
    'MOTOR_BOAT',
    'MOTOR_YACHT',         -- NEW
    'SAILING_BOAT',
    'SAILING_YACHT',       -- NEW
    'INFLATABLE_BOAT',     -- NEW
    'SHIP',                -- NEW
    'CANOE',               -- NEW (was KAYAK_CANOE)
    'PONTOON'              -- NEW
));

-- 3. Add missing fields to boat_specifications (add columns first)
ALTER TABLE boat_specifications
    ADD COLUMN IF NOT EXISTS boat_purpose VARCHAR(20),
    ADD COLUMN IF NOT EXISTS water_type VARCHAR(20),
    ADD COLUMN IF NOT EXISTS engine_hours INTEGER,
    ADD COLUMN IF NOT EXISTS located_in_bulgaria BOOLEAN DEFAULT true;

-- 4. Add constraints for new fields (separate statements for better compatibility)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'boat_specifications_boat_purpose_check') THEN
        ALTER TABLE boat_specifications
            ADD CONSTRAINT boat_specifications_boat_purpose_check
            CHECK (boat_purpose IN ('ALL', 'FISHING', 'BEACH', 'WATER_SPORTS', 'WORK'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'boat_specifications_water_type_check') THEN
        ALTER TABLE boat_specifications
            ADD CONSTRAINT boat_specifications_water_type_check
            CHECK (water_type IN ('ALL', 'FRESHWATER', 'SALTWATER'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'boat_specifications_engine_hours_check') THEN
        ALTER TABLE boat_specifications
            ADD CONSTRAINT boat_specifications_engine_hours_check
            CHECK (engine_hours >= 0);
    END IF;
END $$;

-- 5. Add quick_description constraint that was missing (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ads_quick_description_check') THEN
        ALTER TABLE ads ADD CONSTRAINT ads_quick_description_check
        CHECK (quick_description IS NULL OR (LENGTH(quick_description) >= 20 AND LENGTH(quick_description) <= 210));
    END IF;
END $$;

-- 6. Update brand categories to include new categories
ALTER TABLE brands DROP CONSTRAINT IF EXISTS brands_category_check;
ALTER TABLE brands ADD CONSTRAINT brands_category_check
CHECK (category IN (
    'MOTOR_BOATS',
    'SAILBOATS',
    'KAYAKS',
    'JET_SKIS',            -- NEW
    'TRAILERS',            -- NEW
    'MARINE_ELECTRONICS',  -- NEW
    'ENGINES',             -- NEW
    'FISHING',             -- NEW
    'WATER_SPORTS',        -- NEW
    'MARINE_ACCESSORIES',  -- NEW
    'PARTS',               -- NEW
    'SERVICES'             -- NEW
));

-- 7. Create indexes for new fields (if not exists)
CREATE INDEX IF NOT EXISTS idx_boat_specs_boat_purpose ON boat_specifications(boat_purpose);
CREATE INDEX IF NOT EXISTS idx_boat_specs_water_type ON boat_specifications(water_type);
CREATE INDEX IF NOT EXISTS idx_boat_specs_engine_hours ON boat_specifications(engine_hours);
CREATE INDEX IF NOT EXISTS idx_boat_specs_located_in_bulgaria ON boat_specifications(located_in_bulgaria);