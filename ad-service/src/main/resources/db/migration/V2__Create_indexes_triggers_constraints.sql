-- V2__Create_indexes_triggers_constraints.sql
-- All indexes, triggers, and additional constraints

-- =============================================================================
-- BRANDS TABLE INDEXES
-- =============================================================================
CREATE INDEX idx_brands_category ON brands(category);
CREATE INDEX idx_brands_active ON brands(active);
CREATE INDEX idx_brands_category_active ON brands(category, active, display_order) WHERE active = true;
CREATE INDEX idx_brands_display_order ON brands(category, display_order);
CREATE INDEX idx_brands_name ON brands(name);

-- =============================================================================
-- ADS TABLE INDEXES
-- =============================================================================
-- Basic indexes
CREATE INDEX idx_ads_category ON ads(category);
CREATE INDEX idx_ads_user_id ON ads(user_id);
CREATE INDEX idx_ads_user_email ON ads(user_email);
CREATE INDEX idx_ads_active ON ads(active);
CREATE INDEX idx_ads_created_at ON ads(created_at DESC);
CREATE INDEX idx_ads_updated_at ON ads(updated_at DESC);
CREATE INDEX idx_ads_contact_phone ON ads(contact_phone);
CREATE INDEX idx_ads_contact_person_name ON ads(contact_person_name);

-- Performance indexes
CREATE INDEX idx_ads_price_type ON ads(price_type);
CREATE INDEX idx_ads_price_amount ON ads(price_amount) WHERE price_type = 'FIXED_PRICE';
CREATE INDEX idx_ads_location ON ads(location);
CREATE INDEX idx_ads_views_count ON ads(views_count DESC);
CREATE INDEX idx_ads_featured ON ads(featured);
CREATE INDEX idx_ads_archived ON ads(archived);
CREATE INDEX idx_ads_approval_status ON ads(approval_status);

-- Composite indexes for common queries
CREATE INDEX idx_ads_category_active_created ON ads(category, active, created_at DESC);
CREATE INDEX idx_ads_location_active_created ON ads(location, active, created_at DESC);
CREATE INDEX idx_ads_user_active_created ON ads(user_email, active, created_at DESC);
CREATE INDEX idx_ads_category_location ON ads(category, location) WHERE active = true;
CREATE INDEX idx_ads_category_price ON ads(category, price_amount) WHERE active = true AND price_type = 'FIXED_PRICE';
CREATE INDEX idx_ads_user_category ON ads(user_id, category) WHERE active = true;

-- Text search index
CREATE INDEX idx_ads_text_search ON ads USING gin(to_tsvector('english', title || ' ' || description));

-- =============================================================================
-- BOAT SPECIFICATIONS INDEXES
-- =============================================================================
CREATE INDEX idx_boat_specs_ad_id ON boat_specifications(ad_id);
CREATE INDEX idx_boat_specs_brand ON boat_specifications(brand);
CREATE INDEX idx_boat_specs_boat_type ON boat_specifications(boat_type);
CREATE INDEX idx_boat_specs_year ON boat_specifications(year);
CREATE INDEX idx_boat_specs_horsepower ON boat_specifications(horsepower);
CREATE INDEX idx_boat_specs_length ON boat_specifications(length);
CREATE INDEX idx_boat_specs_boat_purpose ON boat_specifications(boat_purpose);
CREATE INDEX idx_boat_specs_water_type ON boat_specifications(water_type);
CREATE INDEX idx_boat_specs_engine_hours ON boat_specifications(engine_hours);
CREATE INDEX idx_boat_specs_located_in_bulgaria ON boat_specifications(located_in_bulgaria);

-- Composite indexes for complex queries
CREATE INDEX idx_boat_specs_basic ON boat_specifications(boat_type, horsepower, year);
CREATE INDEX idx_boat_specs_search ON boat_specifications(brand, boat_type, year) WHERE boat_type != 'ALL';

-- Feature table indexes
CREATE INDEX idx_boat_interior_features_boat_spec_id ON boat_interior_features(boat_spec_id);
CREATE INDEX idx_boat_exterior_features_boat_spec_id ON boat_exterior_features(boat_spec_id);
CREATE INDEX idx_boat_equipment_boat_spec_id ON boat_equipment(boat_spec_id);

-- =============================================================================
-- JETSKI SPECIFICATIONS INDEXES
-- =============================================================================
CREATE INDEX idx_jetski_specs_ad_id ON jetski_specifications(ad_id);
CREATE INDEX idx_jetski_specs_brand ON jetski_specifications(brand);
CREATE INDEX idx_jetski_specs_year ON jetski_specifications(year);
CREATE INDEX idx_jetski_specs_horsepower ON jetski_specifications(horsepower);
CREATE INDEX idx_jetski_specs_basic ON jetski_specifications(brand, year);

-- =============================================================================
-- TRAILER SPECIFICATIONS INDEXES
-- =============================================================================
CREATE INDEX idx_trailer_specs_ad_id ON trailer_specifications(ad_id);
CREATE INDEX idx_trailer_specs_brand ON trailer_specifications(brand);
CREATE INDEX idx_trailer_specs_load_capacity ON trailer_specifications(load_capacity);
CREATE INDEX idx_trailer_specs_basic ON trailer_specifications(brand, year);
CREATE INDEX idx_trailer_features_spec_id ON trailer_specifications_features(trailer_spec_id);

