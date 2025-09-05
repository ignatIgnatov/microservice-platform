-- V12__Create_missing_specification_tables.sql
-- Create specification tables for Water Sports, Marine Accessories, and Rentals

-- Water Sports Specifications Table
CREATE TABLE water_sports_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    water_sports_type VARCHAR(50) NOT NULL CHECK (water_sports_type IN (
        'ALL', 'SURF', 'SUP', 'DIVING', 'WATER_SKIING', 'PARAGLIDING',
        'WAKEBOARD', 'WINDSURF', 'JETPACK', 'FLYBOARD', 'SAILING',
        'ROWING', 'WATER_SPORTS_CLOTHING'
    )),
    brand VARCHAR(100),
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_water_sports_spec_per_ad UNIQUE(ad_id)
);

-- Marine Accessories Specifications Table
CREATE TABLE marine_accessories_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    accessory_type VARCHAR(50) NOT NULL CHECK (accessory_type IN (
        'ALL', 'LIFE_JACKETS', 'COSMETICS_CHEMICALS', 'HYDROFOIL', 'FENDERS_BUOYS',
        'OARS', 'COOLER_BAGS', 'INSTRUMENTS', 'COMPASSES', 'COVERS', 'TARPS',
        'KNIVES', 'ANCHORS', 'ROPES', 'CONSOLES', 'SEATS_CUSHIONS', 'OTHER'
    )),
    brand VARCHAR(100),
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_marine_accessories_spec_per_ad UNIQUE(ad_id)
);

-- Rentals Specifications Table
CREATE TABLE rentals_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    rental_type VARCHAR(50) NOT NULL CHECK (rental_type IN (
        'ALL', 'MOTOR_BOAT', 'MOTOR_YACHT', 'CATAMARAN',
        'SAILING_BOAT_YACHT', 'JET', 'WATER_SPORTS'
    )),
    license_required BOOLEAN NOT NULL,
    management_type VARCHAR(30) NOT NULL CHECK (management_type IN ('ALL', 'SELF_DRIVE', 'WITH_CAPTAIN')),
    number_of_people INTEGER NOT NULL CHECK (number_of_people > 0),
    service_type VARCHAR(30) NOT NULL CHECK (service_type IN ('ALL', 'SHARED_TRIP', 'PRIVATE_RENTAL')),
    company_name VARCHAR(200) NOT NULL,
    description TEXT CHECK (description IS NULL OR LENGTH(description) <= 2000),
    contact_phone VARCHAR(20) NOT NULL,
    contact_email VARCHAR(100),
    address VARCHAR(500),
    website VARCHAR(200),
    max_price DECIMAL(10,2) NOT NULL CHECK (max_price >= 0),
    price_specification VARCHAR(200) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_rentals_spec_per_ad UNIQUE(ad_id)
);

-- Create indexes for new specification tables
CREATE INDEX idx_water_sports_specs_ad_id ON water_sports_specifications(ad_id);
CREATE INDEX idx_water_sports_specs_type ON water_sports_specifications(water_sports_type);
CREATE INDEX idx_water_sports_specs_brand ON water_sports_specifications(brand);

CREATE INDEX idx_marine_accessories_specs_ad_id ON marine_accessories_specifications(ad_id);
CREATE INDEX idx_marine_accessories_specs_type ON marine_accessories_specifications(accessory_type);
CREATE INDEX idx_marine_accessories_specs_brand ON marine_accessories_specifications(brand);

CREATE INDEX idx_rentals_specs_ad_id ON rentals_specifications(ad_id);
CREATE INDEX idx_rentals_specs_rental_type ON rentals_specifications(rental_type);
CREATE INDEX idx_rentals_specs_company_name ON rentals_specifications(company_name);
CREATE INDEX idx_rentals_specs_management_type ON rentals_specifications(management_type);
CREATE INDEX idx_rentals_specs_max_price ON rentals_specifications(max_price);