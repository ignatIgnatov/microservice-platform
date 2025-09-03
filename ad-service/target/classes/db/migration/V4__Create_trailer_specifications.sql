-- V4__Create_trailer_specifications.sql
-- Trailer specifications table

CREATE TABLE trailer_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    trailer_type VARCHAR(50) NOT NULL CHECK (trailer_type IN ('ALL', 'JET_TRAILER', 'BOAT_TRAILER')),
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    axle_count VARCHAR(20) NOT NULL CHECK (axle_count IN ('ALL', 'SINGLE', 'DOUBLE', 'TRIPLE')),
    is_registered BOOLEAN NOT NULL,
    own_weight DECIMAL(8,2) CHECK (own_weight >= 0),
    load_capacity DECIMAL(8,2) NOT NULL CHECK (load_capacity > 0),
    length DECIMAL(5,2) NOT NULL CHECK (length > 0),
    width DECIMAL(5,2) NOT NULL CHECK (width > 0),
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5),
    suspension_type VARCHAR(50) CHECK (suspension_type IN ('DOUBLE_TORSION', 'TORSION', 'LEAF_SPRING', 'RIGID')),
    keel_rollers VARCHAR(30) CHECK (keel_rollers IN ('ALL', 'TWO_ROLLERS', 'THREE_ROLLERS', 'FOUR_ROLLERS', 'FIVE_ROLLERS', 'MULTIPLE_ROLLERS')),
    in_warranty BOOLEAN NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    CONSTRAINT unique_trailer_spec_per_ad UNIQUE(ad_id)
);

CREATE TABLE trailer_specifications_features (
    id BIGSERIAL PRIMARY KEY,
    trailer_spec_id BIGINT NOT NULL REFERENCES trailer_specifications(id) ON DELETE CASCADE,
    feature VARCHAR(100) NOT NULL,

    CONSTRAINT unique_trailer_feature UNIQUE(trailer_spec_id, feature)
);

-- Indexes for trailer specifications
CREATE INDEX idx_trailer_specs_ad_id ON trailer_specifications(ad_id);
CREATE INDEX idx_trailer_specs_brand ON trailer_specifications(brand);
CREATE INDEX idx_trailer_specs_load_capacity ON trailer_specifications(load_capacity);