-- =============================================================================
-- ENGINE SPECIFICATIONS INDEXES
-- =============================================================================
CREATE INDEX idx_engine_specs_ad_id ON engine_specifications(ad_id);
CREATE INDEX idx_engine_specs_brand ON engine_specifications(brand);
CREATE INDEX idx_engine_specs_horsepower ON engine_specifications(horsepower);
CREATE INDEX idx_engine_specs_year ON engine_specifications(year);
CREATE INDEX idx_engine_specs_basic ON engine_specifications(brand, horsepower, year);

-- =============================================================================
-- OTHER SPECIFICATION INDEXES
-- =============================================================================

-- Marine electronics
CREATE INDEX idx_marine_electronics_specs_ad_id ON marine_electronics_specifications(ad_id);
CREATE INDEX idx_marine_electronics_specs_electronics_type ON marine_electronics_specifications(electronics_type);
CREATE INDEX idx_marine_electronics_specs_brand ON marine_electronics_specifications(brand);

-- Fishing
CREATE INDEX idx_fishing_specs_ad_id ON fishing_specifications(ad_id);
CREATE INDEX idx_fishing_specs_fishing_type ON fishing_specifications(fishing_type);
CREATE INDEX idx_fishing_specs_fishing_technique ON fishing_specifications(fishing_technique);
CREATE INDEX idx_fishing_specs_target_fish ON fishing_specifications(target_fish);

-- Water sports
CREATE INDEX idx_water_sports_specs_ad_id ON water_sports_specifications(ad_id);
CREATE INDEX idx_water_sports_specs_type ON water_sports_specifications(water_sports_type);
CREATE INDEX idx_water_sports_specs_brand ON water_sports_specifications(brand);

-- Marine accessories
CREATE INDEX idx_marine_accessories_specs_ad_id ON marine_accessories_specifications(ad_id);
CREATE INDEX idx_marine_accessories_specs_type ON marine_accessories_specifications(accessory_type);
CREATE INDEX idx_marine_accessories_specs_brand ON marine_accessories_specifications(brand);

-- Parts
CREATE INDEX idx_parts_specs_ad_id ON parts_specifications(ad_id);
CREATE INDEX idx_parts_specs_part_type ON parts_specifications(part_type);

-- Services
CREATE INDEX idx_services_specs_ad_id ON services_specifications(ad_id);
CREATE INDEX idx_services_specs_service_type ON services_specifications(service_type);
CREATE INDEX idx_services_specs_company_name ON services_specifications(company_name);

-- Rentals
CREATE INDEX idx_rentals_specs_ad_id ON rentals_specifications(ad_id);
CREATE INDEX idx_rentals_specs_rental_type ON rentals_specifications(rental_type);
CREATE INDEX idx_rentals_specs_company_name ON rentals_specifications(company_name);
CREATE INDEX idx_rentals_specs_management_type ON rentals_specifications(management_type);
CREATE INDEX idx_rentals_specs_max_price ON rentals_specifications(max_price);

-- =============================================================================
-- AD IMAGES INDEXES
-- =============================================================================
CREATE INDEX idx_ad_images_ad_id ON ad_images(ad_id);
CREATE INDEX idx_ad_images_display_order ON ad_images(ad_id, display_order);
CREATE INDEX idx_ad_images_uploaded_by ON ad_images(uploaded_by);
CREATE INDEX idx_ad_images_active ON ad_images(active);
CREATE INDEX idx_ad_images_max_display_order ON ad_images(ad_id, display_order DESC) WHERE active = true;
CREATE INDEX idx_ad_images_user_ad ON ad_images(uploaded_by, ad_id) WHERE active = true;

-- =============================================================================
-- TRIGGERS FOR UPDATED_AT COLUMNS
-- =============================================================================

-- Create the trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to all tables with updated_at
CREATE TRIGGER update_brands_updated_at
    BEFORE UPDATE ON brands
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ads_updated_at
    BEFORE UPDATE ON ads
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_boat_specifications_updated_at
    BEFORE UPDATE ON boat_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_jetski_specifications_updated_at
    BEFORE UPDATE ON jetski_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_trailer_specifications_updated_at
    BEFORE UPDATE ON trailer_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_engine_specifications_updated_at
    BEFORE UPDATE ON engine_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_marine_electronics_specifications_updated_at
    BEFORE UPDATE ON marine_electronics_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_fishing_specifications_updated_at
    BEFORE UPDATE ON fishing_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_water_sports_specifications_updated_at
    BEFORE UPDATE ON water_sports_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_marine_accessories_specifications_updated_at
    BEFORE UPDATE ON marine_accessories_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_parts_specifications_updated_at
    BEFORE UPDATE ON parts_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_services_specifications_updated_at
    BEFORE UPDATE ON services_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_rentals_specifications_updated_at
    BEFORE UPDATE ON rentals_specifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================================
-- VALIDATION TRIGGERS
-- =============================================================================

-- Basic boat validation trigger
CREATE OR REPLACE FUNCTION validate_boat_basic()
RETURNS TRIGGER AS $$
BEGIN
    -- Engine details required if engine included
    IF NEW.engine_included = true AND NEW.horsepower IS NULL THEN
        RAISE EXCEPTION 'Horsepower is required when engine is included';
    END IF;

    -- Reasonable proportions
    IF NEW.length IS NOT NULL AND NEW.width IS NOT NULL AND NEW.width > NEW.length THEN
        RAISE EXCEPTION 'Width cannot be greater than length';
    END IF;

    -- Engine hours validation
    IF NEW.engine_included = true AND NEW.engine_hours < 0 THEN
        RAISE EXCEPTION 'Engine hours cannot be negative';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER validate_boat_basic_trigger
    BEFORE INSERT OR UPDATE ON boat_specifications
    FOR EACH ROW EXECUTE FUNCTION validate_boat_basic();